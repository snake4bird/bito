package assassin.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

public class ASScriptEngine
{
	private static class ASSBinding extends LinkedHashMap<String, Object> implements Bindings
	{
		private String scope;

		/**
		 * The <code>Map</code> field stores the attributes.
		 */
		public ASSBinding(String scope)
		{
			this.scope = scope;
		}
		// for debug only
		//		public Object put(String key, Object value)
		//		{
		//			System.out.println("set " + key + " = " + value);
		//			return super.put(key, value);
		//		}
		//
		//		public void putAll(Map<? extends String, ? extends Object> toMerge)
		//		{
		//			for(Map.Entry<? extends String, ? extends Object> me : toMerge.entrySet())
		//			{
		//				put(me.getKey(), me.getValue());
		//			}
		//		}
		//
		//		public boolean containsKey(Object key)
		//		{
		//			return super.containsKey(key);
		//		}
		//
		//		public Object get(Object key)
		//		{
		//			Object value = super.get(key);
		//			System.out.println("get " + key + " = " + value);
		//			return value;
		//		}
		//
		//		public Object remove(Object key)
		//		{
		//			Object value = super.remove(key);
		//			System.out.println("remove " + key + " = " + value);
		//			return value;
		//		}
	}

	private static final File system_current_path = getSystemCurrentPath();

