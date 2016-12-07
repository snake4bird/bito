package bito.ass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import bito.util.cfg.SystemConfig;
import bito.util.logger.Log;

public class ASSRunner
{
	ASSRunner o = (ASSRunner)d.E.V().o();

	public ASSRunner()
	{
	}

	public void setSystemInitializeScriptFilename(String initscript_filename)
	{
		o.setSystemInitializeScriptFilename(initscript_filename);
	}

	public void setCommonIncludeScriptFilename(String initscript_filename)
	{
		o.setCommonIncludeScriptFilename(initscript_filename);
	}

	public void setCommonIncludeScript(String initscript)
	{
		o.setCommonIncludeScript(initscript);
	}

	public void setParameter(String key, Object value)
	{
		o.setParameter(key, value);
	}

	public Object getResult()
	{
		return o.getResult();
	}

	public Throwable getLastError()
	{
		return o.getLastError();
	}

	/**
	 * 按配置信息运行
	 * @return
	 */
	public int run()
	{
		return o.run();
	}

	/**
	 * 执行指定脚本
	 * @return
	 * @throws Exception 
	 */
	public Object execute(String procname, String sqlfiles, Map parameters) throws Exception
	{
		return o.execute(procname, sqlfiles, parameters);
	}

	/**
	 * 清除系统资源
	 */
	public void cleanupAll()
	{
		o.cleanupAll();
	}

	/**
	 * 启动新的处理线程
	 * @param procname 处理线程名称
	 * @param sqlfiles 处理过程文件名，相对系统当前路径
	 * @param schedule 系统运行时间安排，cron表达式，可以为null
	 * @param start_delay  首次启动延迟秒，0立即启动，-1只按cron表达式运行
	 * @param min_interval 处理过程两次执行的最小时间间隔秒
	 * @param parameters 指定处理过程参数
	 */
	public void start(String procname, String sqlfiles, String schedule, long start_delay, long min_interval,
		Map parameters)
	{
		o.start(procname, sqlfiles, schedule, start_delay, min_interval, parameters);
	}

	/**
	 * 返回处理过程运行状态 
	 * 		"N" (none，线程不存在) 
	 * 		"W" (waiting，线程已经启动，但未执行处理过程) 
	 * 		"R" (running，处理过程正在执行) 
	 * 		"E" (error，处理过程异常结束) 
	 * 		"C" (completed，处理线程正常结束) 
	 * 处理线程结束后，状态保留一天，超时后状态归为"N" (none)
	 */
	public String status(String procname)
	{
		return o.status(procname);
	}

	/**
	 * 当前处理过程结束后，终止处理线程，
	 * 即，停止处理线程，但不会中断正在执行的处理过程
	 */
	public void stop(String procname)
	{
		o.stop(procname);
	}

	/**
	 * 正在运行的处理过程
	 */
	public String[] runningProcs()
	{
		return o.runningProcs();
	}

	/**
	 * 正在运行的处理过程数量
	 */
	public int runningProcsCount()
	{
		return o.runningProcsCount();
	}

