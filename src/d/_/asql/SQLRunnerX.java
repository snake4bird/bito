package d._.asql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bito.net.URLReader;
import bito.util.cfg.SystemConfig;
import bito.util.dba.DBHelper;
import bito.util.dba.DBTool;
import bito.util.logger.Log;

/**
 * 
 * ��̬SQL�ű�ִ����
 * 
 */
public class SQLRunnerX implements d.SQLRunner
{
	private String name;
	private Log log;
	private HashMap dbtmap = new HashMap();
	private DBTool curdbt = null;
	private URLReader urlreader = null;
	private HashMap data = new HashMap();
	private SQLScriptRunner sr = null;
	private String on_error_proc = null;
	private String default_dbname = "DB";
	private File curfile = new File("");
	private File curdir = new File(".");
	private int recursion_count = 0;
	private boolean cancel = false;

	public SQLRunnerX(String name)
	{
		this.name = name;
		this.log = new Log((name.length() > 0?":":"") + name);
	}

	public void setDefaultDBName(String dbname)
	{
		this.default_dbname = dbname;
	}

	public void setLogger(Log log)
	{
		this.log = log;
		if (curdbt != null)
		{
			curdbt.setLogger(log);
		}
	}

	public void useDB(DBTool dbt)
	{
		curdbt = dbt;
		checkDBConfig();
		curdbt.setLogger(log);
	}

	protected void useDB(String dbname, DBTool dbt)
	{
		curdbt = (DBTool)dbtmap.get(dbname);
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
		curdbt.setLogger(log);
	}

	private void checkDBConfig()
	{
		if (curdbt != null)
		{
			curdbt.refreshConfig();
		}
	}

	public DBTool curDB()
	{
		return curdbt;
	}

	public File getCurfile()
	{
		return curfile;
	}

	public File getCurdir()
	{
		return curdir;
	}

	public int execSQLFile(String sqlfiles) throws Exception
	{
		String[] sfns = sqlfiles.split(";");
		String[] sqltexts = new String[sfns.length];
		File[] curfiles = new File[sfns.length];
		File[] curdirs = new File[sfns.length];
		for(int i = 0; i < sfns.length; i++)
		{
			String fn = sfns[i].trim();
			if (fn.startsWith("/") || (fn.indexOf(":") > 0 && "\\".equals(System.getProperty("file.separator"))))
			{
				curfiles[i] = new File(fn);
			}
			else
			{
				curfiles[i] = new File(curdir, fn);
			}
			curdirs[i] = curfiles[i].getParentFile();
			sqltexts[i] = d.E.V().readfile(curfiles[i]);
		}
		return execSQLText(curfiles, curdirs, sqltexts);
	}

	public int execSQLText(String sqltext) throws Exception
	{
		return execSQLText(new String[]{sqltext});
	}

	public int execSQLText(String[] sqltexts) throws Exception
	{
		File[] curfiles = new File[sqltexts.length];
		File[] curdirs = new File[sqltexts.length];
		for(int i = 0; i < sqltexts.length; i++)
		{
			curfiles[i] = curfile;
			curdirs[i] = curdir;
		}
		return execSQLText(curfiles, curdirs, sqltexts);
	}

	public void cancel()
	{
		if (sr != null)
		{
			sr.cancel();
		}
		if (curdbt != null)
		{
			try
			{
				curdbt.release();
				curdbt = null;
			}
			catch(Exception e)
			{
				if (log != null)
				{
					log.warn(e);
				}
				else
				{
					e.printStackTrace();
				}
			}
		}
		cancel = true;
	}

