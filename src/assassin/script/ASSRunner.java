package assassin.script;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import bito.util.cfg.SystemConfig;
import bito.util.logger.Log;

public class ASSRunner implements ProcThreadManager, ProcThreadMonitor
{
	private Log log = new Log("");
	//系统初始化
	private long treconfig = 0;
	private long treorganize = 0;
	private int runningcount = 0;
	//
	private String common_script_filename = null;
	private String common_script = null;
	private HashMap<String, Object> parameters = new HashMap();
	private Object lastResult = null;
	private Throwable lastError = null;
	//
	private boolean stopped = false;

	public ASSRunner()
	{
	}

	public void setCommonIncludeScriptFilename(String common_script_filename)
	{
		this.common_script_filename = common_script_filename;
	}

	public void setCommonIncludeScript(String common_script)
	{
		this.common_script = common_script;
	}

	public void setParameter(String key, Object value)
	{
		this.parameters.put(key, value);
	}

	public Object getResult()
	{
		return lastResult;
	}

	public Throwable getLastError()
	{
		return lastError;
	}

	public boolean isStopped()
	{
		return stopped;
	}

	public int run()
	{
		stopped = false;
		//根据配置信息，定时调整系统运行状态
		if (System.currentTimeMillis() > treconfig
			+ (1000L * SystemConfig.getLong("config.refresh.interval.seconds", 1)))
		{
			configDataProcs();
			treconfig = System.currentTimeMillis();
		}
		if (System.currentTimeMillis() > treorganize + 1000)
		{
			runningcount = reorganize();
			treorganize = System.currentTimeMillis();
		}
		return runningcount;
	}

	private void configDataProcs()
	{
		SortedMap<String, ProcThread.Args> dataprocs = new TreeMap();
		Properties ps = SystemConfig.getProperties();
		Enumeration pknames = ps.propertyNames();
		while(pknames.hasMoreElements())
		{
			Object pkn = pknames.nextElement();
			if (pkn instanceof String)
			{
				String pkname = (String)pkn;
				if (pkname.startsWith("data.proc.")
					&& pkname.endsWith(".script.files")
						&& ps.getProperty(pkname, "").length() > 0)
				{
					String procname = pkname.substring(0, pkname.length() - ".script.files".length());
					ProcThread.Args args = new ProcThread.Args(SystemConfig.get(procname + ".script.files"),
						SystemConfig.get(procname + ".schedule.cron", ""),
						SystemConfig.getInt(procname + ".start.delay.seconds", 0),
						SystemConfig.getInt(procname + ".loop.interval.min.seconds", 5),
						parameters,
						common_script,
						common_script_filename);
					dataprocs.put(procname, args);
				}
			}
		}
		config_dataprocs = dataprocs;
	}

	private Map<String, ProcThread> procthreads_running = new HashMap();
	private Map<String, Long> procthreads_errortime = new HashMap();
	private Map<String, Long> procthreads_lastendtime = new HashMap();
	//
	private Map<ProcThread, Long> procthreads_starttime = new HashMap();
	private HashMap<ProcThread, Long> toCleanupThreads = new HashMap();
	private HashMap<ProcThread, Long> deadLockThreads = new HashMap();
	private HashMap<ProcThread, Long> timeoutThreads = new HashMap();

	public void procThreadProcBegin(ProcThread pt)
	{
		synchronized(procthreads_running)
		{
			procthreads_starttime.put(pt, System.currentTimeMillis());
		}
	}

	public void procThreadProcEnd(ProcThread pt)
	{
		synchronized(procthreads_running)
		{
			procthreads_starttime.remove(pt);
		}
	}

