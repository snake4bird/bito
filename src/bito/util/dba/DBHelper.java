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
import bito.util.dba.impl.DatabaseConnectionPoolManager;
import bito.util.dba.impl.SQL;
import bito.util.logger.Log;

/**
 * 在 DBAccess 基础上的扩展
 * 可根据数据或条件 map 生成 SQL 语句
 * 
 * @author LIBOFENG
 *
 */
public class DBHelper extends DBAccess
{
	private static HashMap dbtcache = new HashMap();

	protected static DBHelper getCachedDBHelper(DBHelper dbt)
	{
		DBHelper dbtc = (DBHelper)dbtcache.get(dbt.dba.getID());
		if (dbtc != null)
		{
			return dbtc;
		}
		dbtcache.put(dbt.dba.getID(), dbt);
		return dbt;
	}

	protected static DBHelper getDBHelper(String dbname)
	{
		DBHelper dbt = new DBHelper(dbname);
		return getCachedDBHelper(dbt);
	}

	protected static DBHelper getDBHelper(String dbdriver, String dburl, String dbuser, String dbpswd, int dbmxcon,
		long dbmxidletime)
	{
		DBHelper dbt = new DBHelper(dbdriver, dburl, dbuser, dbpswd, dbmxcon, dbmxidletime);
		return getCachedDBHelper(dbt);
	}

	protected static DBHelper getDBHelper(DBAccess dba)
	{
		DBHelper dbt = new DBHelper(dba);
		return getCachedDBHelper(dbt);
	}

	public static void clearAll()
	{
		DatabaseConnectionPoolManager.clearAll();
	}

	private DBAccess dba;
	private BLMetadata blm;

	protected DBHelper(String dbname)
	{
		super(dbname);
		init(this);
	}

	protected DBHelper(String dbdriver, String dburl, String dbuser, String dbpswd, int dbmxcon, long dbmxidletime)
	{
		super(dbdriver, dburl, dbuser, dbpswd, dbmxcon, dbmxidletime);
		init(this);
	}

	protected DBHelper(DBAccess dba)
	{
		init(dba);
	}

	private void init(DBAccess dba)
	{
		this.dba = dba;
		this.blm = BLMetadata.getInstance(this.dba);
	}

	public void refreshConfig()
	{
		if (this.dba == this)
		{
			super.refreshConfig();
		}
		else
		{
			this.dba.refreshConfig();
		}
	}

	public void setLogger(Log log)
	{
		if (this.dba == this)
		{
			super.setLogger(log);
		}
		else
		{
			this.dba.setLogger(log);
		}
	}

	public synchronized long getStamp()
	{
		return bito.util.E.V().getStamp();
	}

	public void insertData(String table, Map data) throws Exception
	{
		String sql = generateInsertSQL(table, data, true);
		try
		{
			update(sql);
		}
		catch(Exception e)
		{
			throw new Exception(sql, e);
		}
	}

	public void updateData(String table, Map data, Map condition) throws Exception
	{
		String sql = generateUpdateSQL(table, data, condition, true);
		try
		{
			update(sql);
		}
		catch(Exception e)
		{
			throw new Exception(sql, e);
		}
	}

	public void deleteData(String table, Map condition) throws Exception
	{
		String sql = generateDeleteSQL(table, condition);
		try
		{
			update(sql);
		}
		catch(Exception e)
		{
			throw new Exception(sql, e);
		}
	}

	public Map[] selectData(String table, Map condition, int toprows) throws Exception
	{
		String sql = generateSelectSQL(table, condition);
		try
		{
			return query(sql, toprows, false);
		}
		catch(Exception e)
		{
			throw new Exception(sql, e);
		}
	}

	protected String generateInsertSQL(String table, Map data, boolean withBlobFields) throws Exception
	{
		StringBuffer sql = new StringBuffer();
		ArrayList insertedFields = new ArrayList(data.size() + 1);
		sql.append(SQL.INSERT_INTO).append(table);
		char sep = '(';
		StringBuffer fields = new StringBuffer();
		for(Iterator i = blm.getFields(table); i.hasNext();)
		{
			String fieldName = (String)i.next();
			if (data.containsKey(fieldName) && !blm.isFieldAutoIncremented(table, fieldName))
			{
				String sfcn = blm.getFieldClassName(table, fieldName);
				if (withBlobFields || !"[B".equals(sfcn))
				{
					fields.append(sep).append(fieldName);
					sep = ',';
					insertedFields.add(fieldName);
				}
			}
		}
		if (fields.length() == 0)
		{
			throw new Exception("No data to generate insert SQL.");
		}
		sql.append(fields);
		sql.append(')');
		sql.append(' ').append(SQL.VALUES);
		sep = '(';
		for(int i = 0; i < insertedFields.size(); i++)
		{
			String fieldName = (String)insertedFields.get(i);
			String fieldValueStr = castFieldValueToSQLString(table, fieldName, data.get(fieldName));
			sql.append(sep).append(fieldValueStr);
			sep = ',';
		}
		sql.append(')');
		return sql.toString();
	}