	private int execSQLText(File[] curfiles, File[] curdirs, String[] sqltexts) throws Exception
	{
		if (cancel)
		{
			throw new Exception("sqlrunner has been cancelled.");
		}
		if (recursion_count == 0)
		{
			checkDBConfig();
		}
		Map sqlsLabelMap = new HashMap();
		String[] goto_label = new String[]{null};
		int i = 0;
		int pos = 0;
		recursion_count++;
		getScriptRunner().eval_begin();
		try
		{
			while(i < sqltexts.length && !cancel)
			{
				try
				{
					if (sqltexts[i] != null && sqltexts[i].trim().length() > 0)
					{
						File oldcurfile = curfile;
						File oldcurdir = curdir;
						curfile = curfiles[i];
						curdir = curdirs[i];
						try
						{
							execActiveSQLText(sqlsLabelMap, i, sqltexts[i], pos, goto_label);
						}
						finally
						{
							curfile = oldcurfile;
							curdir = oldcurdir;
						}
					}
					i++;
					pos = 0;
				}
				catch(SQLRunnerGotoException e)
				{
					goto_label[0] = e.getLable();
					LabelPosition lp = (LabelPosition)sqlsLabelMap.get(goto_label[0]);
					if (lp != null)
					{
						i = lp.index;
						pos = lp.pos;
						goto_label[0] = null;
					}
					else
					{
						pos = e.getPosition();
					}
				}
			}
			if (goto_label[0] != null)
			{
				if (log != null)
				{
					log.debug("Label \"" + goto_label[0] + "\" not found.");
				}
				return -1;
			}
			return 0;
		}
		catch(SQLRunnerExitException e)
		{
			log.debug("exit " + e.getReturnCode() + "");
			return e.getReturnCode();
		}
		finally
		{
			getScriptRunner().eval_end();
			recursion_count--;
		}
	}

	private class LabelPosition
	{
		public int index;
		public int pos;

		public LabelPosition(int index, int pos)
		{
			this.index = index;
			this.pos = pos;
		}
	}

	private void execActiveSQLText(Map sqlsLabelMap, int index, String sqls, int from_position, String[] goto_label)
		throws Exception
	{
		int[] lineno = new int[]{1, 1}; // {�����У���ʼ��}
		int[] pos = new int[]{from_position}; //{SQL�ַ��������ʼλ��}
		char[] sqlcs = sqls.toCharArray(); //{SQL�ַ�����}
		execActiveSQLText(index, sqlsLabelMap, lineno, sqlcs, pos, goto_label);
	}

	private void execActiveSQLText(int index, Map sqlsLabelMap, int[] lineno, char[] sqlcs, int[] pos,
		String[] goto_label) throws Exception
	{
		try
		{
			String[] comment = new String[]{null};
			int lastpos = pos[0];
			SQLStringSetX sqlstr = getNextSQL(lineno, sqlcs, pos, comment, goto_label[0]);
			while(!cancel && sqlstr != null)
			{
				if (log != null && comment[0] != null && comment[0].length() > 0)
				{
					log.debug(comment[0]);
				}
				comment[0] = null;
				// ���ص�һ��SQLֻ�����ݲ�ͬ,�����ʽ��һ����.
				for(int i = 0; i < sqlstr.size(); i++)
				{
					String label = null;
					String sql = sqlstr.get(i);
					String match[];
					if ((match = SStrMatcher.match(sql, "(?is)\\s*(\\w*)\\s*:\\s*(.*)")).length > 0)
					{
						label = match[0];
						sql = match[1];
						// �ظ� Label �Ḳ��֮ǰ�� Label
						sqlsLabelMap.put(label, new LabelPosition(index, lastpos));
					}
					if (label != null)
					{
						// make label variable default value
						putData(label, new Map[0]);
					}
					if (goto_label[0] == null || goto_label[0].equals(label))
					{
						execSQL(label, sql);
						goto_label[0] = null;
					}
				}
				lastpos = pos[0];
				sqlstr = getNextSQL(lineno, sqlcs, pos, comment, goto_label[0]);
			}
		}
		catch(SQLScriptException e)
		{
			throw e;
		}
		catch(SQLException e)
		{
			e = new SQLScriptException(e.getMessage() + " (" + curfile.getName() + "#" + lineno[1] + ")", e);
			if ("CONTINUE".equals(on_error_proc))
			{
				errorlog(e);
			}
			else
			{
				throw e;
			}
		}
		catch(Exception e)
		{
			if ("CONTINUE".equals(on_error_proc))
			{
				errorlog(e);
			}
			else
			{
				throw e;
			}
		}
		finally
		{
		}
	}

