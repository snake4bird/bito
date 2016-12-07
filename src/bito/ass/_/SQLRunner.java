package bito.ass._;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.mozilla.javascript.Script;

import bito.net.URLReader;
import bito.util.cfg.SystemConfig;
import bito.util.dba.DBTool;
import bito.util.logger.Log;

public class SQLRunner
{
	class SourceInfo
	{
		final String source;
		final String dirname;
		final String filename;

		SourceInfo(String source, String dirname, String filename)
		{
			this.source = source;
			this.dirname = dirname;
			this.filename = filename;
		}
	}

	abstract class Section
	{
		final long id = bito.util.E.V().getStamp();
		final SourceInfo sourceinfo;
		final Section parent;
		final int start_line_no;
		final int start_char_index;
		int index_in_parent;
		int end_char_index = 0;
		int end_line_no = 0;
		ArrayList<Section> subsections = null;

		public Section(Section parent)
		{
			this(parent, parent.sourceinfo, -1, -1);
		}

		public Section(Section parent, int start_char_index, int start_line_no)
		{
			this(parent, parent.sourceinfo, start_char_index, start_line_no);
		}

		public Section(Section parent, SourceInfo sourceinfo, int start_char_index, int start_line_no)
		{
			this.sourceinfo = sourceinfo;
			this.parent = parent;
			// final variables default value
			int _index_in_parent = 0;
			int _start_char_index = 0;
			int _start_line_no = 0;
			// final variables auto value detect by parent and previous
			if (parent != null)
			{
				if (parent.subsections == null)
				{
					parent.subsections = new ArrayList();
				}
				_index_in_parent = parent.subsections.size();
				if (_index_in_parent > 0)
				{
					Section prev = parent.subsections.get(_index_in_parent - 1);
					_start_char_index = prev.end_char_index;
					_start_line_no = prev.end_line_no;
				}
				else
				{
					_start_char_index = parent.end_char_index;
					_start_line_no = parent.end_line_no;
				}
				parent.subsections.add(this);
			}
			this.index_in_parent = _index_in_parent;
			// 优先级: 参数设置值 -> 自动判定值 -> 缺省值
			this.start_char_index = start_char_index >= 0?start_char_index:_start_char_index;
			this.start_line_no = start_line_no >= 0?start_line_no:_start_line_no;
			scanCharIndexTo(this.start_char_index);
			scanLineNoTo(this.start_line_no);
			idsMapping.put(this.id, this);
		}

		void remove()
		{
			idsMapping.remove(this.id);
			if (parent != null
				&& index_in_parent >= 0
					&& index_in_parent < parent.subsections.size()
					&& parent.subsections.get(index_in_parent) == this)
			{
				parent.subsections.remove(index_in_parent);
				for(int i = parent.subsections.size() - 1; i >= index_in_parent; i--)
				{
					parent.subsections.get(i).index_in_parent = i;
				}
			}
			if (subsections != null && subsections.size() > 0)
			{
				for(int i = subsections.size() - 1; i >= 0; i--)
				{
					subsections.get(i).remove();
				}
			}
		}

		void scanLineNoTo(int end_line_no)
		{
			this.end_line_no = end_line_no;
			parent.scanLineNoTo(end_line_no);
		}

		void scanCharIndexTo(int char_index_to)
		{
			this.end_char_index = char_index_to;
			parent.scanCharIndexTo(char_index_to);
		}

		public String toString()
		{
			return (sourceinfo == null?"":sourceinfo.filename)
				+ "["
					+ this.start_line_no
					+ "-"
					+ this.end_line_no
					+ "]"
					+ this.subsections == null?(sourceinfo == null || sourceinfo.source == null?"" //
					:sourceinfo.source.substring(start_char_index, end_char_index)) //
					:this.subsections.toString().replaceAll(", ", ",\r\n");
		}
	}

	class Comment extends Section
	{
		final String comment;

		Comment(Section parent, String comment)
		{
			super(parent);
			this.comment = comment;
		}
	}

	class Segment extends Section
	{
		final String segment;

		public Segment(Section st, String segment)
		{
			super(st);
			if (st.subsections != null && st.subsections.size() > 1)
			{
				// the last is this one.
				Section last_sect = st.subsections.get(st.subsections.size() - 2);
				if (last_sect instanceof Segment)
				{
					segment = ((Segment)last_sect).segment + segment;
					last_sect.remove();
				}
			}
			else
			{
				segment = segment.replaceFirst("(?s)^\\s*", "");
			}
			this.segment = segment;
		}
	}