	private String castFieldValueToSQLString(String table, String fieldName, Object o) throws Exception
	{
		if (o == null)
		{
			return SQL.NULL;
		}
		String s = o.toString();
		Class fieldClass = blm.getFieldClass(table, fieldName);
		if (fieldClass == String.class)
		{
			s = s.replaceAll("'", "''"); //when string has '
			return "'" + s + "'";
		}
		String sfcn = blm.getFieldClassName(table, fieldName);
		switch(dba.getDBType())
		{
		case DBServerType.ORACLE:
			if (fieldClass == java.sql.Date.class || fieldClass == java.sql.Time.class || fieldClass == Timestamp.class)
			{
				int lidot = s.lastIndexOf(".");
				if (lidot >= 0)
				{
					//不要毫秒
					s = s.substring(0, lidot - 1);
				}
				return "to_date('" + s + "', 'YYYY-MM-DD HH24:MI:SS')";
			}
			if ("[B".equals(sfcn))
			{
				s = "'";
				byte[] bs = (byte[])o;
				for(int i = 0; i < bs.length; i++)
				{
					int ii = bs[i];
					if (ii < 0)
					{
						ii += 256;
					}
					s += Integer.toString(ii, 16).toUpperCase();
				}
				s += "'";
				return s;
			}
			break;
		case DBServerType.SQLSERVER:
		default:
			if (fieldClass == java.sql.Date.class || fieldClass == java.sql.Time.class || fieldClass == Timestamp.class)
			{
				return "'" + s + "'";
			}
			if ("[B".equals(sfcn))
			{
				s = "0x";
				byte[] bs = (byte[])o;
				for(int i = 0; i < bs.length; i++)
				{
					int ii = bs[i];
					if (ii < 0)
					{
						ii += 256;
					}
					s += Integer.toString(ii, 16).toUpperCase();
				}
				return s;
			}
			break;
		}
		return s;
	}

	private String generateConditionFieldValuePairsSQL(String table, Map data, String separatorKV,
		String separatorBetweenKVs) throws Exception
	{
		if (data == null || data.size() == 0)
		{
			return null;
		}
		StringBuffer sql = new StringBuffer();
		for(Iterator i = data.entrySet().iterator(); i.hasNext();)
		{
			String kv = "";
			Map.Entry me = (Map.Entry)i.next();
			String key = (String)me.getKey();
			Object value = me.getValue();
			if (value == null)
			{
				kv = key + SQL.ISNULL;
			}
			else
			{
				if (value instanceof Map)
				{
					String isql = "";
					if (SQL.OPERATORSET.contains(key))
					{
						isql = generateConditionFieldValuePairsSQL(table, (Map)value, key, separatorBetweenKVs);
					}
					else
					{
						isql = generateConditionFieldValuePairsSQL(table, (Map)value, "=", key);
					}
					if (isql != null)
					{
						kv = "(" + isql + ")";
					}
				}
				else
				{
					kv = key + " " + separatorKV + " " + castFieldValueToSQLString(table, key, value);
				}
			}
			if (kv.length() > 0)
			{
				if (sql.length() > 0)
				{
					sql.append(separatorBetweenKVs);
				}
				sql.append(kv);
			}
		}
		return sql.toString();
	}

	/**
	 * condition map sample:
	 * {
	 *   and : {
	 *     or : {
	 *       > : {
	 *         k : v
	 *       }
	 *       < : {
	 *         k : v
	 *       }
	 *     }
	 *     = : {
	 *       key : value
	 *     }
	 *     like : {
	 *       key : value
	 *     }
	 * }
	 * 
	 * operator:
	 *     =
	 *     >
	 *     >=
	 *     <
	 *     <=
	 *     <>
	 *     !=
	 *     like
	 *     not like
	 * logic:
	 *     and
	 *     or
	 *     not
	 *     ...
	 *     
	 * @param condition
	 * @return
	 * @throws Exception 
	 */
	protected String generateConditionSQL(String table, Map condition) throws Exception
	{
		String sql = generateConditionFieldValuePairsSQL(table, condition, "=", SQL.AND);
		if (sql != null && sql.length() > 0)
		{
			return SQL.WHERE + sql;
		}
		return "";
	}

