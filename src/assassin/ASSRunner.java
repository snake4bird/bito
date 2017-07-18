package assassin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import assassin.script.ASScriptEngine;
import bito.util.cfg.SystemConfig;
import bito.util.logger.Log;

public class ASSRunner
{
	public static void main(String[] args)
	{
		SystemConfig.getStamp();
		new ASSRunner().run(args);
	}

	public final assassin.script.ASSRunner runner = new assassin.script.ASSRunner();

	public ASSRunner()
	{
	}

	private Log log = new Log(":assassin");
	//
	private Thread shutdownhook = new Thread()
	{
		public void run()
		{
			log.info("break end.");
		}
	};

	public void run(String[] args)
	{
		if (args != null && args.length > 0 && args[0].equals("?"))
		{
			System.out.println("Usage: java [options] assassin.ASSRunner \"act.sql.script.files;\"");
			return;
		}
		//系统初始化
		long tnorunning = 0;
		int runningcount = 0;
		log.info("begin.");
		try
		{
			Runtime.getRuntime().addShutdownHook(shutdownhook);
		}
		catch(Exception e)
		{
		}
		//
		if (args != null && args.length > 0)
		{
			int n = 1;
			String prefix = "data.proc.assassin#";
			String suffix = ".script.files";
			for(int i = 0; i < args.length; i++)
			{
				while(System.getProperty(prefix + n + suffix) != null)
				{
					n++;
				}
				System.setProperty(prefix + n + suffix, args[i]);
			}
			System.setProperty("data.proc.system.exit.wait.seconds",
				System.getProperty("data.proc.system.exit.wait.seconds", "0"));
		}
		//系统运行
		while(!runner.isStopped()
			&& (args == null
				|| runningcount > 0
					|| tnorunning == 0
					|| System.currentTimeMillis() < tnorunning
						+ (1000L * SystemConfig.getLong("data.proc.system.exit.wait.seconds", 30))))
		{
			runningcount = runner.run();
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
		}
		if (!runner.isStopped())
		{
			if (runningcount == 0)
			{
				log.info("all data proc thread is end.");
			}
			runner.cleanupAll();
			try
			{
				//系统正常终止
				Runtime.getRuntime().removeShutdownHook(shutdownhook);
			}
			catch(Exception e)
			{
			}
			log.info("end.");
		}
	}
}