	class JScript extends Section
	{
		final String string;
		final Script script;

		JScript(Statement parent, String script_string, int start_line_no) throws SQLScriptException
		{
			super(parent, parent.sourceinfo, -1, start_line_no);
			this.string = script_string;
			this.script = scriptRunner.complie(script_string, sourceinfo.filename, start_line_no);
		}
	}

	class Statement extends Section
	{
		String label;

		public Statement(Section parent)
		{
			super(parent);
		}

		public Statement(Section parent, int start_char_index, int start_line_no)
		{
			super(parent, start_char_index, start_line_no);
		}

		public void setLabel(String label)
		{
			this.label = label;
			// 重复 Label 会覆盖之前的 Label
			labelsMapping.put(label, this);
		}
	}

	class NestStatement extends Statement
	{
		public NestStatement(Section parent)
		{
			super(parent);
		}

		public NestStatement(Section parent, int start_char_index, int start_line_no)
		{
			super(parent, start_char_index, start_line_no);
		}
	}

	class Entire extends Section
	{
		public Entire(Entire parent, SourceInfo sourceinfo, int start_char_index, int start_line_no)
		{
			super(parent, sourceinfo, start_char_index, start_line_no);
		}

		void scanLineNoTo(int end_line_no)
		{
			this.end_line_no = end_line_no;
		}

		void scanCharIndexTo(int char_index_to)
		{
			this.end_char_index = char_index_to;
		}
	}

	private String name;
	private SQLScriptRunner scriptRunner;
	private HashMap<String, Statement> labelsMapping = new HashMap();
	private TreeMap<Long, Section> idsMapping = new TreeMap();
	private HashMap dbtmap = new HashMap();
	private DBTool curdbt = null;
	private Log log;
	private boolean cancel = false;
	private int recursion_count = 0;
	private HashMap data = new HashMap();
	private String on_error_proc = null;
	private String default_dbname = "DB";
	private URLReader urlreader = null;
	private File curfile = new File("");
	private File curdir = new File(".");

	public SQLRunner(String name)
	{
		this.name = name;
		this.log = new Log((name.length() > 0?":":"") + name);
	}

	private SQLScriptRunner getScriptRunner() throws SQLScriptException
	{
		if (scriptRunner == null)
		{
			scriptRunner = new SQLScriptRunner(name);
			scriptRunner.put("ev", bito.util.E.V());
			scriptRunner.put("runner", this);
			scriptRunner.put("log", log);
			{
				scriptRunner.eval_begin();
				try
				{
					scriptRunner.pure_eval("function config(key)" + " { return runner.config(key); }", 1, "runner");
					scriptRunner.pure_eval("function include(sqlfile){ return runner.execSQLFile(sqlfile);}",
						1,
						"runner");
					scriptRunner.pure_eval("function exec(code)" + " { runner.exec(code); }", 1, "runner");
					scriptRunner.pure_eval("function exit(code)" + " { runner.exit(code); }", 1, "runner");
					scriptRunner.pure_eval("function goto(label)" + " { runner.gotoLabel(label); }", 1, "runner");
					scriptRunner.pure_eval("function sleep(ms)" + " { runner.sleep(ms); }", 1, "runner");
					scriptRunner.pure_eval("function runSQL(sqltext)" + " { return runner.execSQLText(sqltext); }",
						1,
						"runner");
					scriptRunner.pure_eval("function loadFile(fn)" + " { return runner.loadFile(fn); }", 1, "runner");
					scriptRunner.pure_eval("function saveFile(fn, s)" + " { runner.saveFile(fn, s); }", 1, "runner");
					scriptRunner.pure_eval("function loadURL(fn)" + " { return runner.loadURL(fn, null); }",
						1,
						"runner");
					scriptRunner.pure_eval("function loadURL(fn, cs)" + " { return runner.loadURL(fn, cs); }",
						1,
						"runner");
				}
				finally
				{
					scriptRunner.eval_end();
				}
			}
		}
		else
		{
			scriptRunner.put("log", log);
		}
		return scriptRunner;
	}

