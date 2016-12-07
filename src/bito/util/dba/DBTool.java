package bito.util.dba;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bito.util.dba.impl.BLMetadata;
import bito.util.dba.impl.SQL;
import bito.util.logger.Log;

/**
 * 和 DBHelper 功能相同，不同的是 DBTool 会输出日志
 *
 * @author LIBOFENG
 */
public class DBTool
{
	public static DBTool getDBTool(String dbname)
	{
		return new DBTool(dbname);
	}

	public static DBTool getDBTool(String dbdriver, String dburl, String dbuser, String dbpswd, int dbmxcon,
		long dbmxidletime)
	{
		return new DBTool(dbdriver, dburl, dbuser, dbpswd, dbmxcon, dbmxidletime);
	}

	public static DBTool getDBTool(DBAccess dba)
	{
		return new DBTool(dba);
	}

	private Log log = new Log("");
	private ThreadLocal<Log> tlLog = new ThreadLocal();
	private DBHelper dbhelper;

	protected DBTool(String dbname)
	{
		dbhelper = DBHelper.getDBHelper(dbname);
		setLogger(this.log);
	}

	protected DBTool(String dbdriver, String dburl, String dbuser, String dbpswd, int dbmxcon, long dbmxidletime)
	{
		dbhelper = DBHelper.getDBHelper(dbdriver, dburl, dbuser, dbpswd, dbmxcon, dbmxidletime);
		setLogger(this.log);
	}

	protected DBTool(DBAccess dba)
	{
		dbhelper = DBHelper.getDBHelper(dba);
		setLogger(this.log);
	}

	public DBHelper getDBHelper()
	{
		return dbhelper;
	}

	public void refreshConfig()
	{
		dbhelper.refreshConfig();
	}

	public void setLogger(Log log)
	{
		this.log = log;
		this.tlLog.set(log);
		dbhelper.setLogger(this.log);
	}

	public synchronized long getStamp()
	{
		return d.E.V().getStamp();
	}

	public void insertData(String table, Map data) throws Exception
	{
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		if (log != null)
		{
			log.debug("insertData table " + table + " data " + data);
		}
		dbhelper.insertData(table, data);
	}

	public void updateData(String table, Map data, Map condition) throws Exception
	{
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		if (log != null)
		{
			log.debug("updateData table " + table + " set " + data + " where " + condition);
		}
		dbhelper.updateData(table, data, condition);
	}

	public void deleteData(String table, Map condition) throws Exception
	{
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		if (log != null)
		{
			log.debug("deleteData table " + table + " where " + condition);
		}
		dbhelper.deleteData(table, condition);
	}

	public Map[] selectData(String table, Map condition, int toprows) throws Exception
	{
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		if (log != null)
		{
			log.debug("selectData table " + table + " where " + condition + " top " + toprows);
		}
		return dbhelper.selectData(table, condition, toprows);
	}

	public String getID()
	{
		return dbhelper.getID();
	}

	public int getDBType() throws Exception
	{
		return dbhelper.getDBType();
	}

	public Connection getConnection() throws Exception
	{
		return dbhelper.getConnection();
	}

	public void destroyConnection(Connection con)
	{
		dbhelper.destroyConnection(con);
	}

	public Connection syncTransaction() throws SQLException
	{
		return dbhelper.syncTransaction();
	}

	public void syncTransaction(Connection transaction) throws SQLException
	{
		dbhelper.syncTransaction(transaction);
	}

	public void transactionBegin() throws Exception
	{
		dbhelper.transactionBegin();
	}

	public void transactionEnd() throws Exception
	{
		dbhelper.transactionEnd();
	}

	public Map[] query(String sql) throws Exception
	{
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		if (log != null)
		{
			log.debug(sql);
		}
		return dbhelper.query(sql);
	}

	public int update(String sql) throws Exception
	{
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		if (log != null)
		{
			log.debug(sql);
		}
		return dbhelper.update(sql);
	}

	public Map[] query(String sql, int toprows, boolean reverse) throws Exception
	{
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		if (log != null)
		{
			log.debug(sql + " [top " + toprows + (reverse?" reverse":"") + "]");
		}
		return dbhelper.query(sql, toprows, reverse);
	}

	public void setResultMapKeyCase(int keycase)
	{
		dbhelper.setResultMapKeyCase(keycase);
	}

	public int run(String sql) throws Exception
	{
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		if (log != null)
		{
			log.debug(sql);
		}
		return dbhelper.run(sql);
	}

	public Map[] execProcedure(String sql, Map[] params) throws Exception
	{
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		if (log != null)
		{
			log.debug(sql);
		}
		return dbhelper.execProcedure(sql, params);
	}

	public Map[] execCommand(String sql) throws Exception
	{
		Log log = this.tlLog.get();
		if (log == null)
		{
			log = this.log;
		}
		if (log != null)
		{
			log.debug(sql);
		}
		return dbhelper.execCommand(sql);
	}

	public void release() throws Exception
	{
		dbhelper.release();
	}
}