	public void procThreadEnd(ProcThread pt, Object result)
	{
		synchronized(procthreads_running)
		{
			ProcThread.Args args = dynamic_dataprocs.get(pt.getName());
			if (args == null
				|| args.schedule == null
					|| args.schedule.length() == 0
					|| !procthreads_errortime.containsKey(pt.getName()))
			{
				// 一次性运行，即没有指定 schedule 参数，即使处理过程出错也不再重试运行
				// 或者 schedule 已经执行完毕，即处理过程正常结束
				// 
				// 一般情况下,动态启动循环处理过程的脚本不会循环执行,否则可能会导致资源耗尽
				// 针对动态处理过程,如果 schedule 参数有错,就会再次试运行,
				// 目的是提示启动脚本的参数错误
				//
				dynamic_dataprocs.remove(pt.getName());
			}
			else
			{
				// 指定了有效 schedule 参数的情况下,处理过程出错结束时,需要尝试再次运行
				// do nothing
			}
			procthreads_lastendtime.put(pt.getName(), System.currentTimeMillis());
			procthreads_running.remove(pt.getName());
			this.lastResult = result;
		}
	}

	public void procThreadInfo(ProcThread pt, String message)
	{
		log.info("thread '" + pt.getName() + "' " + message);
	}

	public void procThreadWarn(ProcThread pt, String message)
	{
		log.warn("thread '" + pt.getName() + "' warning, " + message);
	}

	/**
	 * SQL运行中发生错误，错误发生后会等待一段时间（data.proc.error.retry.interval.seconds）才会继续运行
	 */
	public void procThreadError(ProcThread pt, String message, Throwable e)
	{
		log.error(
			"thread '" + pt.getName() + "' error" + (message != null && message.length() > 0?(", " + message):"."), e);
		synchronized(procthreads_running)
		{
			procthreads_errortime.put(pt.getName(), System.currentTimeMillis());
			this.lastError = e != null?e:new Exception(message);
		}
	}

	private int reorganize()
	{
		synchronizeProcThreads();
		checkCleanupProcThreads();
		checkRunningTimeoutProcs();
		return runningProcsCount();
	}

	public int runningProcsCount()
	{
		synchronized(procthreads_running)
		{
			return procthreads_running.size();
		}
	}

	public String[] runningProcs()
	{
		SortedSet retset = new TreeSet();
		synchronized(procthreads_running)
		{
			retset.addAll(procthreads_running.keySet());
		}
		return (String[])retset.toArray(new String[0]);
	}