	/**
	 * @param table
	 * @param data
	 * @param condition
	 * @param withBlobFields
	 * @return
	 * @throws Exception 
	 */
	protected String generateUpdateSQL(String table, Map data, Map condition, boolean withBlobFields) throws Exception
	{
		if (data.isEmpty())
		{
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(SQL.UPDATE).append(table).append(SQL.SET);
		boolean toUpdateScalar = false;
		char sep = ' ';
		for(Iterator i = blm.getFields(table); i.hasNext();)
		{
			String fieldName = (String)i.next();
			if (data.containsKey(fieldName) && !blm.isFieldAutoIncremented(table, fieldName))
			{
				String sfcn = blm.getFieldClassName(table, fieldName);
				if (withBlobFields || !"[B".equals(sfcn))
				{
					sql.append(sep).append(fieldName).append('=');
					sql.append(castFieldValueToSQLString(table, fieldName, data.get(fieldName)));
					sep = ',';
				}
			}
		}
		sep = ' ';
		sql.append(generateConditionSQL(table, condition));
		return sql.toString();
	}

	protected String generateDeleteSQL(String table, Map condition) throws Exception
	{
		StringBuffer sql = new StringBuffer();
		sql.append(SQL.DELETE_FROM).append(table);
		sql.append(generateConditionSQL(table, condition));
		return sql.toString();
	}

	protected String generateSelectSQL(String table, Map condition) throws Exception
	{
		StringBuffer sql = new StringBuffer();
		sql.append(SQL.SELECT).append('*').append(SQL.FROM).append(table);
		sql.append(generateConditionSQL(table, condition));
		return sql.toString();
	}

	public String getID()
	{
		return (this.dba == this)?super.getID():dba.getID();
	}

	public int getDBType() throws Exception
	{
		return (this.dba == this)?super.getDBType():dba.getDBType();
	}

	public Connection getConnection() throws Exception
	{
		return (this.dba == this)?super.getConnection():dba.getConnection();
	}

	public void destroyConnection(Connection con)
	{
		if (this.dba == this)
			super.destroyConnection(con);
		else
			dba.destroyConnection(con);
	}

	protected void disableAutoCommit(Connection con) throws SQLException
	{
		if (this.dba == this)
			super.disableAutoCommit(con);
		else
			dba.disableAutoCommit(con);
	}

	protected void rollback(Connection con) throws SQLException
	{
		if (this.dba == this)
			super.rollback(con);
		else
			dba.rollback(con);
	}

	protected void commit(Connection con) throws SQLException
	{
		if (this.dba == this)
			super.commit(con);
		else
			dba.commit(con);
	}

	public Connection syncTransaction() throws SQLException
	{
		if (this.dba == this)
			return super.syncTransaction();
		else
			return dba.syncTransaction();
	}

	public void syncTransaction(Connection transaction) throws SQLException
	{
		if (this.dba == this)
			super.syncTransaction(transaction);
		else
			dba.syncTransaction(transaction);
	}

	public void transactionBegin() throws Exception
	{
		if (this.dba == this)
			super.transactionBegin();
		else
			dba.transactionBegin();
	}

	public void transactionEnd() throws Exception
	{
		if (this.dba == this)
			super.transactionEnd();
		else
			dba.transactionEnd();
	}

	protected synchronized boolean STClose(Statement st)
	{
		return (this.dba == this)?super.STClose(st):dba.STClose(st);
	}

	public Map[] query(String sql) throws Exception
	{
		return (this.dba == this)?super.query(sql):dba.query(sql);
	}

	protected String getStringResult(ResultSet rs, String cn) throws Exception
	{
		return (this.dba == this)?super.getStringResult(rs, cn):dba.getStringResult(rs, cn);
	}

	public int update(String sql) throws Exception
	{
		return (this.dba == this)?super.update(sql):dba.update(sql);
	}

	protected Statement getStatement() throws Exception
	{
		return (this.dba == this)?super.getStatement():dba.getStatement();
	}

	protected Statement getStatement(int type, int concur) throws Exception
	{
		return (this.dba == this)?super.getStatement(type, concur):dba.getStatement(type, concur);
	}

	public Map[] query(String sql, int toprows, boolean reverse) throws Exception
	{
		return (this.dba == this)?super.query(sql, toprows, reverse):dba.query(sql, toprows, reverse);
	}

	protected boolean RSNext(ResultSet rs)
	{
		return (this.dba == this)?super.RSNext(rs):dba.RSNext(rs);
	}

	protected boolean RSNext(ResultSet rs, boolean reverse)
	{
		return (this.dba == this)?super.RSNext(rs, reverse):dba.RSNext(rs, reverse);
	}

	public void setResultMapKeyCase(int keycase)
	{
		if (this.dba == this)
			super.setResultMapKeyCase(keycase);
		else
			dba.setResultMapKeyCase(keycase);
	}

	protected Map[] convertResultSet2MapArray(ResultSet rs, int toprows, boolean reverse) throws Exception
	{
		return (this.dba == this)?super.convertResultSet2MapArray(rs, toprows, reverse):dba
			.convertResultSet2MapArray(rs, toprows, reverse);
	}

	public int run(String sql) throws Exception
	{
		return (this.dba == this)?super.run(sql):dba.run(sql);
	}

	public Map[] execProcedure(String sql, Map[] params) throws Exception
	{
		return (this.dba == this)?super.execProcedure(sql, params):dba.execProcedure(sql, params);
	}

	public Map[] execCommand(String sql) throws Exception
	{
		return (this.dba == this)?super.execCommand(sql):dba.execCommand(sql);
	}

	public void release() throws SQLException
	{
		if (this.dba == this)
		{
			super.release();
		}
		else
		{
			dba.release();
		}
	}

	public void batchBegin() throws Exception
	{
		if (this.dba == this)
		{
			super.batchBegin();
		}
		else
		{
			dba.batchBegin();
		}
	}

	public int[] batchEnd() throws SQLException
	{
		if (this.dba == this)
		{
			return super.batchEnd();
		}
		else
		{
			return dba.batchEnd();
		}
	}
}
