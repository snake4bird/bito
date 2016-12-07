package d;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.zip.Inflater;

import bito.evframe.EVF;
import bito.util.cfg.SystemConfig;

import d._.json.JSON;
import d.g.gd;
import d.g.gf;
import d.g.gi;

public abstract class E extends $ implements EVI
{
	public final String evdir = getClassLocation(d.E.class);

	protected E()
	{
		System.setProperty("ev.dir", evdir);
	}

	public static void main(String[] args)
	{
		V().evMain(args);
	}

	public static synchronized E V()
	{
		return (E)$.V();
	}

	private HashSet oset = new HashSet();
	private int ocaller = 0;

	public final Object o()
	{
		String clsname = null;
		StackTraceElement[] est = Thread.currentThread().getStackTrace();
		if (ocaller == 0)
		{
			for(int i = 0; i < est.length; i++)
			{
				if (est[i].getClassName().equals(d.E.class.getName()) && est[i].getMethodName().equals("o"))
				{
					ocaller = i + 1;
				}
			}
		}
		clsname = est[ocaller].getClassName();
		synchronized(oset)
		{
			if (oset.contains(clsname))
			{
				return null;
			}
			oset.add(clsname);
			try
			{
				RuntimeException te = null;
				String OrgClassName = clsname;
				String pkgname = OrgClassName;
				int n = pkgname.lastIndexOf('.');
				while(n > 0)
				{
					pkgname = OrgClassName.substring(0, n);
					try
					{
						String EVClassName = pkgname + "._" + OrgClassName.substring(n);
						return _(EVClassName);
					}
					catch(RuntimeException e)
					{
						te = new RuntimeException(e.getMessage() + " " + ocaller, e.getCause());
						n = pkgname.lastIndexOf('.');
					}
				}
				throw te;
			}
			finally
			{
				synchronized(oset)
				{
					oset.remove(clsname);
				}
			}
		}
	}

	public void evMain(String[] args)
	{
		try
		{
			if (args.length >= 1 && args[0].startsWith("!"))
			{
				new EVF(args).run();
			}
			else if (args.length == 0 && SystemConfig.get("run.class") != null)
			{
				Runnable r = (Runnable)Class.forName(SystemConfig.get("run.class")).newInstance();
				r.run();
			}
			else if (args.length > 1 && args[0].equals("wget"))
			{
				URLReader r = newURLReader();
				if ("debug".equals(System.getProperty("mode")))
				{
					r.setDebugOutput(System.out, System.getProperty("debug.setting"));
				}
				byte[] respbs = r.ReadURL(args[1],
					System.getProperty("method"),
					System.getProperty("username"),
					System.getProperty("password"),
					System.getProperty("proxyhost"),
					System.getProperty("proxyport"),
					System.getProperty("proxyuser"),
					System.getProperty("proxypswd"),
					null,
					args.length > 2?args[2].getBytes():null,
					null,
					-1,
					-1,
					Integer.parseInt(System.getProperty("timeout", "-1")));
				System.out.println(new String(respbs, System.getProperty("charset", Charset.defaultCharset().name())));
			}
			else if (args.length == 0)
			{
				new EVF(new String[]{"!d._.RegxMatch"}).run();
			}
			else
			{
				System.out.println("d.E ?");
				System.out.println("d.E !<JPanel Class Name>");
				System.out.println("d.E encrypt <srcfile> <desfile> <key>");
				System.out.println("d.E decrypt <srcfile> <desfile> <key>");
				System.out.println("d.E tail <tailfile>");
				System.out.println("[-Dusername=...] [-Dpassword=...] [-Dmode=debug] d.E wget <http[s]url> [postdata]");
				System.out.println("d.E publish <v> !");
				System.out.println("-Dmaxfilesize=2048000 -Dmaxbackindex=-1 d.E output <redirect stdout to filename>");
				System.out.println();
				System.out.println(Arrays.toString(args));
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}
