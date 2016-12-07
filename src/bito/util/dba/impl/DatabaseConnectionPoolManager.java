package bito.util.dba.impl;

import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import bito.util.logger.Log;

public class DatabaseConnectionPoolManager
{
	private static HashMap drivers = new HashMap();
	private static HashMap pools = new HashMap();
	// current pool
	private Pool pool;
	private Log log = new Log("");
	private ThreadLocal<Log> tlLog = new ThreadLocal();
	private long check_dbconfig_time = System.currentTimeMillis() + 1000L;

	//
	public DatabaseConnectionPoolManager(DBConfig dbconfig)
	{
		pool = getPool(dbconfig);
	}

	public void refreshConfig()
	{
		if (pool.dbconfig.dbname != null && System.currentTimeMillis() > check_dbconfig_time)
		{
			DBConfig chk_dbconfig = new DBConfig(pool.dbconfig.dbname);
			if (!chk_dbconfig.equals(pool.dbconfig))
			{
				pool = getPool(chk_dbconfig);
			}
			check_dbconfig_time = System.currentTimeMillis() + 1000L;
		}
	}

	private Pool getPool(DBConfig dbconfig)
	{
		synchronized(pools)
		{
			Pool pool = (Pool)pools.get(dbconfig);
			if (pool == null)
			{
				pool = new Pool(dbconfig);
				pools.put(dbconfig, pool);
				log("Connection pool for db "
					+ dbconfig.dbname
						+ " "
						+ dbconfig.dburl
						+ " user:"
						+ dbconfig.dbuser
						+ " created.");
			}
			return pool;
		}
	}