	// main
	public static void main(String[] args)
	{
		if (args.length > 0 && "?".equals(args[0]))
		{
			System.out.println("Usage:");
			System.out.println("  java [options] -Dstop.file=\"stop.file\" runner");
			System.out.println("  java [options] -Dsql.runner.schedule=\"* * * * * ?\" runner -start \"ass.files;\"");
			System.out.println("  java [options] runner \"act.sql.script.files;\"");
			System.out.println("Options:");
			System.out.println("  -Dconfig.file=\"config.txt\"");
			System.out.println("  -Dcommon.include.script=\"proc.init.js\"");
			System.out.println("  -Dsystem.init.ass=\"system.init.txt\"");
			System.out.println("  -DDB.driver=...");
			System.out.println("  -DDB.url=...");
			System.out.println("  -DDB.username=...");
			System.out.println("  -DDB.password=...");
			System.out.println("  -DDB.maxconnect=...");
			System.out.println("  -DDB.maxidleseconds=...");
			System.out.println("  -DDB.rows.limit=...");
			System.out.println("  -Dlog.file=logs/log.[yyyy][MM][dd].txt");
			System.out.println("  -Dlog.file.maxsizekb=10240");
			System.out.println("  -Dlog.file.maxbakidx=10");
			System.out.println("  -Dlog.level=debug");
			System.out.println("  -Dlog.stdout=info");
			System.out.println("  -Dconfig.refresh.interval.seconds=1");
			System.out.println("  -Ddata.proc.system.exit.wait.seconds=60 #wait for none proc");
			System.out.println("  -Ddata.proc.running.timeout.seconds=60 #timeout warning");
			System.out.println("  -Ddata.proc.safe.stop.wait.seconds=60 #wait for proc stop");
			System.out.println("  -Ddata.proc.error.retry.interval.seconds=10");
			System.out.println("  -D[data.proc.name.]sql.file=...");
			System.out.println("  -D[data.proc.name.]schedule.cron=...");
			System.out.println("  -D[data.proc.name.]loop.interval.min.seconds=...");
			System.out.println("  -D[data.proc.name.]start.delay.seconds=...");
			System.out.println("  -D[data.proc.name.]log.file=...");
			System.out.println("  -D[data.proc.name.]log.file.maxsizekb=...");
			System.out.println("  -D[data.proc.name.]log.file.maxbakidx=...");
			System.out.println("  -D[data.proc.name.]log.level=...");
			System.out.println("  -D[data.proc.name.]log.stdout=...");
			return;
		}
		ASSRunner ar = new ASSRunner();
		ar.go(args);
	}

	protected void go(String[] args)
	{
		Log log = new Log("");
		setCommonIncludeScriptFilename(SystemConfig.get("common.include.script", null));
		setSystemInitializeScriptFilename(SystemConfig.get("system.init.ass", null));
		if (args != null && args.length > 0)
		{
			setParameter("parameter", args);
			if (args.length > 1 && args[0].equals("-start"))
			{
				start("ass.runner", args[1], SystemConfig.get("sql.runner.schedule"), 0, 0, null);
				while(runningProcsCount() > 0)
				{
					try
					{
						Thread.sleep(100);
					}
					catch(InterruptedException e)
					{
					}
				}
				Object r = getResult();
				log.debug("return: " + r);
				Throwable e = getLastError();
				if (e != null)
				{
					log.error(e);
				}
			}
			else
			{
				try
				{
					Object r = execute("ass.runner", args[0], null);
					log.debug("return: " + r);
				}
				catch(Exception ee)
				{
					log.error(ee);
				}
			}
		}
		else
		{
			go(log);
		}
	}

	private void go(final Log log)
	{
		Thread hook = new Thread()
		{
			public void run()
			{
				log.info("break end.");
			}
		};
		//系统停止的标记文件
		File stopfile = new File(SystemConfig.get("stop.file", "stop"));
		try
		{
			//系统启动，清除停止标记文件
			if (stopfile.exists())
			{
				stopfile.delete();
			}
			//系统初始化
			long tnorunning = 0;
			int runningcount = 0;
			log.info("begin.");
			Runtime.getRuntime().addShutdownHook(hook);
			//系统运行
			while(!stopfile.exists()
				&& (runningcount > 0 || tnorunning == 0 || System.currentTimeMillis() < tnorunning
					+ (1000L * SystemConfig.getLong("data.proc.system.exit.wait.seconds", 30))))
			{
				runningcount = run();
				if (runningcount > 0)
				{
					tnorunning = 0;
				}
				else
				{
					if (tnorunning == 0)
					{
						tnorunning = System.currentTimeMillis();
					}
				}
				try
				{
					Thread.sleep(100);
				}
				catch(InterruptedException e)
				{
				}
				stopfile = new File(SystemConfig.get("stop.file", "stop"));
			}
			if (runningcount == 0)
			{
				log.info("all data proc thread is end.");
			}
			//系统正常终止
			cleanupAll();
			Runtime.getRuntime().removeShutdownHook(hook);
			log.info("end.");
		}
		catch(Throwable e)
		{
			log.error(e);
			//系统异常终止
		}
		finally
		{
			if (stopfile.exists())
			{
				stopfile.delete();
			}
			//系统终止前处理
		}
	}
}
