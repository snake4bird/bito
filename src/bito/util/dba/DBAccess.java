package bito.util.dba;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import bito.util.cfg.SystemConfig;
import bito.util.dba.impl.DBConfig;
import bito.util.dba.impl.DatabaseConnectionPoolManager;
import bito.util.logger.Log;

/**
 * 数据库存取的基本功能
 * 
 * @author LIBOFENG
 *
 */
public class DBAccess
{
	public static final int KEEPCASE = 0;
	public static final int LOWERCASE = 1;
	public static final int UPPERCASE = 2;
	//
	private Log log = new Log("");
	private DatabaseConnectionPoolManager dcpm;
	private int keycase = 2;
	private int dbtype = -1;
	private ThreadLocal<Connection> tlTransaction = new ThreadLocal();
	private ThreadLocal<Statement> tlBatch = new ThreadLocal();
	private ThreadLocal<Log> tlLog = new ThreadLocal();
	private DBConfig dbconfig;
	private boolean isAutoCommit = false;
	private boolean isReplaceChar0 = false;

	protected DBAccess()
	{
		// for DBHelper only
	}

	public DBAccess(String dbname)
	{
		dbconfig = new DBConfig(dbname);
		initPool(dbconfig);
	}

	public DBAccess(String dbdriver, String dburl, String dbuser, String dbpswd, int dbmxcon, long dbmxidletime)
	{
		this(dbdriver, dburl, dbuser, dbpswd, dbmxcon, dbmxidletime, 10000);
	}

	public DBAccess(String dbdriver, String dburl, String dbuser, String dbpswd, int dbmxcon, long dbmxidletime,
		int rowslimit)
	{
		initPool(new DBConfig(null, dbdriver, dburl, dbuser, dbpswd, dbmxcon, dbmxidletime, rowslimit));
	}

	public void refreshConfig()
	{
		if (dcpm != null)
		{
			dcpm.refreshConfig();
		}
	}

	public void setLogger(Log log)
	{
		this.log = log;
		this.tlLog.set(log);
		if (dcpm != null)
		{
			dcpm.setLogger(log);
		}
	}

	protected void initPool(DBConfig dbconfig)
	{
		if (dbconfig.dbname != null)
		{
			if (SystemConfig.getBoolean(dbconfig.dbname + ".autocommit", false))
			{
				isAutoCommit = true;
			}
			if (SystemConfig.getBoolean(dbconfig.dbname + ".replacechar0", false))
			{
				isReplaceChar0 = true;
			}
		}
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		dcpm = new DatabaseConnectionPoolManager(dbconfig);
		dcpm.setLogger(log);
	}

	public String getID()
	{
		return dbconfig.toString();
	}

	public int getDBType() throws Exception
	{
		if (dbtype == -1)
		{
			Connection con = getConnection();
			try
			{
				dbtype = DBServerType.getDBType(con);
			}
			finally
			{
				con.close();
			}
		}
		return dbtype;
	}

	public Connection syncTransaction() throws SQLException
	{
		Connection con = tlTransaction.get();
		if (con == null)
		{
			throw new SQLException("No transaction now.");
		}
		return con;
	}

	public void syncTransaction(Connection transaction) throws SQLException
	{
		Connection con = tlTransaction.get();
		if (con != null)
		{
			throw new SQLException("Can NOT overwrite a exist transaction.");
		}
		else
		{
			tlTransaction.set(transaction);
			dcpm.SynchUse(transaction);
		}
	}

	public Connection getConnection() throws Exception
	{
		Connection con = dcpm.getConnection();
		disableAutoCommit(con);
		return con;
	}

	public void destroyConnection(Connection con)
	{
		dcpm.destroyConnection(con);
	}

	public void release() throws SQLException
	{
		transactionRelease(0);
	}