	/**
	 * ����һ�����SQL
	 * @param lineno
	 * @param sqlcs
	 * @param pos
	 * @param comment 
	 * @return
	 * @throws SQLException
	 * @throws SQLRunnerException
	 */
	private SQLStringSetX getNextSQL(int[] lineno, char[] sqlcs, int[] pos, String[] comment, String gotolabel)
		throws SQLException, SQLScriptException
	{
		if (pos[0] >= sqlcs.length)
		{
			return null;
		}
		lineno[1] = lineno[0];
		int istart = pos[0];
		int is = istart;
		boolean start_empty_line = true;
		SQLStringSetX retsql = new SQLStringSetX();
		String fInPairOf = "";
		for(int i = is; i < sqlcs.length; i++)
		{
			if (sqlcs[i] == '\n')
			{
				if (start_empty_line && (start_empty_line = new String(sqlcs, istart, i - istart).trim().length() == 0))
				{
					lineno[1]++;
				}
				lineno[0]++;
			}
			if ("{".equals(fInPairOf))
			{
				//����ű�
				int script_start_line = lineno[0];
				pos[0] = i;
				String script = getScript(lineno, sqlcs, pos);
				fInPairOf = "";
				i = pos[0]; // pos[0] Ϊ script ������� '}' ��ĵ�һ���ַ�
				if (i < sqlcs.length && (sqlcs[i] == '\'' || sqlcs[i] == '\"'))
				{
					// retsql.scriptInString ����ԭֵ
					// ���Խű���Ǻ�ĵ����� ''{}''
					is = i + 1;
					fInPairOf = "" + sqlcs[i];
				}
				else
				{
					// �ű�����ֵ����SQL�����ַ������ʽ�У�����������ת�崦��
					retsql.scriptInString("");
					is = i;
					// ����һ���ַ�����Ϊ��for����л����ټ�һ��
					i--;
				}
				if (gotolabel == null || retsql.matchLabel(gotolabel))
				{
					DataSetValues rv = evalScript(script, pos, script_start_line);
					retsql.appendDataSetValues(rv);
				}
				// ����ȱʡ���ű�����ֵ����SQL�����ַ������ʽ�У�����������ת�崦��
				retsql.scriptInString("");
			}
			else if ("--".equals(fInPairOf) || "//".equals(fInPairOf))
			{
				//��--��ע����ȫ����
				//��һ�м���
				//����ע���е��κ���Ϣ
				if (sqlcs[i] == '\n')
				{
					//end of comment
					fInPairOf = "";
					is = i + 1;
				}
				else
				{
					//skip
				}
			}
			else if ("/*".equals(fInPairOf))
			{
				//��/* */��ע����ȫ����
				//��һ�ַ�����
				//����ע���е��κ���Ϣ
				if (sqlcs[i] == '/' && i > is + 1 && sqlcs[i - 1] == '*')
				{
					//end of comment
					fInPairOf = "";
					is = i + 1;
				}
				else
				{
					//skip
				}
			}
			else if (";".equals(fInPairOf))
			{
				//��;��ע����Ϊdebug��Ϣ���
				//����SQL
				//����ע���е��κ���Ϣ
				if (sqlcs[i] == '\n')
				{
					//end of comment
					fInPairOf = "";
					comment[0] = new String(sqlcs, is + 1, i - is).trim();
					is = i + 1;
					//ע��ֻ����SQL���
					pos[0] = is;
					return retsql;
				}
				else
				{
					//skip
				}
			}
			else if ("\'".equals(fInPairOf) || "\"".equals(fInPairOf))
			{
				//�ַ���
				if (fInPairOf.equals("" + sqlcs[i]))
				{
					fInPairOf = "";
				}
				else
				{
					//skip
				}
			}
			else if (sqlcs[i] == '{')
			{
				//����ű�
				//���Խű����ǰ��ĵ����� ''{}''
				fInPairOf = "" + sqlcs[i];
				int ie = i;
				if (i > 0 && (sqlcs[i - 1] == '\'' || sqlcs[i - 1] == '\"'))
				{
					ie = i - 1;
					// �ű�����ֵ��SQL�����ַ������ʽ�У���Ҫ�ٽ���SQL���ʱ��������ת�崦��
					retsql.scriptInString("" + sqlcs[i - 1]);
				}
				retsql.append(sqlcs, is, ie - is);
				is = i;
			}
			else if (sqlcs[i] == '\'' || sqlcs[i] == '\"')
			{
				//�����ַ���
				fInPairOf = "" + sqlcs[i];
			}
			else if (sqlcs[i] == ';')
			{
				retsql.append(sqlcs, is, i - is);
				is = i;
				fInPairOf = "" + sqlcs[i];
			}
			else if (sqlcs[i] == '-' && i < sqlcs.length - 1 && sqlcs[i + 1] == '-')
			{
				retsql.append(sqlcs, is, i - is);
				i++;
				is = i;
				fInPairOf = "--";
			}
			else if (sqlcs[i] == '/' && i < sqlcs.length - 1 && sqlcs[i + 1] == '/')
			{
				retsql.append(sqlcs, is, i - is);
				i++;
				is = i;
				fInPairOf = "//";
			}
			else if (sqlcs[i] == '/' && i < sqlcs.length - 1 && sqlcs[i + 1] == '*')
			{
				retsql.append(sqlcs, is, i - is);
				i++;
				is = i;
				fInPairOf = "/*";
			}
			else
			{
			}
		}
		//��������
		if (is < sqlcs.length)
		{
			if ("{".equals(fInPairOf))
			{
				//�ű���Ǹտ�ʼ,�����ű��ͽ�����
				throw new SQLException("Script Error: Script start with '{' and end with '}'." + "\r\nscript: {");
			}
			if (!(";".equals(fInPairOf) || "--".equals(fInPairOf) || "/*".equals(fInPairOf)))
			{
				retsql.append(sqlcs, is, sqlcs.length - is);
			}
			else
			{
				// ignore comment
			}
			is = sqlcs.length;
		}
		pos[0] = is;
		return retsql;
	}

