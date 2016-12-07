package d;

import java.io.IOException;
import java.util.Map;

import bito.util.dba.DBTool;
import bito.util.logger.Log;
import bito.util.textparser.Parser;

public interface SQLRunner
{
	public void cancel();

	public void setDefaultDBName(String dbname);

	public void setLogger(Log log);

	public void useDB(DBTool dbt);

	public DBTool curDB();

	/**
	 * 返回值 0 正常， 非0异常，具体值由应用定义
	 */
	public int execSQLFile(String sqlfile) throws Exception;

	/**
	 * 返回值 0 正常， 非0异常，具体值由应用定义
	 */
	public int execSQLText(String sqltext) throws Exception;

	/**
	 * 返回值 0 正常， 非0异常，具体值由应用定义
	 */
	public int execSQLText(String[] sqltexts) throws Exception;

	public Map[] getData(String key);

	public void putData(String key, Map[] md) throws Exception;

	public Object getVariable(String name) throws Exception;

	public void setVariable(String name, Object value) throws Exception;

	public Object evalJScript(String js) throws Exception;
}