	private void transactionRelease(int commit) throws SQLException
	{
		Connection con = tlTransaction.get();
		try
		{
			if (con != null)
			{
				int count = dcpm.SynchFree(con);
				if (count == 0)
				{
					try
					{
						if (commit == 1)
						{
							Log log = this.tlLog.get();
							if (log == null)
							{
								log = this.log;
							}
							if (log != null)
							{
								log.debug("Commit the transaction.");
							}
							commit(con);
						}
						else if (commit == -1)
						{
							Log log = this.tlLog.get();
							if (log == null)
							{
								log = this.log;
							}
							if (log != null)
							{
								log.warn("Rollback the uncommitted transaction.");
							}
							rollback(con);
						}
						else
						{
							Log log = this.tlLog.get();
							if (log == null)
							{
								log = this.log;
							}
							if (log != null)
							{
								log.warn("End transaction with default auto commit flag.");
							}
							// do nothing
						}
					}
					finally
					{
						try
						{
							con.close();
						}
						catch(SQLException xe)
						{
						}
					}
				}
			}
		}
		finally
		{
			tlTransaction.remove();
		}
	}

	public void transactionBegin() throws Exception
	{
		transactionRelease(-1);
		Connection transaction = getConnection();
		try
		{
			syncTransaction(transaction);
		}
		catch(SQLException e)
		{
			try
			{
				transaction.close();
			}
			catch(SQLException xe)
			{
			}
			throw e;
		}
	}

	public void transactionEnd() throws Exception
	{
		transactionRelease(1);
	}

	protected void disableAutoCommit(Connection con) throws SQLException
	{
		if (!isAutoCommit)
		{
			con.setAutoCommit(false);
		}
	}

	protected void commit(Connection con) throws SQLException
	{
		if (!isAutoCommit)
		{
			con.commit();
		}
	}

	protected void rollback(Connection con) throws SQLException
	{
		if (!isAutoCommit)
		{
			con.rollback();
		}
	}

	protected Statement getStatement() throws Exception
	{
		return getStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}

	protected Statement getStatement(int type, int concur) throws Exception
	{
		Connection con = tlTransaction.get();
		boolean inTransaction = (con != null);
		if (!inTransaction)
		{
			con = getConnection();
		}
		try
		{
			return con.createStatement(type, concur);
		}
		catch(Exception e)
		{
			// try again
			if (inTransaction)
			{
				throw e;
			}
			destroyConnection(con);
			con = getConnection();
			try
			{
				return con.createStatement(type, concur);
			}
			catch(Exception ex)
			{
				try
				{
					con.close();
				}
				catch(SQLException xe)
				{
				}
				throw ex;
			}
		}
	}

	protected synchronized boolean STClose(Statement st)
	{
		Connection con = tlTransaction.get();
		boolean inTransaction = (con != null);
		try
		{
			if (!inTransaction)
			{
				con = st.getConnection();
			}
			st.close();
			if (!inTransaction)
			{
				commit(con);
			}
			return true;
		}
		catch(Exception sqle)
		{
			return false;
		}
		finally
		{
			if (!inTransaction)
			{
				try
				{
					con.close();
				}
				catch(SQLException xe)
				{
				}
			}
		}
	}

	private static final String[] dtstyle = new String[]{"yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss",};

	public static long dtparse(String dtstring) throws Exception
	{
		for(int i = 0; i < dtstyle.length; i++)
		{
			try
			{
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(dtstyle[i]);
				return sdf.parse(dtstring).getTime();
			}
			catch(java.text.ParseException e)
			{
			}
		}
		throw new Exception("Unparseable datetime: \"" + dtstring + "\"");
	}

	public Map[] query(String sql) throws Exception
	{
		return query(sql, dbconfig.rowslimit, false);
	}

	protected String getStringResult(ResultSet rs, String cn) throws Exception
	{
		String s = rs.getString(cn);
		if (isReplaceChar0 && s != null)
		{
			s = s.replaceAll("\0", "");
		}
		return s;
	}

