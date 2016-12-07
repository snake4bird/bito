package d;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.Inflater;

import d.g.gd;
import d.g.gf;
import d.g.gi;

public abstract class $
{
	private static final $ V;
	private static final ClassLoader evcl;
	private static final Method evcla;
	private static final Method evclc;
	private static Throwable evcle = null;
	private static boolean gettingTHIS = false;
	private static $ THIS = null;
	static
	{
		evcl = c();
		evcla = a(evcl);
		evclc = c(evcl);
		V = V();
	}

	protected static synchronized $ V()
	{
		if (THIS == null)
		{
			if (gettingTHIS)
			{
				//System.out.println("getting " + THIS);
				return THIS; // occurs when tomcat reload
			}
			gettingTHIS = true;
			String EVClassName = d.$.class.getName().replace(".$", "._.EV");
			//System.out.println("new " + EVClassName);
			try
			{
				THIS = ($)_newDynamicObject(EVClassName, d.$.class);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			//System.out.println("got " + THIS);
		}
		else
		{
			(($)THIS).checksum(new Throwable().getStackTrace()[1].getClass());
		}
		return THIS;
	}

	private void checksum(Class cls)
	{
		try
		{
			if (evcle != null)
				throw evcle;
			try
			{
				evclc.invoke(evcl, new Object[]{cls});
			}
			catch(InvocationTargetException ite)
			{
				throw ite.getCause();
			}
		}
		catch(RuntimeException runtimeexception)
		{
			throw runtimeexception;
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
		catch(Throwable throwable)
		{
			throw new RuntimeException(throwable);
		}
	}

	private static final Object _newDynamicObject(String clsname, Class super_class)
	{
		if (clsname != null && clsname.length() > 0)
		{
			Class cls;
			try
			{
				if (evcle != null)
					throw evcle;
				evcla.invoke(evcl, new Object[]{super_class});
				cls = Class.forName(clsname, true, evcl);
			}
			catch(RuntimeException runtimeexception)
			{
				throw runtimeexception;
			}
			catch(Exception exception)
			{
				throw new RuntimeException("find class exception:" + clsname, exception);
			}
			catch(Throwable throwable)
			{
				throw new RuntimeException("find class error:" + clsname, throwable);
			}
			try
			{
				Object obj = null;
				obj = cls.newInstance();
				if (!super_class.isAssignableFrom(cls))
				{
					throw new RuntimeException(clsname
						+ " should "
							+ (super_class.isInterface()?"implements interface":"extends class")
							+ " "
							+ super_class.getName());
				}
				else
				{
					return obj;
				}
			}
			catch(RuntimeException re)
			{
				throw re;
			}
			catch(Throwable t)
			{
				throw new RuntimeException("create object error:" + clsname, t);
			}
		}
		else
		{
			return null;
		}
	}

	private static Method a(ClassLoader classloader)
	{
		try
		{
			if (evcle == null)
			{
				Method method = classloader.getClass().getMethod("a", new Class[]{Class.class});
				return method;
			}
		}
		catch(Exception exception)
		{
			evcle = exception;
		}
		return null;
	}

	private static Method c(ClassLoader classloader)
	{
		try
		{
			if (evcle == null)
			{
				Method method = classloader.getClass().getMethod("c", new Class[]{Class.class});
				return method;
			}
		}
		catch(Exception exception)
		{
			evcle = exception;
		}
		return null;
	}

	private static ClassLoader c()
	{
		try
		{
			try
			{
				return (ClassLoader)Class.forName("$.$").newInstance();
			}
			catch(Throwable throwable)
			{
			}
			ClassLoader cl = d.$.class.getClassLoader();
			if (cl == null)
			{
				cl = ClassLoader.getSystemClassLoader();
			}
			return (ClassLoader)Class.forName("$", true, new ClassLoader(cl)
			{
				protected synchronized Class loadClass(String s, boolean flag) throws ClassNotFoundException
				{
					//System.out.println("Class loading: " + s);
					Class class1 = ("$".equals(s))?getEVCL():super.loadClass(s, flag);
					if (flag)
						resolveClass(class1);
					//System.out.println("Class loaded: " + class1.getCanonicalName());
					return class1;
				}

				private Class getEVCL() throws ClassNotFoundException
				{
					try
					{
						String sdollar = "$$$$";
						byte[] orgclassbytes = new byte[4];
						byte[] sdbinary = new byte[4];
						sdollar = sdollar.substring(0);
						System.arraycopy(d.g.b.decode(sdollar), 0, sdbinary, 0, sdbinary.length);
						orgclassbytes = gd.bs(sdbinary);
						{
							//java.io.FileOutputStream fos = new java.io.FileOutputStream("evcl.class");
							//fos.write(orgclassbytes);
							//fos.close();
						}
						return defineClass(null, orgclassbytes, 0, orgclassbytes.length);
					}
					catch(Throwable throwable1)
					{
						//hidden detail error message.
						//throwable1.printStackTrace();
						throw new ClassNotFoundException(throwable1.getMessage());
					}
				}
			}).newInstance();
		}
		catch(Throwable throwable)
		{
			evcle = throwable;
		}
		return null;
	}

	protected final Object _(String clsname)
	{
		return _newDynamicObject(clsname, Object.class);
	}
}
