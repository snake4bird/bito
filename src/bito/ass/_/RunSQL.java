package bito.ass._;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import d.SQLRunner;
import bito.util.cfg.SystemConfig;
import bito.util.dba.DBTool;
import bito.util.logger.Log;

public class RunSQL
{
	private String procname;
	private Log log;
	private String dbname = "DB";
	private SQLRunner sqlrunner;
	private boolean cancel = false;
	private ProcThreadManager ptmanager;

	public RunSQL(ProcThreadManager ptmanager, String procname)
	{
		this.ptmanager = ptmanager;
		this.procname = procname;
		this.log = new Log(":" + procname);
		log.setThreadLocalCatlog(procname);
	}

	public void setVariable(String key, Object value) throws Exception
	{
		initSQLRunner();
		sqlrunner.setVariable(key, value);
	}

	public Object getVariable(String key) throws Exception
	{
		initSQLRunner();
		return sqlrunner.getVariable(key);
	}

	public Object evalJScript(String script) throws Exception
	{
		initSQLRunner();
		return sqlrunner.evalJScript(script);
	}

	private void initSQLRunner() throws Exception
	{
		if (sqlrunner == null)
		{
			sqlrunner = d.E.V().newSQLRunner(procname);
			sqlrunner.setVariable("procman", ptmanager);
			sqlrunner.setVariable("procname", procname);
		}
	}

	public int run(String sqlfilenames) throws Exception
	{
		int returnCode = -1;
		initSQLRunner();
		sqlrunner.setLogger(log);
		sqlrunner.setDefaultDBName(dbname);
		if (log != null)
		{
			log.info("begin.");
		}
		try
		{
			returnCode = sqlrunner.execSQLFile(sqlfilenames);
			if (returnCode != 0)
			{
				if (log != null)
				{
					log.info("break " + returnCode + ".");
				}
			}
		}
		catch(Exception e)
		{
			errorlog(e);
		}
		if (log != null)
		{
			if (cancel)
			{
				log.info("cancel.");
			}
			log.info("end.");
		}
		return returnCode;
	}

	public void cancel()
	{
		cancel = true;
		if (sqlrunner != null)
		{
			sqlrunner.cancel();
		}
	}

	private void errorlog(Exception e) throws Exception
	{
		if (log != null)
		{
			try
			{
				if (e instanceof SQLException || e instanceof FileNotFoundException)
				{
					log.error(e.getMessage());
				}
				else
				{
					log.error(e);
				}
			}
			catch(Exception ex)
			{
				log.error(e);
			}
		}
		else
		{
			throw e;
		}
	}
}