	public int update(String sql) throws Exception
	{
		Statement st = tlBatch.get();
		if (st != null)
		{
			st.addBatch(sql);
			return 0;
		}
		st = getStatement();
		try
		{
			int i = st.executeUpdate(sql);
			return i;
		}
		catch(Exception e)
		{
			throw e;
		}
		finally
		{
			STClose(st);
		}
	}

	public Map[] query(String sql, int toprows, boolean reverse) throws Exception
	{
		if (tlBatch.get() != null)
		{
			throw new Exception("Not support in batch mode.");
		}
		Statement st = null;
		try
		{
			if (reverse)
			{
				st = getStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				st.setFetchDirection(ResultSet.FETCH_REVERSE);
			}
			else
			{
				st = getStatement();
			}
			ResultSet rs = st.executeQuery(sql);
			//
			return convertResultSet2MapArray(rs, toprows, reverse);
		}
		catch(Exception e)
		{
			throw e;
		}
		catch(Error err)
		{
			throw new Exception(err.getClass().getName() + ":" + err.getMessage());
		}
		finally
		{
			if (st != null)
			{
				STClose(st);
			}
		}
	}

	protected CallableStatement getCallableStatement(String sql) throws Exception
	{
		Connection con = tlTransaction.get();
		boolean inTransaction = (con != null);
		if (!inTransaction)
		{
			con = getConnection();
		}
		try
		{
			return con.prepareCall(sql);
		}
		catch(Exception e)
		{
			// try again
			if (inTransaction)
			{
				throw e;
			}
			destroyConnection(con);
			con = getConnection();
			try
			{
				return con.prepareCall(sql);
			}
			catch(Exception ex)
			{
				try
				{
					con.close();
				}
				catch(SQLException xe)
				{
				}
				throw ex;
			}
		}
	}

	public Map[] execProcedure(String sql, Map[] params) throws Exception
	{
		if (tlBatch.get() != null)
		{
			throw new Exception("Not support in batch mode.");
		}
		CallableStatement cstmt = getCallableStatement(sql);
		try
		{
			if (params != null)
			{
				for(int i = 0; i < params.length; i++)
				{
					if (params[i] != null && params[i].size() > 0)
					{
						int type = 0;
						String typename = (String)params[i].get("TYPE");
						if (typename != null && typename.length() > 0)
						{
							type = java.sql.Types.class.getField(typename).getInt(java.sql.Types.class);
						}
						if (params[i].containsKey("INPUT"))
						{
							Object value = params[i].get("INPUT");
							cstmt.setObject(i + 1, value, type);
						}
						else if (params[i].containsKey("OUTPUT"))
						{
							Object value = params[i].get("OUTPUT");
							cstmt.registerOutParameter(i + 1, type);
							cstmt.setObject(i + 1, value, type);
						}
						else
						{
							cstmt.registerOutParameter(i + 1, type);
						}
					}
				}
			}
			cstmt.execute();
			if (params != null)
			{
				for(int i = 0; i < params.length; i++)
				{
					if (params[i] != null && params[i].size() > 0)
					{
						int type = 0;
						String typename = (String)params[i].get("TYPE");
						if (typename != null && typename.length() > 0)
						{
							type = java.sql.Types.class.getField(typename).getInt(java.sql.Types.class);
						}
						if (!params[i].containsKey("INPUT"))
						{
							params[i].put("OUTPUT", cstmt.getObject(i + 1));
						}
					}
				}
			}
			ResultSet rs = null;
			while(cstmt.getMoreResults())
			{
				rs = cstmt.getResultSet();
			}
			if (rs != null)
			{
				return convertResultSet2MapArray(rs, dbconfig.rowslimit, false);
			}
			int updatecount = cstmt.getUpdateCount();
			if (rs == null && updatecount != -1)
			{
				Map m = new HashMap();
				m.put("UPDATECOUNT", "" + updatecount);
				return new Map[]{m};
			}
			return new Map[0];
		}
		catch(Exception e)
		{
			throw e;
		}
		catch(Error err)
		{
			throw new Exception(err.getClass().getName() + ":" + err.getMessage());
		}
		finally
		{
			if (cstmt != null)
			{
				STClose(cstmt);
			}
		}
	}

