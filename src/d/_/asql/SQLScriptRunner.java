package d._.asql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.UniqueTag;
import org.mozilla.javascript.WrappedException;

import bito.json.JSON;

public class SQLScriptRunner
{
	private ContextFactory contextFactory = new ContextFactory();
	private Context context = null;
	private ScriptableObject global = null;
	private HashMap newProperties = new HashMap();

	public SQLScriptRunner(String name)
	{
		super();
		init(name);
	}

	private void init(String name)
	{
		// Creates and enters a Context. The Context stores information
		// about the execution environment of a script.
		context = contextFactory.enterContext(context);
		//Context cx = Context.enter(context, contextFactory);
		try
		{
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			global = new SQLScriptScope(context);
			((SQLScriptScope)global).setKey(null, name);
		}
		finally
		{
			// Exit from the context.
			Context.exit();
		}
	}

	public void eval_begin()
	{
		context = contextFactory.enterContext(context);
	}

	public Script complie(String source, String sourceName, int lineno) throws SQLScriptException
	{
		try
		{
			return context.compileString(source, sourceName, lineno, null);
		}
		catch(EvaluatorException ee)
		{
			throw new SQLScriptException(ee.getMessage() + "\r\n" + addlineno(source, lineno), ee);
		}
		finally
		{
		}
	}

	public void cancel()
	{
	}

	private void putNewProperties()
	{
		// fill in the newly variables
		Iterator npesi = newProperties.entrySet().iterator();
		while(npesi.hasNext())
		{
			Entry e = (Entry)npesi.next();
			String key = (String)e.getKey();
			Object value = e.getValue();
			Object jsobj = Context.javaToJS(transJO2JSO(value), global);
			ScriptableObject.putProperty(global, key, jsobj);
		}
		newProperties.clear();
	}

	public void put(String key, Object value)
	{
		newProperties.put(key, value);
	}

	private Object pure_eval(Script script, String script_string, String scriptname, int script_start_line,
		boolean bWrapDataSetValues) throws SQLScriptException
	{
		try
		{
			// fill in the newly variables
			putNewProperties();
			// Now evaluate the script
			Object result;
			if (script != null)
			{
				result = script.exec(context, global);
			}
			else
			{
				result = context.evaluateString(global, script_string, scriptname, script_start_line, null);
			}
			// Convert the result to a string and print it.
			Object obj = Context.jsToJava(result, Object.class);
			if (bWrapDataSetValues)
			{
				obj = wrapDataSetValues(obj);
			}
			return obj;
		}
		catch(WrappedException e)
		{
			Throwable cause = e.getCause();
			if (cause == null)
			{
				throw e;
			}
			if (cause instanceof SQLScriptException)
			{
				throw (SQLScriptException)cause;
			}
			if (cause instanceof ASSException)
			{
				ASSException ae = (ASSException)cause;
				throw new SQLScriptException(ae.getMessage()
					+ "("
						+ ae.sourcename
						+ "#"
						+ ae.error_line_no
						+ ")"
						+ "\r\n"
						+ addlineno(ae.sourcecode, ae.start_line_no), ae);
			}
			throw new SQLScriptException(e.getMessage() + "\r\n" + addlineno(script_string, script_start_line), e);
		}
		catch(EvaluatorException ee)
		{
			throw new SQLScriptException(ee.getMessage() + "\r\n" + addlineno(script_string, script_start_line), ee);
		}
		catch(EcmaError ee)
		{
			throw new SQLScriptException(ee.getMessage() + "\r\n" + addlineno(script_string, script_start_line), ee);
		}
		finally
		{
		}
	}

	private DataSetValues wrapDataSetValues(Object obj)
	{
		if (obj != null)
		{
			if (obj instanceof DataSetValues)
			{
				return (DataSetValues)obj;
			}
			else if (obj instanceof SQLScriptUnkown)
			{
				return new DataSetValues("", new String[0]);
			}
			else if (obj instanceof SQLScriptUndefined)
			{
				String fullname = ((SQLScriptable)obj).getFullName();
				throw Context.reportRuntimeError("object '" + fullname + "' is undefined.");
			}
			else if (obj instanceof SQLScriptable)
			{
				String fullname = ((SQLScriptable)obj).getFullName();
				if (obj instanceof SQLScriptArray)
				{
					return new DataSetValues(fullname, null, null, ((SQLScriptArray)obj).toArray());
				}
				else
				{
					return new DataSetValues(fullname, null, null, new Object[]{obj});
				}
			}
			else if (obj instanceof Object[])
			{
				return new DataSetValues("", null, null, (Object[])obj);
			}
			else if (obj instanceof Collection)
			{
				Collection objs = (Collection)obj;
				return new DataSetValues("", null, null, objs.toArray());
			}
			else
			{
				return new DataSetValues("", null, null, new Object[]{obj});
			}
		}
		return new DataSetValues("", new String[0]);
	}

