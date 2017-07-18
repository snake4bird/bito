package assassin.script;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bito.util.dba.DBTool;
import bito.util.logger.Log;

public class SQLRunner
{
	private ASScriptEngine asse;

	public SQLRunner(ASScriptEngine asse)
	{
		this.asse = asse;
	}

	public Map[] runsql(String sql) throws Exception
	{
		if (sql == null || sql.length() == 0)
		{
			return null;
		}
		sql = sql.trim();
		String label = null;
		String[] ss;
		if ((ss = SStrMatcher.match(sql, "(?s)\\s*(\\w+)\\s*:(.*)")).length == 2)
		{
			label = ss[0].trim();
			sql = ss[1].trim();
		}
		if (sql.length() == 0)
		{
			return null;
		}
		return execSQL(label, sql);
	}

	private HashMap<String, DBTool> dbtmap = new HashMap();
	private DBTool curdbt = null;
	private String on_error_proc = null;
	private String default_dbname = "DB";

	private Map[] execSQL(String label, String sql) throws Exception
	{
		asse.debug((label == null?"":(label + " : ")) + sql);
		try
		{
			String match[];
			if ((match = SStrMatcher.match(sql, "(?is)use\\s+db\\s+(\\S+).*")).length > 0)
			{
				String dbname = match[0];
				useDB(dbname, null);
			}
			else if ((match = SStrMatcher.match(sql, "(?is)on\\s+error\\s+((?:continue)|(?:break))\\s*")).length > 0)
			{
				on_error_proc = match[0].toUpperCase();
			}
			else if ((match = SStrMatcher.match(sql, "(?is)log\\s+info\\s+(.*)")).length > 0)
			{
				//skip log info
			}
			else if ((match = SStrMatcher.match(sql,
				"(?is)((?:\\#\\#)|(?:\\@\\@)|(?:\\$\\$)|(?:--)|(?://))(.*)")).length > 0)
			{
				//skip a statement
			}
			else if ((match = SStrMatcher.match(sql,
				"(?is)((?:skip)|(?:ignore)|(?:script)|(?:not\\ssql))\\s+(.*)")).length > 0)
			{
				//skip a statement
			}
			else
			{
				if (curdbt == null)
				{
					asse.debug("use default db " + default_dbname);
					useDB(default_dbname, null);
				}
				if ((match = SStrMatcher.match(sql, "(?is)transaction\\s+((?:begin)|(?:end))\\s*")).length > 0)
				{
					if ("BEGIN".equals(match[0].toUpperCase()))
					{
						curdbt.getDBHelper().transactionBegin();
					}
					else
					// if ("END".equals(match[0].toUpperCase()))
					{
						curdbt.getDBHelper().transactionEnd();
					}
				}
				else if ((match = SStrMatcher.match(sql, "(?is)batch\\s+((?:begin)|(?:end))\\s*")).length > 0)
				{
					if ("BEGIN".equals(match[0].toUpperCase()))
					{
						curdbt.getDBHelper().batchBegin();
					}
					else
					// if ("END".equals(match[0].toUpperCase()))
					{
						int[] rs = curdbt.getDBHelper().batchEnd();
						asse.debug("batch end: " + rs == null?".":Arrays.toString(rs));
					}
				}
				else if (sql.matches("(?is)select\\s.*"))
				{
					return execQuerySQL(label, sql);
				}
				else if (sql.matches("(?is)insert\\s.*")
					|| sql.matches("(?is)update\\s.*")
						|| sql.matches("(?is)merge\\s.*")
						|| sql.matches("(?is)delete\\s.*"))
				{
					return execUpdateSQL(label, sql);
				}
				else if (sql.matches("(?is)call\\s.*")
					|| sql.matches("(?is)exec\\s.*")
						|| sql.matches("(?is)execute\\s.*"))
				{
					return execProcedureSQL(label, sql);
				}
				else
				{
					return execCommandSQL(label, sql);
				}
			}
		}
		catch(SQLException e)
		{
			int dbtype = 0;
			try
			{
				dbtype = curdbt == null?0:curdbt.getDBType();
			}
			catch(Exception ex)
			{
			}
			String s = SQLStateMessage.getMessage(dbtype, e.getSQLState());
			SQLException se = new SQLException((s == null?"":(s + "\r\n")) + "SQL: " + sql + "\r\n" + e.getMessage(),
				e.getSQLState(),
				e.getErrorCode());
			se.initCause(e.getCause());
			se.setNextException(e.getNextException());
			if ("CONTINUE".equals(on_error_proc))
			{
				asse.error(se.getMessage());
			}
			else
			{
				throw se;
			}
		}
		catch(Exception e)
		{
			if ("CONTINUE".equals(on_error_proc))
			{
				asse.error(e.getMessage());
			}
			else
			{
				throw e;
			}
		}
		finally
		{
		}
		return null;
	}

