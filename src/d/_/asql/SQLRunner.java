package d._.asql;

import java.util.Map;

import bito.util.dba.DBTool;
import bito.util.logger.Log;

/**
 * 
 * ¶¯Ì¬SQL½Å±¾Ö´ÐÐÆ÷
 * 
 */
public class SQLRunner implements d.SQLRunner
{
	private ASS ass;

	public SQLRunner(String name)
	{
		ass = new ASS(name);
	}

	public void setDefaultDBName(String dbname)
	{
		ass.setDefaultDBName(dbname);
	}

	public void setLogger(Log log)
	{
		ass.setLogger(log);
	}

	public void useDB(DBTool dbt)
	{
		ass.useDB(dbt);
	}

	public DBTool curDB()
	{
		return ass.curDB();
	}

	public int execSQLFile(String sqlfiles) throws Exception
	{
		return ass.execSQLFile(sqlfiles);
	}

	public int execSQLText(String sqltext) throws Exception
	{
		return ass.execSQLText(sqltext);
	}

	public int execSQLText(String[] sqltexts) throws Exception
	{
		return ass.execSQLText(sqltexts);
	}

	public void cancel()
	{
		ass.cancel();
	}

	public Map[] getData(String key)
	{
		return ass.getData(key);
	}

	public void putData(String key, Map[] md) throws Exception
	{
		ass.putData(key, md);
	}

	public Object getVariable(String name) throws Exception
	{
		return ass.getVariable(name);
	}

	public void setVariable(String name, Object value) throws Exception
	{
		ass.setVariable(name, value);
	}

	public Object evalJScript(String js) throws Exception
	{
		return ass.evalJScript(js);
	}
}