	protected Driver RegisterDriver(String dbdriver)
	{
		if (dbdriver.length() == 0)
		{
			throw new RuntimeException("Not found special db config.");
		}
		try
		{
			ClassLoader loader = this.getClass().getClassLoader();
			HashMap loader_driver_map;
			synchronized(drivers)
			{
				loader_driver_map = (HashMap)drivers.get(dbdriver);
				if (loader_driver_map == null)
				{
					loader_driver_map = new HashMap();
					drivers.put(dbdriver, loader_driver_map);
				}
			}
			Driver driver = (Driver)loader_driver_map.get(loader);
			if (driver == null)
			{
				Class jdbc = Class.forName(dbdriver);
				driver = (Driver)jdbc.newInstance();
				DriverManager.registerDriver(driver);
				loader_driver_map.put(loader, driver);
			}
			return driver;
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public Connection getConnection() throws Exception
	{
		return pool.getConnection();
	}

	public void destroyConnection(Connection con)
	{
		pool.closeConnection(((ProxyConnection)con).getPhysicalConnection());
	}

	interface ProxyConnection extends Connection
	{
		Connection getPhysicalConnection();

		PooledConnection getPooledConnection();
	}

	class PooledConnection
	{
		ProxyConnection proxy_connection;
		Connection physical_connection;
		Pool pool;
		int synchronized_use_count = 0;
		long lastusetime;
	}

	class Pool
	{
		private ArrayList freeconn = new ArrayList();
		//
		// configuration
		final DBConfig dbconfig;
		//
		int conncount = 0;
		//
		int connfailedcount = 0;
		long allusedtime = System.currentTimeMillis();
		boolean username_password_error = false;

		public Pool(DBConfig dbconfig)
		{
			this.dbconfig = dbconfig;
		}

		private Connection newConnection() throws Exception
		{
			log("connect("
				+ (conncount + 1)
					+ ") to db "
					+ dbconfig.dbname
					+ " "
					+ dbconfig.dburl
					+ " user:"
					+ dbconfig.dbuser
					+ (connfailedcount > 0?", try again.":""));
			Connection con = null;
			try
			{
				if (username_password_error)
				{
					throw new Exception("Username or Password error, avoid trying again.");
				}
				Driver driver = RegisterDriver(dbconfig.dbdriver);
				con = (Connection)DriverManager.getConnection(dbconfig.dburl, dbconfig.dbuser, dbconfig.dbpswd);;
				PooledConnection pc = new PooledConnection();
				pc.physical_connection = con;
				pc.pool = this;
				InvocationHandler handler = new ConnectionProxy(pc);
				pc.proxy_connection = (ProxyConnection)Proxy.newProxyInstance(ProxyConnection.class.getClassLoader(),
					new Class[]{ProxyConnection.class},
					handler);
				conncount++;
				return pc.proxy_connection;
			}
			catch(Exception e)
			{
				if ((e instanceof SQLException)
					&& ((((SQLException)e).getSQLState().equals("28000")) || (((SQLException)e).getSQLState()
						.equals("08001"))))
				{
					username_password_error = true;
				}
				if (con != null)
				{
					try
					{
						con.close();
					}
					catch(SQLException se)
					{
					}
				}
				if (conncount == 0 && connfailedcount > 0)
				{
					throw e;
				}
				Log log = tlLog.get();
				if (log == null)
				{
					log = DatabaseConnectionPoolManager.this.log;
				}
				if (log != null)
				{
					log.error("connect failed : " + e.getMessage());
				}
				else
				{
					e.printStackTrace();
				}
				try
				{
					connfailedcount++;
					if (conncount == 0)
					{
						con = newConnection();
					}
					else
					{
						con = getConnection();
					}
					return con;
				}
				finally
				{
					connfailedcount--;
				}
			}
		}

		public synchronized Connection getConnection() throws Exception
		{
			long t = 0;
			int n = 0;
			while((connfailedcount > 0 || (dbconfig.dbmxcon > 0 && conncount >= dbconfig.dbmxcon))
				&& freeconn.size() == 0)
			{
				if (t == 0 || System.currentTimeMillis() - t > 1000)
				{
					log("wait for connection free"
						+ (n > 0?(" " + n + "s"):"")
							+ ", db "
							+ dbconfig.dbname
							+ " conncount="
							+ conncount);
					t = System.currentTimeMillis();
					n++;
				}
				wait();
			}
			if (freeconn.size() <= 1)
			{
				allusedtime = System.currentTimeMillis();
			}
			if (freeconn.size() > 0)
			{
				PooledConnection pc = (PooledConnection)freeconn.remove(freeconn.size() - 1);
				return pc.proxy_connection;
			}
			Connection con = newConnection();
			return con;
		}

		public synchronized void freeConnection(PooledConnection pc) throws SQLException
		{
			if (!pc.physical_connection.isClosed() && !freeconn.contains(pc))
			{
				freeconn.add(pc);
				notifyAll();
			}
		}

		public synchronized void cleanFreeConnection()
		{
			if (System.currentTimeMillis() > allusedtime + dbconfig.keepidletime)
			{
				if (freeconn.size() > 0)
				{
					PooledConnection pc = (PooledConnection)freeconn.remove(0);
					closeConnection(pc.physical_connection);
					allusedtime = System.currentTimeMillis();
				}
				clearEmptyPool();
			}
		}

		public synchronized void cleanAllConnection()
		{
			for(int i = freeconn.size() - 1; i >= 0; i--)
			{
				PooledConnection pc = (PooledConnection)freeconn.remove(i);
				closeConnection(pc.physical_connection);
			}
			clearEmptyPool();
		}

		private void clearEmptyPool()
		{
			if (conncount == 0)
			{
				synchronized(pools)
				{
					pools.remove(dbconfig);
				}
				Log log = tlLog.get();
				if (log == null)
				{
					log = DatabaseConnectionPoolManager.this.log;
				}
				if (log != null)
				{
					log.info("Connection pool for db "
						+ dbconfig.dbname
							+ " "
							+ dbconfig.dburl
							+ " user:"
							+ dbconfig.dbuser
							+ " destroyed.");
					if (pools.size() == 0)
					{
						log.info("All connection pools destroyed.");
					}
				}
			}
		}

		synchronized void closeConnection(Connection conn)
		{
			conncount--;
			destroyConnection(conn);
			notifyAll();
		}

		private void destroyConnection(Connection con)
		{
			try
			{
				if (con != null)
				{
					con.close();
				}
			}
			catch(Exception e)
			{
			}
			log("close db connection("
				+ (conncount + 1)
					+ ") "
					+ dbconfig.dbname
					+ " "
					+ dbconfig.dburl
					+ " user:"
					+ dbconfig.dbuser);
		}

		public synchronized int getConnectionCount()
		{
			return conncount;
		}
	};

	private static CleanThread freeThread = new CleanThread();

	private static class CleanThread extends Thread
	{
		public Log clean_log = new Log(":DBConnection.Cleaner");

		public CleanThread()
		{
			super("connection cleaner");
			Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook CC")
			{
				public void run()
				{
					endloop();
				}
			});
			this.setDaemon(true);
			this.start();
		}

		public void run()
		{
			try
			{
				while(DatabaseConnectionPoolManager.freeThread == this)
				{
					loop();
				}
				endloop();
			}
			catch(Exception e)
			{
				if (clean_log != null)
				{
					clean_log.error(e.getMessage());
				}
				else
				{
					e.printStackTrace();
				}
			}
			catch(Error e)
			{
				if (clean_log != null)
				{
					clean_log.error(e.getMessage());
				}
				else
				{
					e.printStackTrace();
				}
			}
		}

		public void loop()
		{
			try
			{
				Pool[] ps;
				synchronized(pools)
				{
					ps = (Pool[])pools.values().toArray(new Pool[0]);
				}
				for(int i = 0; i < ps.length; i++)
				{
					Pool pi = (Pool)ps[i];
					pi.cleanFreeConnection();
				}
				sleep(1000);
			}
			catch(InterruptedException e)
			{
			}
			catch(Exception e)
			{
				if (clean_log != null)
				{
					clean_log.error(e.getMessage());
				}
				else
				{
					e.printStackTrace();
				}
			}
			catch(Error e)
			{
				if (clean_log != null)
				{
					clean_log.error(e.getMessage());
				}
				else
				{
					e.printStackTrace();
				}
			}
		}

		public void endloop()
		{
			clearAll();
		}
	}

	//
	public static class ConnectionProxy implements InvocationHandler
	{
		private PooledConnection pc;

		ConnectionProxy(PooledConnection pc)
		{
			this.pc = pc;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			String methodName = method.getName();
			Class[] params = method.getParameterTypes();
			try
			{
				// close are special cased
				if ("close".equals(methodName) && params.length == 0)
				{
					pc.pool.freeConnection(pc);
					return null;
				}
				if ("getPhysicalConnection".equals(methodName) && params.length == 0)
				{
					return pc.physical_connection;
				}
				if ("getPooledConnection".equals(methodName) && params.length == 0)
				{
					return pc;
				}
				if ("equals".equals(methodName) && params.length == 1)
				{
					if (args == null || args.length != 1 || args[0] == null)
					{
						return false;
					}
					if (args[0] instanceof ProxyConnection)
					{
						return pc.physical_connection.equals(((ProxyConnection)args[0]).getPhysicalConnection());
					}
					return pc.physical_connection.equals(args[0]);
				}
				if ("hashCode".equals(methodName) && params.length == 0)
				{
					return pc.physical_connection.hashCode();
				}
				Object o = method.invoke(pc.physical_connection, args);
				if (o instanceof CallableStatement)
				{
					o = newProxyInstance(pc, o, CallableStatement.class);
				}
				else if (o instanceof PreparedStatement)
				{
					o = newProxyInstance(pc, o, PreparedStatement.class);
				}
				else if (o instanceof Statement)
				{
					o = newProxyInstance(pc, o, Statement.class);
				}
				else if (o instanceof DatabaseMetaData)
				{
					o = newProxyInstance(pc, o, DatabaseMetaData.class);
				}
				return o;
			}
			catch(InvocationTargetException ite)
			{
				throw ite.getTargetException();
			}
			finally
			{
				pc.lastusetime = System.currentTimeMillis();
			}
		}
	}

	protected static Object newProxyInstance(PooledConnection pc, Object o, Class claxx)
	{
		return Proxy.newProxyInstance(o.getClass().getClassLoader(), new Class[]{claxx}, new DBObjProxy(pc, null, o));
	}

	public static void clearAll()
	{
		try
		{
			Pool[] ps;
			synchronized(pools)
			{
				ps = (Pool[])pools.values().toArray(new Pool[0]);
			}
			for(int i = 0; i < ps.length; i++)
			{
				Pool pi = (Pool)ps[i];
				pi.cleanAllConnection();
			}
			synchronized(drivers)
			{
				Iterator i = drivers.values().iterator();
				while(i.hasNext())
				{
					HashMap loader_driver_map = (HashMap)i.next();
					Iterator dsi = loader_driver_map.values().iterator();
					while(dsi.hasNext())
					{
						Driver driver = (Driver)dsi.next();
						try
						{
							DriverManager.deregisterDriver(driver);
						}
						catch(Throwable t)
						{
						}
					}
				}
			}
		}
		catch(Exception e)
		{
		}
		catch(Error e)
		{
		}
	}

	public static class DBObjProxy implements InvocationHandler
	{
		private PooledConnection pc;
		private Statement pst;
		private Object obj;

		DBObjProxy(PooledConnection pc, Statement pst, Object obj)
		{
			this.pc = pc;
			this.pst = pst;
			this.obj = obj;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			String methodName = method.getName();
			Class[] params = method.getParameterTypes();
			if ("getConnection".equals(methodName) && params.length == 0)
			{
				return pc.proxy_connection;
			}
			if ("getStatement".equals(methodName) && params.length == 0)
			{
				return pst;
			}
			try
			{
				Object o = method.invoke(obj, args);
				if (o instanceof ResultSet)
				{
					Object ret = Proxy.newProxyInstance(this.getClass().getClassLoader(),
						new Class[]{ResultSet.class},
						new DBObjProxy(pc, (Statement)proxy, o));
					return ret;
				}
				return o;
			}
			catch(InvocationTargetException ite)
			{
				throw ite.getTargetException();
			}
		}
	}

	public void setLogger(Log log)
	{
		this.log = log;
		this.tlLog.set(log);
	}

	private void log(String s)
	{
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		if (log != null)
		{
			log.info(s);
		}
	}

	public int SynchUse(Connection transaction)
	{
		synchronized(transaction)
		{
			PooledConnection pc = ((ProxyConnection)transaction).getPooledConnection();
			pc.synchronized_use_count++;
			return pc.synchronized_use_count;
		}
	}

	public int SynchFree(Connection transaction)
	{
		synchronized(transaction)
		{
			PooledConnection pc = ((ProxyConnection)transaction).getPooledConnection();
			pc.synchronized_use_count--;
			return pc.synchronized_use_count;
		}
	}

	public int SynchUseCount(Connection transaction)
	{
		synchronized(transaction)
		{
			PooledConnection pc = ((ProxyConnection)transaction).getPooledConnection();
			return pc.synchronized_use_count;
		}
	}
}