	protected void useDB(String dbname, DBTool dbt)
	{
		curdbt = dbtmap.get(dbname);
		if (curdbt == null)
		{
			if (dbt == null)
			{
				curdbt = DBTool.getDBTool(dbname);
			}
			else
			{
				curdbt = dbt;
			}
			dbtmap.put(dbname, curdbt);
		}
		checkDBConfig();
		curdbt.setLogger(null);
	}

	private void checkDBConfig()
	{
		if (curdbt != null)
		{
			curdbt.refreshConfig();
		}
	}

	private Map[] execQuerySQL(String label, String sql) throws Exception
	{
		if (curdbt == null)
		{
			throw new SQLException("No connection.");
		}
		Map[] md = curdbt.getDBHelper().query(sql);
		if (label != null)
		{
			asse.set(label, md);
		}
		asse.set("", md);
		//
		if (md.length == 1)
		{
			asse.debug("got data: " + Arrays.toString(md));
		}
		else
		{
			asse.debug("got " + md.length + " rows data");
			asse.detail(Arrays.toString(md).replaceAll("\\}\\,\\s\\{", "},\r\n{"));
		}
		return md;
	}

	private Map[] execUpdateSQL(String label, String sql) throws Exception
	{
		if (curdbt == null)
		{
			throw new SQLException("No connection.");
		}
		int r = curdbt.getDBHelper().update(sql);
		Map[] md = new Map[]{bito.util.E.V().newMapSortedByAddTime()};
		md[0].put("UPDATECOUNT", "" + r);
		if (label != null)
		{
			asse.set(label, md);
		}
		asse.set("", md);
		asse.debug(r + " rows affected.");
		return md;
	}

	private Map[] execProcedureSQL(String label, String sql) throws Exception
	{
		if (curdbt == null)
		{
			throw new SQLException("No connection.");
		}
		Map[] params;
		Object pp = asse.get("procedure.parameters");
		if (pp == null)
		{
			params = null;
		}
		else if (pp instanceof List)
		{
			params = ((List<Map>)pp).toArray(new Map[0]);
		}
		else if (pp instanceof Map[])
		{
			params = (Map[])pp;
		}
		else
		{
			throw new Exception("procedure.parameters MUST be array of Maps.");
		}
		Map[] md = curdbt.getDBHelper().execProcedure(sql, params);
		if (params != null)
		{
			// 参数传递到下一个存储过程
			asse.set("procedure.parameters", params);
		}
		if (label != null)
		{
			asse.set(label, md);
		}
		asse.set("", md);
		//
		if (md.length == 1)
		{
			asse.debug("got data: " + Arrays.toString(md));
		}
		else
		{
			asse.debug("got " + md.length + " rows data");
			asse.detail(Arrays.toString(md).replaceAll("\\}\\,\\s\\{", "},\r\n{"));
		}
		return md;
	}

	private Map[] execCommandSQL(String label, String sql) throws Exception
	{
		if (curdbt == null)
		{
			throw new SQLException("No connection.");
		}
		try
		{
			Map[] md = curdbt.getDBHelper().execCommand(sql);
			if (label != null)
			{
				asse.set(label, md);
			}
			asse.set("", md);
			//
			if (md.length == 1)
			{
				asse.debug("got data: " + Arrays.toString(md));
			}
			else
			{
				asse.debug("got " + md.length + " rows data");
				asse.detail(Arrays.toString(md).replaceAll("\\}\\,\\s\\{", "},\r\n{"));
			}
			return md;
		}
		catch(SQLException sqle)
		{
			throw sqle;
		}
		catch(Exception e)
		{
			SQLException se = new SQLException(e.getClass().getName() + ":" + e.getMessage(), "58023", 0);
			se.initCause(e);
			throw se;
		}
	}
}