	private String getScript(int[] lineno, char[] sqlcs, int[] pos) throws SQLException
	{
		StringBuffer sb = new StringBuffer();
		int fInScript = 1;
		String fInScriptPair = "";
		for(int i = pos[0]; i < sqlcs.length; i++)
		{
			if (sqlcs[i] == '\n')
			{
				lineno[0]++;
			}
			if (fInScriptPair.length() > 0)
			{
				if ("@$$".equals(fInScriptPair))
				{
					//??
				}
				//�ַ�����ע�ʹ���
				else if ("\'".equals(fInScriptPair) || "\"".equals(fInScriptPair))
				{
					// �ַ���
					if (sqlcs[i] == '\r')
					{
						// �����ַ����а����س�����
						sb.append(sqlcs, pos[0], i - pos[0]);
						sb.append("\\r");
						pos[0] = i + 1;
					}
					else if (sqlcs[i] == '\n')
					{
						// �����ַ����а����س�����
						sb.append(sqlcs, pos[0], i - pos[0]);
						sb.append("\\n");
						pos[0] = i + 1;
					}
					else if ("\\".equals(fInScriptPair))
					{
						// �����ַ�����ת���������ַ�
						i++;
						if (sqlcs[i] == '\n')
						{
							lineno[0]++;
						}
					}
					else if (("\'".equals(fInScriptPair) && sqlcs[i] == '\'')
						|| ("\"".equals(fInScriptPair) && sqlcs[i] == '\"'))
					{
						// �ַ�������
						fInScriptPair = "";
					}
					else
					{
						//skip
					}
				}
				else if ("//".equals(fInScriptPair) && sqlcs[i] == '\n')
				{
					// ��ע��
					fInScriptPair = "";
				}
				else if ("/*".equals(fInScriptPair) && i + 1 < sqlcs.length && sqlcs[i] == '*' && sqlcs[i + 1] == '/')
				{
					// ��ע��
					i = i + 1;
					fInScriptPair = "";
				}
				else
				{
					//skip
				}
			}
			else if (sqlcs[i] == '$' && i >= 2 && sqlcs[i - 2] == '@' && sqlcs[i - 1] == '$')
			{
				// ASS��ʼ
				fInScriptPair = "@$$";
				sb.append(sqlcs, pos[0], i - 2 - pos[0]);
				sb.append("runSQL(\"");
				pos[0] = i + 1;
			}
			else if (sqlcs[i] == '\'' || sqlcs[i] == '\"')
			{
				// �ַ�����ʼ
				fInScriptPair = "" + sqlcs[i];
			}
			else if (sqlcs[i] == '/')
			{
				if (i + 1 < sqlcs.length)
				{
					if (sqlcs[i + 1] == '/')
					{
						// ��ע�Ϳ�ʼ
						i = i + 1;
						fInScriptPair = "//";
					}
					else if (sqlcs[i + 1] == '*')
					{
						// ��ע�Ϳ�ʼ
						i = i + 1;
						fInScriptPair = "/*";
					}
					else
					{
						//skip
					}
				}
				else
				{
					//skip
				}
			}
			//Ѱ��script�������
			else if (sqlcs[i] == '{')
			{
				// �ű�Ƕ�׽���
				fInScript++;
			}
			else if (sqlcs[i] == '}')
			{
				// �ű�Ƕ���˳�
				fInScript--;
				if (fInScript == 0)
				{
					// �ű�����
					sb.append(sqlcs, pos[0], i - pos[0]);
					pos[0] = i + 1;
					return sb.toString();
				}
			}
			else
			{
				//skip
			}
		}
		String s = "";
		if (pos[0] < sqlcs.length)
		{
			s = new String(sqlcs, pos[0], sqlcs.length - pos[0]);
		}
		throw new SQLException("Script Error: Script start with '{' and end with '}'." + "\r\nscript: {" + s);
	}

