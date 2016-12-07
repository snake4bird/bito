package bito.util.pool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import bito.util.cfg.SystemConfig;
import bito.util.logger.Log;

public class PoolManager
{
	private static PoolManager pm = new PoolManager();

	public static Pool getPool(Class poolableClass, Object[] args)
	{
		return pm.getClassPool(poolableClass, args);
	}

	private HashMap pools = new HashMap();

	//
	private PoolManager()
	{
	}

	private Pool getClassPool(Class poolableClass, Object[] args)
	{
		synchronized(pools)
		{
			ArrayList key = new ArrayList();
			key.add(poolableClass);
			if (args != null && args.length > 0)
			{
				key.addAll(Arrays.asList(args));
			}
			Pool pool = (Pool)pools.get(key);
			if (pool == null)
			{
				String clsname = poolableClass.getName().replaceAll("[^\\.]+\\.", "");
				int maxobjectscount = SystemConfig.getInt("pool." + clsname + ".max.objects.count", 10);
				int maxholdcount = SystemConfig.getInt("pool." + clsname + ".max.hold.count", 10);
				long keepidletime = 1000L * SystemConfig.getLong("pool." + clsname + ".keep.idle.seconds", 900);
				pool = new Pool(poolableClass, args, maxobjectscount, maxholdcount, keepidletime);
				pools.put(key, pool);
			}
			return pool;
		}
	}

	private CleanThread cleanThread = new CleanThread();

	private class CleanThread extends Thread
	{
		public Log log = null;

		public CleanThread()
		{
			super("object pool cleaner");
			Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook Object Pool Cleaner")
			{
				public void run()
				{
					endloop();
				}
			});
			this.setDaemon(true);
			this.start();
		}

		public void run()
		{
			try
			{
				while(cleanThread == this)
				{
					loop();
				}
				endloop();
			}
			catch(Exception e)
			{
				if (log != null)
				{
					log.error(e.getMessage());
				}
				else
				{
					e.printStackTrace();
				}
			}
			catch(Error e)
			{
				if (log != null)
				{
					log.error(e.getMessage());
				}
				else
				{
					e.printStackTrace();
				}
			}
		}

		public void loop()
		{
			try
			{
				synchronized(pools)
				{
					Iterator i = pools.values().iterator();
					while(i.hasNext())
					{
						Pool pi = (Pool)i.next();
						pi.cleanTimeoutFreeObjects();
					}
				}
				sleep(1000);
			}
			catch(InterruptedException e)
			{
			}
			catch(Exception e)
			{
				if (log != null)
				{
					log.error(e.getMessage());
				}
				else
				{
					e.printStackTrace();
				}
			}
			catch(Error e)
			{
				if (log != null)
				{
					log.error(e.getMessage());
				}
				else
				{
					e.printStackTrace();
				}
			}
		}

		public void endloop()
		{
			synchronized(pools)
			{
				Iterator i = pools.values().iterator();
				while(i.hasNext())
				{
					Pool pi = (Pool)i.next();
					pi.cleanAllFreeObjects();
				}
				pools.clear();
			}
		}
	}
}
