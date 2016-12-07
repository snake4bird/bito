package bito.util.textparser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

import bito.util.dba.SQLRunner;
import bito.util.logger.Log;
import bito.util.script.ScriptRunner;

public class SegmentParserScript implements SegmentParser
{
	private Log log = new Log("");
	private SQLRunner sqlrunner = null;
	private String parseName = null;
	private SegmentParser parentSegment = null;
	private ScriptRunner sr = new ScriptRunner();

	public SegmentParserScript()
	{
	}

	public void setParserScript(String parseName) throws Exception
	{
		this.parseName = parseName;
		sr.put("currentSegment", this);
		sr.put("parentSegment", parentSegment);
		sr.put("log", log);
		sr.put("sqlrunner", sqlrunner);
		sr.put("me", "");
		sr.put("endOfSegment", new Boolean(false));
		sr.eval("result={};");
		String script = System.getProperty("parser.script." + parseName, parseName).trim();
		if (script.startsWith("{") && script.endsWith("}"))
		{
			sr.eval(script.substring(1, script.length() - 1));
		}
		else
		{
			FileReader fr = new FileReader(script);
			sr.eval(fr);
			fr.close();
		}
	}

	public String getParserName() throws Exception
	{
		return parseName;
	}

	public void setLogger(Log log)
	{
		this.log = log;
	}

	public void setSQLRunner(SQLRunner sqlrunner)
	{
		this.sqlrunner = sqlrunner;
	}

	public void setVariable(String key, Object value) throws Exception
	{
		sr.put(key, value);
	}

	public SegmentParser getParentSegment()
	{
		return this.parentSegment;
	}

	public void setParentSegment(SegmentParser parentseg)
	{
		this.parentSegment = parentseg;
		sr.put("parentSegment", parentseg);
	}

	public SegmentParser parseLine(String line, int lineno) throws Exception
	{
		sr.put("currentLine", line);
		sr.put("lineNumber", lineno);
		Object parseReturn = sr.eval("parseLine(currentLine, lineNumber);");
		if (parseReturn == null)
		{
			return null;
		}
		if (parseReturn instanceof String)
		{
			String scriptName = (String)parseReturn;
			if ("".equals(scriptName))
			{
				return this;
			}
			else
			{
				SegmentParser sp = new SegmentParserScript();
				sp.setLogger(log);
				sp.setSQLRunner(sqlrunner);
				sp.setParserScript(scriptName);
				return sp;
			}
		}
		else
		{
			throw new Exception("function parseLine() should return a string.");
		}
	}

	public boolean isEndOfSegment() throws Exception
	{
		Object parseReturn = sr.get("endOfSegment");
		if (parseReturn instanceof Boolean)
		{
			return ((Boolean)parseReturn).booleanValue();
		}
		else
		{
			throw new Exception("endOfSegment should be a boolean value.");
		}
	}

	public Map getResult() throws Exception
	{
		Object o = sr.get("result");
		if (o instanceof Map)
		{
			return (Map)o;
		}
		Map m = new HashMap();
		m.put(o, o);
		return m;
	}

	public void parseEnd() throws Exception
	{
		sr.eval("parseEnd();");
	}
}
