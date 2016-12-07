package bito.util.pool;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.SortedMap;

/**
 * 限制：
 * 		Poolable 必须有无参数构造方法
 * 
 * @author LIBOFENG
 *
 */
public class Pool
{
	private static File debugflag = new File("ev.debug");
	//
	private SortedMap freeObjects = bito.util.E.V().newMapSortedByAddTime();
	// configuration
	private Class poolableClass;
	private Object[] args;
	private int maxObjectsCount = 0;
	private int maxHoldCount = 0;
	private long keepIdleTime = 0;
	//
	private int objectsCount = 0;
	private int failedcount = 0;
	private long allusedtime = 0;

	public Pool(Class poolableClass, Object[] args, int maxObjectsCount, int maxHoldCount, long keepIdleTime)
	{
		this.poolableClass = poolableClass;
		this.args = args;
		this.maxObjectsCount = maxObjectsCount;
		this.maxHoldCount = maxHoldCount;
		this.keepIdleTime = keepIdleTime;
	}

	private void log(String s)
	{
		if (debugflag.exists())
		{
			System.out.println(s);
		}
	}

	public String poolableObject()
	{
		String s = poolableClass.getName();
		if (args != null && args.length > 0)
		{
			s += "(";
			for(int i = 0; i < args.length; i++)
			{
				if (i > 0)
				{
					s += ", ";
				}
				s += args[i];
			}
			s += ")";
		}
		return s;
	}

	public class PooledObjectSpy implements Poolable
	{
		private final Pool pool;
		private final Poolable purepo;
		private Poolable poolpo;
		private boolean useable = true;

		public PooledObjectSpy(Pool pool, Poolable purepo)
		{
			this.pool = pool;
			this.purepo = purepo;
		}

		public void setPooledObject(Poolable poolpo)
		{
			this.poolpo = poolpo;
		}

		public void hold()
		{
			purepo.hold();
		}

		public void free()
		{
			purepo.free();
			pool.freeObject(poolpo, purepo);
		}

		public boolean reusable()
		{
			boolean b = purepo.reusable();
			return b && useable;
		}

		public void destroy()
		{
			purepo.destroy();
			pool.destroyObject(this);
		}

		boolean useable()
		{
			return useable;
		}

		void disable()
		{
			useable = false;
		}
	}

	private Poolable newObject() throws Exception
	{
		log("new " + poolableObject() + " instance, poolled " + (objectsCount) + " objects.");
		Poolable pure_obj = null;
		try
		{
			if (args != null && args.length > 0)
			{
				Class[] parameterTypes = new Class[args.length];
				for(int i = 0; i < args.length; i++)
				{
					parameterTypes[i] = args[i].getClass();
				}
				pure_obj = (Poolable)poolableClass.getConstructor(parameterTypes).newInstance(args);
			}
			else
			{
				pure_obj = (Poolable)poolableClass.getConstructor().newInstance();
			}
			ArrayList<Method> al = new ArrayList();
			al.addAll(Arrays.asList(PooledObjectSpy.class.getDeclaredMethods()));
			Class cls = poolableClass;
			while(!cls.equals(Object.class))
			{
				al.addAll(Arrays.asList(cls.getDeclaredMethods()));
				cls = cls.getSuperclass();
			}
			Class proxyClass = bito.util.E.V().getProxyClass(poolableClass.getName() + "$" + bito.util.E.V().getStamp(),
				poolableClass,
				new Class[]{Poolable.class},
				new Class[]{PooledObjectSpy.class, poolableClass},
				al.toArray(new Method[0]));
			PooledObjectSpy pooled_obj_spy = new PooledObjectSpy(this, pure_obj);
			Poolable pooled_obj = (Poolable)proxyClass
				.getConstructor(new Class[]{PooledObjectSpy.class, poolableClass})
				.newInstance(pooled_obj_spy, pure_obj);
			pooled_obj_spy.setPooledObject(pooled_obj);
			objectsCount++;
			return pooled_obj;
		}
		catch(Exception e)
		{
			if (pure_obj != null)
			{
				pure_obj.destroy();
			}
			if (objectsCount == 0)
			{
				throw e;
			}
			log("new " + poolableObject() + " instance failed : " + e.getMessage());
			failedcount++;
			Poolable pooled_obj = holdObject();
			failedcount--;
			return pooled_obj;
		}
	}

	public synchronized Poolable holdObject() throws Exception
	{
		long t = 0;
		int n = 0;
		while((failedcount > 0 || (maxObjectsCount > 0 && objectsCount >= maxObjectsCount)) && freeObjects.size() == 0)
		{
			if (t == 0 || System.currentTimeMillis() - t > 1000)
			{
				log("wait for "
					+ poolableObject()
						+ " object free"
						+ (n > 0?(" " + n + "s"):"")
						+ ", objects count="
						+ objectsCount);
				t = System.currentTimeMillis();
				n++;
			}
			wait();
		}
		if (freeObjects.size() <= 1)
		{
			allusedtime = System.currentTimeMillis();
		}
		Poolable po;
		if (freeObjects.size() > 0)
		{
			po = (Poolable)freeObjects.firstKey();
			freeObjects.remove(po);
		}
		else
		{
			po = newObject();
		}
		po.hold();
		return po;
	}

	public synchronized void freeObject(Poolable poolpo, Poolable purepo)
	{
		if (poolpo.reusable() && (maxHoldCount <= 0 || freeObjects.size() < maxHoldCount))
		{
			purepo.free();
			freeObjects.put(poolpo, purepo);
			notifyAll();
		}
		else
		{
			poolpo.destroy();
		}
	}

	public synchronized void cleanTimeoutFreeObjects()
	{
		if (System.currentTimeMillis() > allusedtime + keepIdleTime)
		{
			if (freeObjects.size() > 0)
			{
				Poolable po = (Poolable)freeObjects.firstKey();
				freeObjects.remove(po);
				po.destroy();
				allusedtime = System.currentTimeMillis();
			}
		}
	}

	public synchronized void cleanAllFreeObjects()
	{
		for(int i = freeObjects.size() - 1; i >= 0; i--)
		{
			Poolable po = (Poolable)freeObjects.firstKey();
			freeObjects.remove(po);
			po.destroy();
		}
	}

	synchronized void destroyObject(PooledObjectSpy spy)
	{
		if (spy.useable())
		{
			freeObjects.remove(spy.poolpo);
			objectsCount--;
			spy.disable();
			log("destroy " + poolableObject() + " instance, poolled " + (objectsCount) + " objects.");
			notifyAll();
		}
	}
};