	private void execSQL(String label, String sql) throws Exception
	{
		if (sql == null || sql.length() == 0 || sql.trim().length() == 0)
		{
			return;
		}
		sql = sql.trim();
		if (log != null)
		{
			log.debug((label == null?"":(label + " : ")) + sql);
		}
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
				String info = match[0];
				if (log != null)
				{
					log.info(info);
				}
			}
			else if ((match = SStrMatcher.match(sql, "(?is)((?:skip)|(?:--)|(?://)|(?:ignore))\\s+(.*)")).length > 0)
			{
				//skip a statement
			}
			else
			{
				if (curdbt == null)
				{
					if (log != null)
					{
						log.debug("use default db " + default_dbname);
					}
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
				if ((match = SStrMatcher.match(sql, "(?is)batch\\s+((?:begin)|(?:end))\\s*")).length > 0)
				{
					if ("BEGIN".equals(match[0].toUpperCase()))
					{
						curdbt.getDBHelper().batchBegin();
					}
					else
					// if ("END".equals(match[0].toUpperCase()))
					{
						int[] rs = curdbt.getDBHelper().batchEnd();
						if (log != null)
						{
							log.debug("batch end: " + rs == null?".":Arrays.toString(rs));
						}
					}
				}
				else if (sql.matches("(?is)select\\s.*"))
				{
					execQuerySQL(label, sql);
				}
				else if (sql.matches("(?is)insert\\s.*")
					|| sql.matches("(?is)update\\s.*")
						|| sql.matches("(?is)merge\\s.*")
						|| sql.matches("(?is)delete\\s.*"))
				{
					execUpdateSQL(label, sql);
				}
				else if (sql.matches("(?is)call\\s.*")
					|| sql.matches("(?is)exec\\s.*")
						|| sql.matches("(?is)execute\\s.*"))
				{
					execProcedureSQL(label, sql);
				}
				else
				{
					execCommandSQL(label, sql);
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
				errorlog(se);
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
				errorlog(e);
			}
			else
			{
				throw e;
			}
		}
		finally
		{
		}
	}

	private void errorlog(Exception e)
	{
		if (log != null)
		{
			try
			{
				if (e instanceof SQLException)
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
	}

	private void execCommandSQL(String label, String sql) throws Exception
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
				putData(label, md);
			}
			putData("", md);
			//
			if (log != null)
			{
				if (md.length == 1)
				{
					log.debug("got data: " + Arrays.toString(md));
				}
				else
				{
					log.debug(("got " + md.length + " rows data")
						+ ((log.getLevel() > Log.level_debug)?(":\r\n" + Arrays.toString(md).replaceAll("\\}\\,\\s\\{",
							"},\r\n{")):"."));
				}
			}
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

	private void execUpdateSQL(String label, String sql) throws Exception
	{
		if (curdbt == null)
		{
			throw new SQLException("No connection.");
		}
		int r = curdbt.getDBHelper().update(sql);
		Map[] md = new Map[]{d.E.V().newMapSortedByAddTime()};
		md[0].put("UPDATECOUNT", "" + r);
		if (label != null)
		{
			putData(label, md);
		}
		putData("", md);
		if (log != null)
		{
			log.debug(r + " rows affected.");
		}
	}

	private void execQuerySQL(String label, String sql) throws Exception
	{
		if (curdbt == null)
		{
			throw new SQLException("No connection.");
		}
		Map[] md = curdbt.getDBHelper().query(sql);
		if (label != null)
		{
			putData(label, md);
		}
		putData("", md);
		//
		if (log != null)
		{
			if (md.length == 1)
			{
				log.debug("got data: " + Arrays.toString(md));
			}
			else
			{
				log.debug(("got " + md.length + " rows data")
					+ ((log.getLevel() > Log.level_debug)?(":\r\n" + Arrays.toString(md).replaceAll("\\}\\,\\s\\{",
						"},\r\n{")):"."));
			}
		}
	}

	private void execProcedureSQL(String label, String sql) throws Exception
	{
		if (curdbt == null)
		{
			throw new SQLException("No connection.");
		}
		Map[] params;
		Object pp = getVariable("procedure.parameters");
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
			setVariable("procedure.parameters", params);
		}
		if (label != null)
		{
			putData(label, md);
		}
		putData("", md);
		//
		if (log != null)
		{
			if (md.length == 1)
			{
				log.debug("got data: " + Arrays.toString(md));
			}
			else
			{
				log.debug(("got " + md.length + " rows data")
					+ ((log.getLevel() > Log.level_debug)?(":\r\n" + Arrays.toString(md).replaceAll("\\}\\,\\s\\{",
						"},\r\n{")):"."));
			}
		}
	}