	private static File getSystemCurrentPath()
	{
		try
		{
			return new File("").getCanonicalFile();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static final long engine_start_time = System.currentTimeMillis();
	private static final String ass_init_filename = ":assassin/script/assinit";
	private static final ScriptEngineManager sem = new ScriptEngineManager();
	private static final ASSBinding global = new ASSBinding("global");
	private ASScriptPreprocess asp = new ASScriptPreprocess();
	private SQLRunner sr = new SQLRunner(this);
	private ScriptEngine se = sem.getEngineByName("js");
	private Compilable sec = (javax.script.Compilable)se;
	private Invocable sei = (javax.script.Invocable)se;
	private ASSBinding engine = new ASSBinding("engine");
	private ScriptContext sc = new SimpleScriptContext();

	public ASScriptEngine()
	{
		sc.setBindings(engine, ScriptContext.ENGINE_SCOPE);
		sc.setBindings(global, ScriptContext.GLOBAL_SCOPE);
		se.setContext(sc);
		init();
	}

	private Object invokeFunction(String name, Object... args)
	{
		try
		{
			return sei.invokeFunction(name, args);
		}
		catch(ScriptException se)
		{
			String s = se.getCause().getMessage();
			throw new RuntimeException(s);
		}
		catch(NoSuchMethodException nsme)
		{
			String s = nsme.getCause().getMessage();
			throw new RuntimeException(s);
		}
	}

	private Object init()
	{
		set("ScriptEngine", this);
		set("SQLRunner", sr);
		set("ev", bito.util.E.V());
		return exec(ass_init_filename);
	}

	public void set(String key, Object value)
	{
		se.put(key, value);
	}

	public void global(String key, Object value)
	{
		global.put(key, value);
	}

	public Object get(String key)
	{
		return se.get(key);
	}

	public void setCurrentFilename(String filename)
	{
		set(ScriptEngine.FILENAME, filename);
	}

	public String getCurrentFilename()
	{
		return (String)get(ScriptEngine.FILENAME);
	}

	public Object eval(String code)
	{
		try
		{
			String fn;
			if (code.length() < 80 && code.indexOf("\n") < 0)
			{
				fn = ":" + code;
			}
			else
			{
				fn = ":inline_" + bito.util.E.V().MD5(code);
			}
			setCurrentFilename(fn);
			Long lft = fstamp.get(fn);
			CompiledScript script = fscript.get(fn);
			List<String> fascode = ascode.get(fn);
			char[] cs = code.toCharArray();
			List<String> nascode = fascode(cs);
			if (script == null || lft == null || !nascode.equals(fascode))
			{
				script = preprocess(fn, cs);
				ascode.put(fn, nascode);
				fscript.put(fn, script);
				fstamp.put(fn, System.currentTimeMillis());
			}
			return script.eval(sc);
		}
		catch(ScriptException se)
		{
			String s = se.getCause().getMessage();
			throw new RuntimeException(s, se.getCause());
		}
	}

	private CompiledScript readscript(String sfn, long ft, InputStream is) throws IOException, ScriptException
	{
		try
		{
			// System.out.println("read script: " + sfn);
			String code = new String(bito.util.E.V().readBytes(is));
			char[] cs = code.toCharArray();
			List<String> nascode = fascode(cs);
			CompiledScript script = preprocess(sfn, cs);
			ascode.put(sfn, nascode);
			fscript.put(sfn, script);
			fstamp.put(sfn, ft);
			return script;
		}
		finally
		{
			is.close();
		}
	}

	public Object exec(String sfn)
	{
		boolean logset_pushed = false;
		String cfn = getCurrentFilename();
		File cp = fcpath.get(cfn);
		if (cp == null)
		{
			cp = system_current_path;
		}
		try
		{
			long ft;
			setCurrentFilename(sfn);
			Long lft = fstamp.get(sfn);
			CompiledScript script = fscript.get(sfn);
			File f = new File(cp, sfn);
			if (f.exists())
			{
				ft = f.lastModified();
				cp = f.getCanonicalFile().getParentFile();
				fcpath.put(sfn, cp);
				run("log_set_stack.push(log.set); log.set = clone(default_log_set);");
				logset_pushed = true;
				setCurrentFilename(sfn);
				if (script == null || lft == null || lft.longValue() != ft)
				{
					script = readscript(sfn, ft, new FileInputStream(f));
				}
			}
			else
			{
				String xfn = sfn;
				if (sfn.startsWith(":"))
				{
					xfn = sfn.substring(1);
				}
				InputStream is = ASScriptEngine.class.getClassLoader().getResourceAsStream(xfn);
				if (is != null)
				{
					ft = engine_start_time;
					if (script == null || lft == null || lft.longValue() != ft)
					{
						script = readscript(sfn, ft, is);
					}
				}
				else
				{
					if (sfn.indexOf(":") > 0)
					{
						URL url = new URL(sfn);
						URLConnection uc = url.openConnection();
						ft = uc.getLastModified();
						if (script == null || lft == null || lft.longValue() != ft)
						{
							is = uc.getInputStream();
							script = readscript(sfn, ft, is);
						}
					}
					else
					{
						throw new RuntimeException("Not found file '" + f.getCanonicalPath() + "'");
					}
				}
			}
			return script.eval(sc);
		}
		catch(ScriptException se)
		{
			String s = se.getCause().getMessage();
			throw new RuntimeException(s, se.getCause());
		}
		catch(IOException e)
		{
			String s = e.getMessage();
			if (s.indexOf(sfn) < 0)
			{
				s += " " + sfn;
			}
			throw new RuntimeException(s, e);
		}
		finally
		{
			if (logset_pushed)
			{
				run("log.set = log_set_stack.pop();");
			}
		}
	}

	/**
	 * 运行指定code, 在 code 中可以引用参数数组 args 
	 * @param code
	 * @param args
	 * @return
	 */
	public Object run(String code, Object... args)
	{
		se.put("args", args);
		return eval(code);
	}

	public void cancel()
	{
		// force stop running code, but don't clear variables for other thread used.
	}

	private Map<String, Long> fstamp = new HashMap();
	private Map<String, CompiledScript> fscript = new HashMap();
	private Map<String, List<String>> ascode = new HashMap();
	private Map<String, List<String>> jscode = new HashMap();
	private Map<String, File> fcpath = new HashMap();

	private List<String> fascode(char[] cs)
	{
		List<String> fascode = new ArrayList();
		if (fascode != null)
		{
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < cs.length; i++)
			{
				char c = cs[i];
				sb.append(c);
				if (c == '\n')
				{
					fascode.add(sb.toString());
					sb = new StringBuffer();
				}
			}
			fascode.add(sb.toString());
		}
		return fascode;
	}

	private static final boolean debug_preprocess = "true".equals(System.getProperty("debug.ass.preprocess"));

	private CompiledScript preprocess(String fn, char[] code_chars) throws ScriptException
	{
		List<String> fjscode = new ArrayList();
		String code = asp.proc(fn, code_chars, fjscode);
		jscode.put(fn, fjscode);
		if (debug_preprocess)
		{
			System.out.println(js_source(0, 0));
		}
		return sec.compile(code);
	}

	public String source(int sline, int eline)
	{
		String fn = (String)getCurrentFilename();
		if (fn != null)
		{
			StringBuffer sb = new StringBuffer();
			List<String> fvlcode = ascode.get(fn);
			for(int i = sline > 0?(sline - 1):0; i < fvlcode.size() && (eline <= 0 || i < eline); i++)
			{
				sb.append((i + 1)).append(": ").append(fvlcode.get(i));
			}
			return sb.toString();
		}
		return "";
	}

	public String js_source(int sline, int eline)
	{
		String fn = getCurrentFilename();
		if (fn != null)
		{
			StringBuffer sb = new StringBuffer();
			List<String> fvlcode = jscode.get(fn);
			for(int i = sline > 0?(sline - 1):0; i < fvlcode.size() && (eline <= 0 || i < eline); i++)
			{
				sb.append((i + 1)).append(": ").append(fvlcode.get(i));
			}
			return sb.toString();
		}
		return "";
	}

	public void error(String s)
	{
		synchronized(this)
		{
			run("log.error(args[0])", s);
		}
	}

	public void debug(String s)
	{
		synchronized(this)
		{
			run("log.debug(args[0])", s);
		}
	}

	public void detail(String s)
	{
		synchronized(this)
		{
			run("log.detail(args[0])", s);
		}
	}
}