	public int exec(long statementid) throws Exception
	{
		int ret = exec(idsMapping.get(statementid));
		scriptRunner.pure_eval("", 0, "");
		return ret;
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
		this.log = log;
		if (curdbt != null)
		{
			curdbt.setLogger(log);
		}
		if (scriptRunner != null)
		{
			scriptRunner.put("log", log);
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

	private int exec(Section section) throws Exception
	{
		if (cancel)
		{
			throw new Exception("sqlrunner has been cancelled.");
		}
		if (recursion_count == 0)
		{
			checkDBConfig();
		}
		recursion_count++;
		scriptRunner.eval_begin();
		try
		{
			boolean run_next = false;
			while(!cancel && section != null)
			{
				try
				{
					Section cur_section = section;
					execOnce(cur_section);
					section = null;
					if (run_next)
					{
						//使用过goto,需要运行至parent的所有statement结束
						//不建议使用goto
						int n = cur_section.index_in_parent + 1;
						Section p = (Section)cur_section.parent;
						if (p != null && n < p.subsections.size())
						{
							Section nextSection = p.subsections.get(n);
							if (nextSection instanceof NestStatement)
							{
								// ignore, NestStatement will run in JScript by exec(nst.id)
							}
							else if (nextSection instanceof Statement)
							{
								section = (Statement)nextSection;
							}
						}
					}
				}
				catch(SQLRunnerGotoException e)
				{
					section = labelsMapping.get(e.getLable());
					if (section == null)
					{
						if (log != null)
						{
							log.warn("Label \"" + e.getLable() + "\" not found.");
						}
						return -1;
					}
					run_next = true;
				}
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
			scriptRunner.eval_end();
			recursion_count--;
		}
	}

	private void execOnce(Section st) throws Exception
	{
		try
		{
			if (st.subsections == null)
			{
				return;
			}
			SQLStringSet retsql = new SQLStringSet();
			Iterator<Section> subsections_iterator = st.subsections.iterator();
			while(!cancel && subsections_iterator.hasNext())
			{
				Section subsection = subsections_iterator.next();
				if (subsection instanceof Comment)
				{
					if (log != null)
					{
						log.debug(((Comment)subsection).comment);
					}
				}
				else if (subsection instanceof Segment)
				{
					retsql.append(((Segment)subsection).segment);
				}
				else if (subsection instanceof JScript)
				{
					JScript js = ((JScript)subsection);
					setCurFile(js.sourceinfo.dirname, js.sourceinfo.filename);
					DataSetValues dvs = scriptRunner.evalSQLScript(js.script,
						js.string,
						js.sourceinfo.filename,
						js.start_line_no);
					retsql.appendDataSetValues(dvs);
				}
				else if (subsection instanceof NestStatement)
				{
					// ignore, NestStatement will run in JScript by exec(nst.id)
				}
				else if (subsection instanceof Statement)
				{
					Statement statement = (Statement)subsection;
					execOnce(statement);
				}
				else if (subsection instanceof Entire)
				{
					execOnce((Entire)subsection);
				}
				else
				{
					throw new Exception("Impossible: not support exec " + subsection.getClass());
				}
			}
			if (st instanceof Statement)
			{
				retsql.end();
				Statement statement = (Statement)st;
				for(int i = 0; i < retsql.size(); i++)
				{
					String sql = retsql.get(i);
					if (statement.label != null)
					{
						// make label variable default value
						putData(statement.label, new Map[0]);
					}
					execSQL(((Statement)st).label, sql);
				}
			}
		}
		catch(SQLScriptException e)
		{
			throw e;
		}
		catch(SQLException e)
		{
			e = new SQLScriptException(e.getMessage()
				+ " ("
					+ st.sourceinfo.filename
					+ "#"
					+ st.start_line_no
					+ "-"
					+ st.end_line_no
					+ ")", e);
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

	private Entire append(Entire entire, String ass, String dirname, String filename, int firstLineNo)
		throws SQLException
	{
		scriptRunner.eval_begin();
		try
		{
			Entire se = new Entire(entire, new SourceInfo(ass, dirname, filename), 0, firstLineNo);
			scan(ass.toCharArray(), se);
			return se;
		}
		finally
		{
			scriptRunner.eval_end();
		}
	}

	private void scan(char[] sqlcs, Entire se) throws SQLException
	{
		Statement subst;
		while((subst = getNext(sqlcs, se, false)) != null)
		{
			//scan one ass file or text
		}
	}

	private Statement getNext(char[] sqlcs, Section pse, boolean recursiveInScript) throws ASSException,
		SQLScriptException
	{
		if (pse.end_char_index >= sqlcs.length)
		{
			//scan is end.
			return null;
		}
		int i_line_no = pse.end_line_no;
		int i_start = pse.end_char_index;
		// skip start empty line
		for(; i_start < sqlcs.length && Character.isWhitespace(sqlcs[i_start]); i_start++)
		{
			if (sqlcs[i_start] == '\n')
			{
				i_line_no++;
			}
		}
		if (i_start == sqlcs.length)
		{
			//scan is end.
			pse.scanCharIndexTo(i_start);
			pse.scanLineNoTo(i_line_no);
			return null;
		}
		if (recursiveInScript
			&& sqlcs[i_start] == '@'
				&& i_start + 2 < sqlcs.length
				&& sqlcs[i_start + 1] == '$'
				&& sqlcs[i_start + 2] == '$')
		{
			// @$$ 嵌套结束 
			pse.scanCharIndexTo(i_start + 3);
			pse.scanLineNoTo(i_line_no);
			return null;
		}
		//
		String fInPairOf = "";
		//char fScriptInQuote = '\0';
		Statement st = new Statement(pse, i_start, i_line_no);
		for(int i = i_start; i < sqlcs.length; i++)
		{
			if (sqlcs[i] == '\n')
			{
				i_line_no++;
			}
			/////////////////////////////////////////////////////////////
			// fInPairOf is empty
			if (fInPairOf.isEmpty())
			{
				if (recursiveInScript
					&& sqlcs[i] == '@'
						&& i + 2 < sqlcs.length
						&& sqlcs[i + 1] == '$'
						&& sqlcs[i + 2] == '$')
				{
					Segment segment = new Segment(st, new String(sqlcs, i_start, i - i_start));
					segment.scanCharIndexTo(i);
					segment.scanLineNoTo(i_line_no);
					// 跳过js内嵌ass @$$结束标识
					st.scanCharIndexTo(i + 3);
					i_start = i + 3;
					log.warn("include @$$ flag in recursive SQL line[" + i_line_no + "].");
					return st;
				}
				else if (st.label == null && sqlcs[i] == ':')
				{
					if (st.subsections != null && st.subsections.size() > 0)
					{
						ArrayList<Section> ssss = st.subsections;
						for(int ssssi = 0; ssssi < ssss.size(); ssssi++)
						{
							if (!(st.subsections.get(ssssi) instanceof Comment)
								&& !((st.subsections.get(ssssi) instanceof Segment) && ((Segment)st.subsections
									.get(ssssi)).segment.trim().isEmpty()))
							{
								throw new ASSException("Script Error: Unsupported anything before label.",
									st.sourceinfo.filename,
									st.sourceinfo.source.substring(st.start_char_index, i + 1),
									st.start_line_no,
									i_line_no);
							}
						}
					}
					st.setLabel((new String(sqlcs, i_start, i - i_start)).trim());
					st.scanCharIndexTo(i);
					st.scanLineNoTo(i_line_no);
					i_start = i + 1;
				}
				else if (sqlcs[i] == '{')
				{
					//保存当前内容
					Segment segment = new Segment(st, new String(sqlcs, i_start, i - i_start));
					segment.scanCharIndexTo(i);
					segment.scanLineNoTo(i_line_no);
					i_start = i + 1;
					st.scanCharIndexTo(i_start);
					//处理脚本
					JScript js = getScript(sqlcs, st);
					// st.end_char_index 为 script 结束标记 '}' 后的第一个字符
					i = js.end_char_index;
					i_line_no = js.end_line_no;
					i_start = i + 1;
					//					//忽略脚本标记前后的单引号 ''{}''
					//					int ie = i;
					//					if (i > 0 && (sqlcs[i - 1] == '\'' || sqlcs[i - 1] == '\"'))
					//					{
					//						ie = i - 1;
					//						// 脚本返回值在SQL语句的字符串表达式中，需要再接入SQL语句时做单引号转义处理
					//						fScriptInQuote = sqlcs[ie];
					//					}
					//					else
					//					{
					//						fScriptInQuote = '\0';
					//					}
				}
				else if (sqlcs[i] == '\'' || sqlcs[i] == '\"')
				{
					//处理字符串
					fInPairOf = "" + sqlcs[i];
				}
				else if (sqlcs[i] == ';')
				{
					Segment segment = new Segment(st, new String(sqlcs, i_start, i - i_start));
					segment.scanCharIndexTo(i);
					segment.scanLineNoTo(i_line_no);
					i_start = i + 1;
					fInPairOf = "" + sqlcs[i];
				}
				else if (sqlcs[i] == '-' && i < sqlcs.length - 1 && sqlcs[i + 1] == '-')
				{
					Segment segment = new Segment(st, new String(sqlcs, i_start, i - i_start));
					segment.scanCharIndexTo(i);
					segment.scanLineNoTo(i_line_no);
					i++;
					i_start = i + 1;
					fInPairOf = "--";
				}
				else if (sqlcs[i] == '/' && i < sqlcs.length - 1 && sqlcs[i + 1] == '/')
				{
					Segment segment = new Segment(st, new String(sqlcs, i_start, i - i_start));
					segment.scanCharIndexTo(i);
					segment.scanLineNoTo(i_line_no);
					i++;
					i_start = i + 1;
					fInPairOf = "//";
				}
				else if (sqlcs[i] == '/' && i < sqlcs.length - 1 && sqlcs[i + 1] == '*')
				{
					Segment segment = new Segment(st, new String(sqlcs, i_start, i - i_start));
					segment.scanCharIndexTo(i);
					segment.scanLineNoTo(i_line_no);
					i++;
					i_start = i + 1;
					fInPairOf = "/*";
				}
				else
				{
					// continue
				}
			}
			//			else if ("{".equals(fInPairOf))
			//			{
			//处理脚本
			//				Script script = getScript(sqlcs, st);
			//				// st.end_char_index 为 script 结束标记 '}' 后的第一个字符
			//				i = st.end_char_index;
			//				i_line_no = st.end_line_no;
			//				if (i < sqlcs.length && fScriptInQuote != '\0' && sqlcs[i] == fScriptInQuote)
			//				{
			//					// 忽略脚本标记后的单引号 ''{}''
			//					i_start = i + 1;
			//					i = i_start;
			//					// 继续字符串处理
			//					fInPairOf = "" + sqlcs[i];
			//				}
			//				else
			//				{
			//					fInPairOf = "";
			//					// 脚本不在SQL语句的字符串表达式中，不做单引号转义处理
			//					fScriptInQuote = '\0';
			//					i_start = i;
			//				}
			//				JScript jscript = new JScript(st, script, fScriptInQuote);
			//				jscript.scanCharIndexTo(i);
			//				jscript.scanLineNoTo(i_line_no);
			//				// 重置缺省，脚本返回值不在SQL语句的字符串表达式中，不做单引号转义处理
			//				fScriptInQuote = '\0';
			//			}
			else if ("--".equals(fInPairOf) || "//".equals(fInPairOf))
			{
				//“--”注释完全忽略
				//下一行继续
				//忽略注释中的任何信息
				if (sqlcs[i] == '\n')
				{
					//end of comment
					st.scanCharIndexTo(i);
					st.scanLineNoTo(i_line_no);
					fInPairOf = "";
					i_start = i + 1;
				}
				else
				{
					//skip
				}
			}
			else if ("/*".equals(fInPairOf))
			{
				//“/* */”注释完全忽略
				//下一字符继续
				//忽略注释中的任何信息
				if (sqlcs[i] == '/' && i > i_start && sqlcs[i - 1] == '*')
				{
					//end of comment
					st.scanCharIndexTo(i);
					st.scanLineNoTo(i_line_no);
					fInPairOf = "";
					i_start = i + 1;
				}
				else
				{
					//skip
				}
			}
			else if (";".equals(fInPairOf))
			{
				//“;”注释作为debug信息输出
				//返回SQL
				//忽略注释中的任何信息
				if (sqlcs[i] == '\n')
				{
					//end of comment
					fInPairOf = "";
					Comment comment = new Comment(st, new String(sqlcs, i_start + 1, i - i_start).trim());
					//注释只能在SQL最后
					comment.scanCharIndexTo(i);
					comment.scanLineNoTo(i_line_no - 1);
					return st;
				}
				else
				{
					//skip
				}
			}
			else if ("\'".equals(fInPairOf) || "\"".equals(fInPairOf))
			{
				//字符串
				if (fInPairOf.equals("" + sqlcs[i]))
				{
					fInPairOf = "";
				}
				else
				{
					//skip
				}
			}
			else
			{
				throw new ASSException("Impossible: 不认识的组对标识",
					st.sourceinfo.filename,
					st.sourceinfo.source.substring(i_start, i),
					st.end_line_no,
					i_line_no);
			}
		}
		//结束处理
		{
			if ("{".equals(fInPairOf))
			{
				//脚本标记刚开始,整个脚本就结束了
				throw new ASSException("Script Error: Script start with '{' and end with '}'." + "\r\nscript: {",
					st.sourceinfo.filename,
					st.sourceinfo.source.substring(i_start, sqlcs.length),
					st.end_line_no,
					i_line_no);
			}
			if (";".equals(fInPairOf))
			{
				Comment comment = new Comment(st, new String(sqlcs, i_start, sqlcs.length - i_start));
				comment.scanCharIndexTo(sqlcs.length);
				comment.scanLineNoTo(i_line_no);
			}
			else if (!("--".equals(fInPairOf) || "//".equals(fInPairOf) || "/*".equals(fInPairOf)))
			{
				Segment segment = new Segment(st, new String(sqlcs, i_start, sqlcs.length - i_start));
				segment.scanCharIndexTo(sqlcs.length);
				segment.scanLineNoTo(i_line_no);
			}
			else
			{
				// ignore comment
				st.scanCharIndexTo(sqlcs.length);
				st.scanLineNoTo(i_line_no);
			}
		}
		return st;
	}

	private JScript getScript(char[] sqlcs, Statement st) throws ASSException, SQLScriptException
	{
		StringBuffer sb = new StringBuffer();
		int i_line_no = st.end_line_no;
		int i_start = st.end_char_index;
		int start_line_no = i_line_no;
		int start_char_index = i_start;
		int fInScript = 1;
		String fInScriptPair = "";
		for(int i = i_start; i < sqlcs.length; i++)
		{
			if (sqlcs[i] == '\n')
			{
				i_line_no++;
			}
			if (fInScriptPair.isEmpty())
			{
				if (sqlcs[i] == '@' && i + 2 < sqlcs.length && sqlcs[i + 1] == '$' && sqlcs[i + 2] == '$')
				{
					// 嵌套ASS @$$
					sb.append(sqlcs, i_start, i - i_start);
					st.scanCharIndexTo(i + 3);
					st.scanLineNoTo(i_line_no);
					NestStatement nst = new NestStatement(st, i + 3, i_line_no);
					Statement subst = getNext(sqlcs, nst, true);
					while(subst != null)
					{
						subst = getNext(sqlcs, nst, true);
					}
					sb.append("exec(").append(nst.id).append(");");
					sb.append("/*");
					sb.append(new String(sqlcs, i, st.end_char_index - i).replaceAll("\\*\\/", "**"));
					sb.append("*/");
					i = st.end_char_index;
					i_line_no = st.end_line_no;
					i_start = i;
				}
				else if (sqlcs[i] == '\'' || sqlcs[i] == '\"')
				{
					// 字符串开始
					fInScriptPair = "" + sqlcs[i];
				}
				else if (sqlcs[i] == '/')
				{
					if (i + 1 < sqlcs.length)
					{
						if (sqlcs[i + 1] == '/')
						{
							// 行注释开始
							i = i + 1;
							fInScriptPair = "//";
						}
						else if (sqlcs[i + 1] == '*')
						{
							// 块注释开始
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
				//寻找script结束标记
				else if (sqlcs[i] == '{')
				{
					// 脚本嵌套进入
					fInScript++;
				}
				else if (sqlcs[i] == '}')
				{
					// 脚本嵌套退出
					fInScript--;
					if (fInScript == 0)
					{
						// 脚本结束，不要最后一个'}'
						sb.append(sqlcs, i_start, i - i_start);
						String script_string = sb.toString();
						JScript jscript = new JScript(st, script_string, start_line_no);
						jscript.scanCharIndexTo(i);
						jscript.scanLineNoTo(i_line_no);
						return jscript;
					}
				}
				else
				{
					//skip
				}
			}
			else if ("\'".equals(fInScriptPair) || "\"".equals(fInScriptPair))
			{
				//字符串处理
				if (sqlcs[i] == '\r')
				{
					// 允许字符串中包含回车换行
					sb.append(sqlcs, i_start, i - i_start);
					// 转义回车换行字符
					sb.append("\\r");
					// 跳过实际回车换行
					i_start = i + 1;
				}
				else if (sqlcs[i] == '\n')
				{
					// 允许字符串中包含回车换行
					sb.append(sqlcs, i_start, i - i_start);
					// 转义回车换行字符
					sb.append("\\r");
					// 跳过实际回车换行
					i_start = i + 1;
				}
				else if (sqlcs[i] == '\\')
				{
					// 跳过字符串中转义符后面的字符
					i++;
					if (i + 1 < sqlcs.length && sqlcs[i + 1] == '\n')
					{
						i_line_no++;
					}
				}
				else if (("\'".equals(fInScriptPair) && sqlcs[i] == '\'')
					|| ("\"".equals(fInScriptPair) && sqlcs[i] == '\"'))
				{
					// 字符串结束
					fInScriptPair = "";
				}
				else
				{
					//skip
				}
			}
			else if ("//".equals(fInScriptPair))
			{
				if (sqlcs[i] == '\n')
				{
					// 行注释结束
					fInScriptPair = "";
				}
				else
				{
					//skip
				}
			}
			else if ("/*".equals(fInScriptPair))
			{
				if (sqlcs[i] == '*' && i + 1 < sqlcs.length && sqlcs[i + 1] == '/')
				{
					// 块注释结束
					i = i + 1;
					fInScriptPair = "";
				}
				else
				{
					//skip
				}
			}
			else
			{
				throw new ASSException("Impossible: 不认识的组对标识",
					st.sourceinfo.filename,
					st.sourceinfo.source.substring(start_char_index, sqlcs.length),
					start_line_no,
					i_line_no);
			}
		}
		throw new ASSException("Script Error: Script start with '{' and end with '}'." + "\r\nscript: {",
			st.sourceinfo.filename,
			st.sourceinfo.source.substring(start_char_index, sqlcs.length),
			start_line_no,
			i_line_no);
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
			else if ((match = SStrMatcher.match(sql, "(?is)((?:\\#\\#)|(?:\\@\\@)|(?:\\$\\$)|(?:--)|(?://))(.*)")).length > 0)
			{
				//skip a statement
			}
			else if ((match = SStrMatcher.match(sql, "(?is)((?:skip)|(?:ignore)|(?:script)|(?:not\\ssql))\\s+(.*)")).length > 0)
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
		Map[] md = new Map[]{bito.util.E.V().newMapSortedByAddTime()};
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
			// 参数传递到下一个存储过程
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
			return bito.util.E.V().readfile(new File(filename));
		}
		catch(FileNotFoundException fnfe)
		{
			return SQLScriptObject.not_found;
		}
	}

	public void saveFile(String filename, String content) throws IOException
	{
		bito.util.E.V().writefile(new File(filename), content);
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

	private void setCurFile(String dirname, String filename)
	{
		curdir = new File(dirname);
		curfile = new File(curdir, filename);
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
				curfiles[i] = new File(getCurdir(), fn);
			}
			curdirs[i] = curfiles[i].getParentFile();
			sqltexts[i] = bito.util.E.V().readfile(curfiles[i]);
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
			curdirs[i] = curdir;
			curfiles[i] = new File(curdir, "");
		}
		return execSQLText(curfiles, curdirs, sqltexts);
	}

	public void cancel()
	{
		if (scriptRunner != null)
		{
			scriptRunner.cancel();
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
		Entire entire = new Entire(null, null, 0, 0);
		try
		{
			Entire[] ses = new Entire[sqltexts.length];
			for(int i = 0; i < sqltexts.length && !cancel; i++)
			{
				if (sqltexts[i] != null && sqltexts[i].trim().length() > 0)
				{
					ses[i] = append(entire, sqltexts[i], curdirs[i].getCanonicalPath(), curfiles[i].getName(), 1);
				}
			}
			for(int i = 0; i < sqltexts.length && !cancel; i++)
			{
				exec(ses[i]);
			}
		}
		finally
		{
			entire.remove();
		}
		return 0;
	}
}
