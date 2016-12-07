package d._;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public class ObjectInheritance
{
	public static Object override(Object superobject, Object overrideobject) throws Exception
	{
		return ProxyGenerator.newProxyInstance(superobject.getClass(), new ObjectProxy(superobject, overrideobject));
	}

	private static class ObjectProxy implements InvocationHandler
	{
		private Object superobject;
		private Object overrideobject;
		private HashMap overridemethodmap = new HashMap();

		public ObjectProxy(Object superobject, Object overrideobject)
		{
			this.superobject = superobject;
			this.overrideobject = overrideobject;
		}

		private Method getOverrideMethod(String name, Class[] params)
		{
			String key = name;
			for(int i = 0; i < params.length; i++)
			{
				key += "," + params[i].getName();
			}
			if (overridemethodmap.containsKey(key))
			{
				return (Method)overridemethodmap.get(key);
			}
			Method mtd;
			try
			{
				mtd = overrideobject.getClass().getMethod(name, params);
				if (!mtd.getDeclaringClass().equals(overrideobject.getClass()))
				{
					mtd = null;
				}
			}
			catch(NoSuchMethodException nsme)
			{
				mtd = null;
			}
			overridemethodmap.put(key, mtd);
			return mtd;
		}

		private Object invokeMethod(Object obj, Method method, Object[] args) throws Throwable
		{
			try
			{
				return method.invoke(obj, args);
			}
			catch(InvocationTargetException ite)
			{
				throw ite.getTargetException();
			}
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			Object ret;
			String methodName = method.getName();
			Class[] params = method.getParameterTypes();
			try
			{
				Method mtd = getOverrideMethod(methodName, params);
				if (mtd != null)
				{
					ret = invokeMethod(overrideobject, mtd, args);
				}
				else
				{
					ret = invokeMethod(superobject, method, args);
				}
			}
			catch(InvocationTargetException ite)
			{
				throw ite.getTargetException();
			}
			finally
			{
			}
			return ret;
		}
	}
}