	public Map[] execCommand(String sql) throws Exception
	{
		if (tlBatch.get() != null)
		{
			throw new Exception("Not support in batch mode.");
		}
		Statement cstmt = getStatement();
		try
		{
			cstmt.execute(sql);
			ResultSet rs = null;
			while(cstmt.getMoreResults())
			{
				rs = cstmt.getResultSet();
			}
			if (rs != null)
			{
				return convertResultSet2MapArray(rs, dbconfig.rowslimit, false);
			}
			int updatecount = cstmt.getUpdateCount();
			if (rs == null && updatecount != -1)
			{
				Map m = new HashMap();
				m.put("UPDATECOUNT", "" + updatecount);
				return new Map[]{m};
			}
			return new Map[0];
		}
		catch(Exception e)
		{
			throw e;
		}
		catch(Error err)
		{
			throw new Exception(err.getClass().getName() + ":" + err.getMessage());
		}
		finally
		{
			if (cstmt != null)
			{
				STClose(cstmt);
			}
		}
	}

	protected boolean RSNext(ResultSet rs)
	{
		return RSNext(rs, false);
	}

	protected boolean RSNext(ResultSet rs, boolean reverse)
	{
		try
		{
			if (reverse)
			{
				return rs.previous();
			}
			else
			{
				return rs.next();
			}
		}
		catch(Exception sqle)
		{
			return false;
		}
	}

	public void setResultMapKeyCase(int keycase)
	{
		this.keycase = keycase;
	}

	protected Map[] convertResultSet2MapArray(ResultSet rs, int toprows, boolean reverse) throws Exception
	{
		ResultSetMetaData md = rs.getMetaData();
		ArrayList al = new ArrayList();
		boolean hasnext = true;
		if (reverse)
		{
			hasnext = rs.last();
		}
		else
		{
			hasnext = RSNext(rs, reverse);
		}
		for(; hasnext && al.size() < toprows; hasnext = RSNext(rs, reverse))
		{
			Map hm = d.E.V().newMapSortedByAddTime();
			for(int i = 1; i <= md.getColumnCount(); i++)
			{
				try
				{
					String cn = md.getColumnLabel(i);
					if (cn == null || cn.length() == 0)
					{
						cn = md.getColumnName(i);
					}
					hm.put(keycase == 2?cn.toUpperCase():keycase == 1?cn.toLowerCase():cn, getStringResult(rs, cn));
				}
				catch(SQLException sqle)
				{
				}
			}
			al.add(hm);
		}
		if (hasnext)
		{
			throw new SQLException("Too more records large than " + toprows + " rows in result set");
		}
		if (reverse)
		{
			Collections.reverse(al);
		}
		Map[] rets = new Map[al.size()];
		al.toArray(rets);
		return rets;
	}

	public int run(String sql) throws Exception
	{
		Statement st = tlBatch.get();
		if (st != null)
		{
			st.addBatch(sql);
			return 0;
		}
		st = getStatement();
		try
		{
			boolean b = st.execute(sql);
			return b?1:0;
		}
		catch(Exception e)
		{
			throw e;
		}
		finally
		{
			STClose(st);
		}
	}

	public void batchBegin() throws Exception
	{
		if (tlBatch.get() != null)
		{
			tlBatch.get().clearBatch();
		}
		else
		{
			tlBatch.set(getStatement());
		}
	}

	public int[] batchEnd() throws SQLException
	{
		Statement st = tlBatch.get();
		if (st != null)
		{
			try
			{
				return st.executeBatch();
			}
			finally
			{
				tlBatch.remove();
				STClose(st);
			}
		}
		return new int[0];
	}
}