	public DataSetValues evalSQLScript(Script script, String source, String scriptname, int script_start_line)
		throws SQLScriptException
	{
		return (DataSetValues)pure_eval(script, source, scriptname, script_start_line, true);
	}

	public Object pure_eval(String script, int script_start_line, String scriptname) throws SQLScriptException
	{
		return pure_eval(null, script, scriptname, script_start_line, false);
	}

	String addlineno(String script, int script_start_line)
	{
		String[] ss = script.split("\\r?\\n");
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < ss.length; i++)
		{
			sb.append((i + script_start_line)).append(": ").append(ss[i]).append("\r\n");
		}
		return sb.toString();
	}

	public void eval_end()
	{
		// Exit from the context.
		Context.exit();
	}

	public Object quick_eval(String script) throws Exception
	{
		eval_begin();
		try
		{
			return transJSO2JO(pure_eval(script, 1, ""));
		}
		finally
		{
			eval_end();
		}
	}

	public Object[] vars()
	{
		// Creates and enters a Context. The Context stores information
		// about the execution environment of a script.
		eval_begin();
		try
		{
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Object[] ids = ScriptableObject.getPropertyIds(global);
			return ((List)transJSO2JO(Context.jsToJava(ids, Object.class))).toArray();
		}
		finally
		{
			// Exit from the context.
			eval_end();
		}
	}

	public Object get(String key)
	{
		// Creates and enters a Context. The Context stores information
		// about the execution environment of a script.
		eval_begin();
		try
		{
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Object result = ScriptableObject.getProperty(global, key);
			return transJSO2JO(Context.jsToJava(result, Object.class));
		}
		finally
		{
			// Exit from the context.
			eval_end();
		}
	}

	private Map transNO2Map(NativeObject no)
	{
		SortedMap m = d.E.V().newMapSortedByAddTime();
		Iterator noesi = no.entrySet().iterator();
		while(noesi.hasNext())
		{
			Map.Entry me = (Map.Entry)noesi.next();
			Object k = me.getKey();
			Object v = me.getValue();
			k = k == null?"":transJSO2JO(k).toString();
			m.put(k, transJSO2JO(v));
		}
		return m;
	}

	private Map transJOMap2JSOMap(Map m)
	{
		SortedMap ret = d.E.V().newMapSortedByAddTime();
		Iterator mesi = m.entrySet().iterator();
		while(mesi.hasNext())
		{
			Map.Entry me = (Map.Entry)mesi.next();
			Object k = me.getKey();
			Object v = me.getValue();
			ret.put(transJO2JSO(k), transJO2JSO(v));
		}
		return ret;
	}

	private Map transMap2NO(Map m)
	{
		NativeObject no = new NativeObject();
		Iterator mesi = m.entrySet().iterator();
		while(mesi.hasNext())
		{
			Map.Entry me = (Map.Entry)mesi.next();
			Object k = me.getKey();
			Object v = me.getValue();
			if (k instanceof String)
			{
				no.put((String)k, no, transJO2JSO(v));
			}
			else
			{
				return transJOMap2JSOMap(m);
			}
		}
		return no;
	}

	private List transNA2List(NativeArray na)
	{
		List l = new ArrayList();
		Iterator nai = na.iterator();
		while(nai.hasNext())
		{
			Object o = nai.next();
			l.add(transJSO2JO(o));
		}
		return l;
	}

	private List transCollection2NA(Collection coll)
	{
		NativeArray na = new NativeArray(coll.size());
		Iterator ci = coll.iterator();
		int i = 0;
		while(ci.hasNext())
		{
			Object o = ci.next();
			na.put(i, na, transJO2JSO(o));
			i++;
		}
		return na;
	}

	private Object transJSO2JO(Object jo)
	{
		if (jo == Scriptable.NOT_FOUND || jo instanceof SQLScriptUnkown || jo instanceof Undefined)
		{
			return null;
		}
		else if (jo instanceof NativeObject)
		{
			return transNO2Map((NativeObject)jo);
		}
		else if (jo instanceof NativeArray)
		{
			return transNA2List((NativeArray)jo);
		}
		else if (jo instanceof Object[])
		{
			List al = new ArrayList();
			for(Object o : ((Object[])jo))
			{
				al.add(transJSO2JO(o));
			}
			return al;
		}
		return jo;
	}

	private Object transJO2JSO(Object jo)
	{
		if (jo == null)
		{
			return null;
		}
		else if (jo instanceof Map)
		{
			return transMap2NO((Map)jo);
		}
		else if (jo instanceof Collection)
		{
			return transCollection2NA((Collection)jo);
		}
		else if (jo instanceof Object[])
		{
			return transCollection2NA(Arrays.asList((Object[])jo));
		}
		return jo;
	}
}
