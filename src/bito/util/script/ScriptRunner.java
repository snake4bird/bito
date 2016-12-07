package bito.util.script;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaAdapter;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrappedException;

import d._.asql.SQLScriptUnkown;

/**
 *  脚本中调用java类.方法时，如果类名不是java，在类名前要加Packages，
 *  也可以用JavaImporter().importPackage(Packages.packagename)导入Package。
 */
public class ScriptRunner
{
	public static final ScriptRunner global = new ScriptRunner();
	private ContextFactory contextFactory = new ContextFactory();
	private Context context = null;
	private ScriptableObject globalso = null;
	private HashMap newProperties = new HashMap();

	public ScriptRunner()
	{
		init();
	}

	private void init()
	{
		// Creates and enters a Context. The Context stores information
		// about the execution environment of a script.
		context = contextFactory.enterContext();
		try
		{
			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			globalso = (ScriptableObject)context.initStandardObjects();
		}
		finally
		{
			// Exit from the context.
			Context.exit();
		}
	}

	public void put(String key, Object value)
	{
		newProperties.put(key, value);
	}

	public Script compile(String scriptsource)
	{
		// Creates and enters a Context. The Context stores information
		// about the execution environment of a script.
		Context cx = contextFactory.enterContext(context);
		try
		{
			putNewProperties();
			Script script = cx.compileString(scriptsource, "", 1, null);
			return script;
		}
		finally
		{
			// Exit from the context.
			Context.exit();
		}
	}

	public Script compile(Reader scriptreader) throws IOException
	{
		// Creates and enters a Context. The Context stores information
		// about the execution environment of a script.
		Context cx = contextFactory.enterContext(context);
		try
		{
			putNewProperties();
			Script script = cx.compileReader(scriptreader, "", 1, null);
			return script;
		}
		finally
		{
			// Exit from the context.
			Context.exit();
		}
	}

	protected void putNewProperties()
	{
		// fill in the newly variables
		Iterator npesi = newProperties.entrySet().iterator();
		while(npesi.hasNext())
		{
			Entry e = (Entry)npesi.next();
			String key = (String)e.getKey();
			Object value = e.getValue();
			Object jsobj = Context.javaToJS(transJO2JSO(value), globalso);
			ScriptableObject.putProperty(globalso, key, jsobj);
		}
		newProperties.clear();
	}

	public Object eval(Script script) throws Exception
	{
		// Creates and enters a Context. The Context stores information
		// about the execution environment of a script.
		Context cx = contextFactory.enterContext(context);
		try
		{
			// fill in the newly variables
			putNewProperties();
			// Now evaluate the script
			Object result = script.exec(cx, globalso);
			if (result instanceof Undefined)
			{
				result = null;
			}
			return transJSO2JO(Context.jsToJava(result, Object.class));
		}
		catch(WrappedException e)
		{
			Throwable cause = e.getCause();
			if (cause instanceof ScriptException)
			{
				throw (ScriptException)cause;
			}
			throw e;
		}
		catch(EvaluatorException ee)
		{
			throw new ScriptException(ee.getMessage());
		}
		catch(EcmaError ee)
		{
			throw new ScriptException(ee.getMessage());
		}
		finally
		{
			// Exit from the context.
			Context.exit();
		}
	}

	public Object eval(String script) throws Exception
	{
		// Creates and enters a Context. The Context stores information
		// about the execution environment of a script.
		Context cx = contextFactory.enterContext(context);
		try
		{
			// fill in the newly variables
			putNewProperties();
			// Now evaluate the script
			Object result = cx.evaluateString(globalso, script, "", 1, null);
			// Convert the result to a string and print it.
			if (result instanceof Undefined)
			{
				result = null;
			}
			return transJSO2JO(Context.jsToJava(result, Object.class));
		}
		catch(WrappedException e)
		{
			Throwable cause = e.getCause();
			if (cause instanceof ScriptException)
			{
				throw (ScriptException)cause;
			}
			throw e;
		}
		catch(EvaluatorException ee)
		{
			throw new ScriptException(ee.getMessage() + "\r\n" + addlineno(script));
		}
		catch(EcmaError ee)
		{
			throw new ScriptException(ee.getMessage() + "\r\n" + addlineno(script));
		}
		finally
		{
			// Exit from the context.
			Context.exit();
		}
	}

	String addlineno(String script)
	{
		String[] ss = script.split("\\r?\\n");
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < ss.length; i++)
		{
			sb.append((i + 1)).append(": ").append(ss[i]).append("\r\n");
		}
		return sb.toString();
	}

	public Object eval(Reader fr) throws Exception
	{
		// Creates and enters a Context. The Context stores information
		// about the execution environment of a script.
		Context cx = contextFactory.enterContext(context);
		try
		{
			// fill in the newly variables
			putNewProperties();
			// Now evaluate the script
			Object result = cx.evaluateReader(globalso, fr, "", 1, null);
			// Convert the result to a string and print it.
			if (result instanceof Undefined)
			{
				result = null;
			}
			return transJSO2JO(Context.jsToJava(result, Object.class));
		}
		catch(WrappedException e)
		{
			Throwable cause = e.getCause();
			if (cause instanceof ScriptException)
			{
				throw (ScriptException)cause;
			}
			throw e;
		}
		catch(EvaluatorException ee)
		{
			throw new ScriptException(ee.getMessage());
		}
		catch(EcmaError ee)
		{
			throw new ScriptException(ee.getMessage());
		}
		finally
		{
			// Exit from the context.
			Context.exit();
		}
	}

	public Object[] vars()
	{
		// Creates and enters a Context. The Context stores information
		// about the execution environment of a script.
		Context cx = contextFactory.enterContext(context);
		try
		{
			Object[] ids = ScriptableObject.getPropertyIds(globalso);
			return ((List)transJSO2JO(Context.jsToJava(ids, Object.class))).toArray();
		}
		finally
		{
			// Exit from the context.
			Context.exit();
		}
	}

	public Object get(String key)
	{
		// Creates and enters a Context. The Context stores information
		// about the execution environment of a script.
		Context cx = contextFactory.enterContext(context);
		try
		{
			Object result = ScriptableObject.getProperty(globalso, key);
			return transJSO2JO(Context.jsToJava(result, Object.class));
		}
		finally
		{
			// Exit from the context.
			Context.exit();
		}
	}

	public Object S2J(Object o)
	{
		return transJSO2JO(o);
	}

	public Object J2S(Object o)
	{
		return transJO2JSO(o);
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
		ScriptRuntime.setBuiltinProtoAndParent(no, globalso, TopLevel.Builtins.Object);
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
		ScriptRuntime.setBuiltinProtoAndParent(na, globalso, TopLevel.Builtins.Array);
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