	public Map[] getData(String key)
	{
		return (Map[])data.get(key);
	}

	public void putData(String key, Map[] md) throws Exception
	{
		data.put(key, md);
		setVariable(key, md);
	}

	public String loadURL(String url, String charset) throws Exception
	{
		if (urlreader == null)
		{
			urlreader = new URLReader();
		}
		byte[] bs = urlreader.ReadURL(url);
		return (charset == null)?new String(bs):new String(bs, charset);
	}

	public String loadFile(String filename) throws IOException
	{
		try
		{
			return d.E.V().readfile(new File(filename));
		}
		catch(FileNotFoundException fnfe)
		{
			return SQLScriptObject.not_found;
		}
	}

	public void saveFile(String filename, String content) throws IOException
	{
		d.E.V().writefile(new File(filename), content);
	}

	public void exit(int returnCode) throws SQLRunnerExitException
	{
		throw new SQLRunnerExitException(returnCode);
	}

	public void gotoLabel(String label) throws SQLRunnerGotoException
	{
		throw new SQLRunnerGotoException(label);
	}

	public Object getVariable(String name) throws Exception
	{
		return getScriptRunner().get(name);
	}

	public void setVariable(String name, Object value) throws Exception
	{
		getScriptRunner().put(name, value);
	}

	public Object evalJScript(String js) throws SQLScriptException, Exception
	{
		return getScriptRunner().quick_eval(js);
	}

	private SQLScriptRunner getScriptRunner() throws SQLScriptException
	{
		if (sr == null)
		{
			sr = new SQLScriptRunner(name);
			sr.put("ev", d.E.V());
			sr.put("runner", this);
			sr.put("log", log);
			{
				sr.eval_begin();
				try
				{
					sr.pure_eval("function config(key)" + " { return runner.config(key); }", 1, "runner");
					sr.pure_eval("function include(sqlfile){ return runner.execSQLFile(sqlfile);}", 1, "runner");
					sr.pure_eval("function exit(code)" + " { runner.exit(code); }", 1, "runner");
					sr.pure_eval("function goto(label)" + " { runner.gotoLabel(label); }", 1, "runner");
					sr.pure_eval("function sleep(ms)" + " { runner.sleep(ms); }", 1, "runner");
					sr.pure_eval("function runSQL(sqltext)" + " { return runner.execSQLText(sqltext); }", 1, "runner");
					sr.pure_eval("function loadFile(fn)" + " { return runner.loadFile(fn); }", 1, "runner");
					sr.pure_eval("function saveFile(fn, s)" + " { runner.saveFile(fn, s); }", 1, "runner");
					sr.pure_eval("function loadURL(fn)" + " { return runner.loadURL(fn, null); }", 1, "runner");
					sr.pure_eval("function loadURL(fn, cs)" + " { return runner.loadURL(fn, cs); }", 1, "runner");
				}
				finally
				{
					sr.eval_end();
				}
			}
		}
		else
		{
			sr.put("log", log);
		}
		return sr;
	}

	public void sleep(long ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch(InterruptedException e)
		{
		}
	}

	public Object config(String key)
	{
		return SystemConfig.get(key, "");
	}

	private DataSetValues evalScript(String oscript, int[] pos, int script_start_line) throws SQLException,
		SQLScriptException
	{
		String script = oscript;
		try
		{
			return getScriptRunner().evalSQLScript(null, script, curfile.getName(), script_start_line);
		}
		catch(SQLScriptException e)
		{
			e.setPosition(pos[0]);
			throw e;
		}
	}

	public void setCurFile(String dirname, String filename)
	{
		curdir = new File(dirname);
		curfile = new File(curdir, filename);
	}
}