	private void checkRunningTimeoutProcs()
	{
		long dprts = SystemConfig.getLong("data.proc.running.timeout.seconds", 30);
		long dpkts = SystemConfig.getLong("data.proc.timeout.kill.seconds", 0);
		synchronized(procthreads_running)
		{
			Iterator<Entry<ProcThread, Long>> pti = procthreads_starttime.entrySet().iterator();
			while(pti.hasNext())
			{
				Entry<ProcThread, Long> ptce = pti.next();
				ProcThread pt = ptce.getKey();
				long starttime = ptce.getValue();
				long rt = 1000L * SystemConfig.getLong(pt.getName() + ".running.timeout.seconds", dprts);
				if (System.currentTimeMillis() > starttime + rt)
				{
					if (!timeoutThreads.containsKey(pt) || timeoutThreads.get(pt) != starttime)
					{
						log.warn("thread '" + pt.getName() + "' running timeout.");
						timeoutThreads.put(pt, starttime);
					}
					else
					{
						long kt = 1000L * SystemConfig.getLong(pt.getName() + ".timeout.kill.seconds", dpkts);
						if (kt > 0 && System.currentTimeMillis() > starttime + kt)
						{
							log.warn("thread '" + pt.getName() + "' timeout kill.");
							pt.destroy(0);
							synchronized(toCleanupThreads)
							{
								if (!toCleanupThreads.containsKey(pt))
								{
									toCleanupThreads.put(pt, 0L);
								}
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void forceStopThread(Thread t)
	{
		t.stop();
	}

	private void checkCleanupProcThreads()
	{
		long t = 1000L * SystemConfig.getLong("data.proc.safe.stop.wait.seconds", 30);
		synchronized(toCleanupThreads)
		{
			Iterator<Entry<ProcThread, Long>> pti = toCleanupThreads.entrySet().iterator();
			while(pti.hasNext())
			{
				Entry<ProcThread, Long> ptce = pti.next();
				ProcThread pt = ptce.getKey();
				if (pt.isAlive())
				{
					if (ptce.getValue() == 0)
					{
						ptce.setValue(System.currentTimeMillis());
						pt.destroy(0);
					}
					else if (System.currentTimeMillis() > ptce.getValue() + t)
					{
						log.warn("thread '" + pt.getName() + "' maybe deadlock, force stop it.");
						{
							forceStopThread(pt);
						}
						pti.remove();
						synchronized(deadLockThreads)
						{
							deadLockThreads.put(pt, System.currentTimeMillis());
						}
					}
					else
					{
						pt.interrupt();
					}
				}
				else
				{
					pti.remove();
				}
			}
		}
	}

	private Object execute_inner(String proc_name, String sqlfiles, Map parameters) throws Exception
	{
		Map params = new HashMap(this.parameters);
		if (parameters != null)
		{
			params.putAll(parameters);
		}
		ProcThread.Args args = new ProcThread.Args(sqlfiles, null, 0, 0, params, common_script, common_script_filename);
		ProcThread pt = new ProcThread(this, this, proc_name, args);
		pt.run();
		Object r = pt.getLastResult();
		Throwable e = pt.getLastError();
		if (e != null)
		{
			if (e instanceof Exception)
			{
				throw (Exception)e;
			}
			else
			{
				throw new Exception(e);
			}
		}
		return r;
	}

	public Object execute(String procname, String sqlfiles, Map parameters) throws Exception
	{
		return execute_inner(procname, sqlfiles, parameters);
	}

	public void start(String procname, String sqlfiles, String schedule, long start_delay, long min_interval,
		Map parameters)
	{
		Map params = new HashMap(this.parameters);
		if (parameters != null)
		{
			params.putAll(parameters);
		}
		ProcThread.Args args = new ProcThread.Args(sqlfiles,
			schedule,
			start_delay,
			min_interval,
			params,
			common_script,
			common_script_filename);
		boolean changed = false;
		synchronized(procthreads_running)
		{
			if (!args.equals(dynamic_dataprocs.get(procname)))
			{
				dynamic_dataprocs.put(procname, args);
				changed = true;
			}
		}
		if (changed)
		{
			reorganize();
		}
	}

	private Map share_map = new HashMap();

	public void share(String sharename, Object object)
	{
		synchronized(share_map)
		{
			share_map.put(sharename, object);
		}
	}

	public Object share(String sharename)
	{
		synchronized(share_map)
		{
			return share_map.get(sharename);
		}
	}

	public String status(String procname)
	{
		ProcThread pt;
		synchronized(procthreads_running)
		{
			pt = procthreads_running.get(procname);
			if (pt == null)
			{
				//处理线程未开始 or 已结束
				if (procthreads_errortime.containsKey(procname))
				{
					return "E"; // error
				}
				else if (procthreads_lastendtime.containsKey(procname))
				{
					return "C"; // complete
				}
				else
				{
					return "N"; // none
				}
			}
			//处理线程已开始未结束
			if (procthreads_starttime.containsKey(pt))
			{
				//处理过程开始
				return "R"; // running
			}
		}
		//处理过程未开始 or 已结束
		return "W"; // waiting
	}

	public void stop(String procname)
	{
		synchronized(procthreads_running)
		{
			dynamic_dataprocs.remove(procname);
		}
	}

	private ProcThread procThreadSynchronize(String procname, ProcThread.Args args)
	{
		synchronized(procthreads_running)
		{
			ProcThread pt = procthreads_running.get(procname);
			if (pt == null)
			{
				pt = new ProcThread(this, this, procname, args);
				procthreads_running.put(procname, pt);
				pt.start();
			}
			else
			{
				pt.refresh(args);
			}
			return pt;
		}
	}

	private SortedMap<String, ProcThread.Args> dynamic_dataprocs = new TreeMap();
	private SortedMap<String, ProcThread.Args> config_dataprocs = new TreeMap();

	private SortedMap<String, ProcThread.Args> getDataProcs()
	{
		SortedMap<String, ProcThread.Args> dataprocs = new TreeMap();
		dataprocs.putAll(config_dataprocs);
		synchronized(procthreads_running)
		{
			dataprocs.putAll(dynamic_dataprocs);
		}
		return dataprocs;
	}

	private void synchronizeProcThreads()
	{
		//		synchronized(procthreads_running)
		//		{
		//			//最后处理线程结束状态保留一天
		//			//  修改, 需要永久保留, 否则需要一次性运行的过程会在一天后重新启动
		//			Iterator<Entry<String, Long>> lastEndTimeESI = procthreads_lastendtime.entrySet().iterator();
		//			while(lastEndTimeESI.hasNext())
		//			{
		//				Entry<String, Long> e = lastEndTimeESI.next();
		//				String procname = e.getKey();
		//				Long lastendtime = e.getValue();
		//				if (System.currentTimeMillis() > lastendtime + 24 * 3600 * 1000L)
		//				{
		//					lastEndTimeESI.remove();
		//				}
		//			}
		//		}
		long error_retry_interval = 1000L * SystemConfig.getLong("data.proc.error.retry.interval.seconds", 30);
		HashSet<String> alive_procnames = new HashSet();
		synchronized(procthreads_running)
		{
			SortedMap<String, ProcThread.Args> dataprocs = getDataProcs();
			Iterator<Entry<String, ProcThread.Args>> dpesi = dataprocs.entrySet().iterator();
			while(dpesi.hasNext())
			{
				Entry<String, ProcThread.Args> dpe = dpesi.next();
				String procname = dpe.getKey();
				ProcThread.Args args = dpe.getValue();
				Long last_error_occurs_time;
				Long last_end_time;
				synchronized(procthreads_running)
				{
					last_error_occurs_time = procthreads_errortime.get(procname);
					last_end_time = procthreads_lastendtime.get(procname);
				}
				if (((last_error_occurs_time == null && last_end_time == null)
					|| (last_error_occurs_time != null
						&& System.currentTimeMillis() > (last_error_occurs_time.longValue() + error_retry_interval)))
					&& args.sqlfiles != null
						&& args.sqlfiles.length() > 0)
				{
					synchronized(procthreads_running)
					{
						procthreads_errortime.remove(procname);
					}
					ProcThread pt = procThreadSynchronize(procname, args);
					alive_procnames.add(procname);
				}
			}
			Iterator<ProcThread> pti = procthreads_running.values().iterator();
			while(pti.hasNext())
			{
				ProcThread pt = pti.next();
				if (!alive_procnames.contains(pt.getName()))
				{
					synchronized(toCleanupThreads)
					{
						if (!toCleanupThreads.containsKey(pt))
						{
							toCleanupThreads.put(pt, 0L);
						}
					}
				}
			}
		}
	}

	public void cleanupAll()
	{
		synchronized(procthreads_running)
		{
			Iterator<ProcThread> pti = procthreads_running.values().iterator();
			while(pti.hasNext())
			{
				ProcThread pt = pti.next();
				{
					synchronized(toCleanupThreads)
					{
						if (!toCleanupThreads.containsKey(pt))
						{
							toCleanupThreads.put(pt, 0L);
						}
					}
				}
			}
		}
		waitForSafeStop();
		stopped = true;
	}

	private void waitForSafeStop()
	{
		synchronized(toCleanupThreads)
		{
			while(toCleanupThreads.size() > 0)
			{
				checkCleanupProcThreads();
				if (toCleanupThreads.size() > 0)
				{
					try
					{
						Thread.sleep(10);
					}
					catch(InterruptedException e)
					{
					}
				}
			}
		}
	}
}
