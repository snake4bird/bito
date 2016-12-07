package bito.ass._;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;

public class ProcThread extends Thread
{
	public static class Args
	{
		final String sqlfiles;
		final String schedule;
		final long start_delay;
		final long min_interval;
		final Map<String, Object> parameters;
		final String init_script;
		final String string;

		public Args(String sqlfiles, String schedule, long start_delay, long min_interval,
			Map<String, Object> parameters, String init_script)
		{
			this.sqlfiles = sqlfiles;
			this.schedule = schedule;
			this.start_delay = 1000L * start_delay;
			this.min_interval = 1000L * min_interval;
			this.parameters = parameters;
			this.init_script = init_script;
			{
				string = sqlfiles
					+ "\r\n"
						+ schedule
						+ "\r\n"
						+ start_delay
						+ "\r\n"
						+ min_interval
						+ "\r\n"
						+ parameters
						+ "\r\n"
						+ init_script
						+ "\r\n";
			}
		}

		public int hashCode()
		{
			return toString().hashCode();
		}

		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof Args))
			{
				return false;
			}
			return toString().equals(((Args)o).toString());
		}

		public String toString()
		{
			return string;
		}
	};

	private static final int SCHEDULE_RUN_ONCE = 0;
	private static final int SCHEDULE_IS_END = -1;
	private static final int SCHEDULE_NONE_CONFIG = -11;
	private static final int SCHEDULE_NO_NEXT_VALID = -12;
	private static final int SCHEDULE_FORMAT_ERROR = -100;

	private class Cron
	{
		private CronExpression cron;
		private long start_delay;
		private long min_interval;
		private long dt_nextrun_starttime;
		private Date dt_nextrun = null;
		private long dt_lastrun_endtime = 0;

		private Cron(String schedule, long start_delay, long min_interval)
		{
			try
			{
				if (args.schedule != null && args.schedule.length() > 0)
				{
					this.cron = new CronExpression(schedule);
				}
				else
				{
					this.cron = null;
				}
				dt_nextrun_starttime = 0;
			}
			catch(ParseException e)
			{
				this.cron = null;
				lastError = e;
				dt_nextrun_starttime = SCHEDULE_FORMAT_ERROR;
			}
			this.start_delay = start_delay;
			this.min_interval = min_interval;
		}

		private void setLastRunEnd()
		{
			dt_lastrun_endtime = System.currentTimeMillis();
			dt_nextrun_starttime = 0;
		}

		private long getNextRunStartTime()
		{
			if (dt_nextrun_starttime == 0)
			{
				if (dt_nextrun == null)
				{
					if (start_delay >= 0)
					{
						dt_nextrun = new Date(System.currentTimeMillis() + start_delay);
					}
					else if (cron != null)
					{
						Date dt = cron.getNextValidTimeAfter(new Date(System.currentTimeMillis() - 1000L));
						while(System.currentTimeMillis() / 1000 > dt.getTime() / 1000)
						{
							dt_nextrun = dt;
							dt = cron.getNextValidTimeAfter(dt);
						}
						dt_nextrun = dt;
					}
					else
					{
						return SCHEDULE_NONE_CONFIG;
					}
				}
				else if (cron != null)
				{
					dt_nextrun = cron.getNextValidTimeAfter(dt_nextrun);
				}
				else
				{
					dt_nextrun = null;
				}
				if (dt_nextrun == null)
				{
					if (cron != null)
					{
						return SCHEDULE_NO_NEXT_VALID;
					}
					else if (start_delay == 0 && min_interval == 0)
					{
						return SCHEDULE_RUN_ONCE;
					}
					else
					{
						return SCHEDULE_IS_END;
					}
				}
				dt_nextrun_starttime = dt_nextrun.getTime();
				if (dt_nextrun_starttime < dt_lastrun_endtime + args.min_interval)
				{
					dt_nextrun_starttime = dt_lastrun_endtime + args.min_interval;
					dt_nextrun = new Date(dt_nextrun_starttime);
				}
			}
			return dt_nextrun_starttime;
		}
	}

	private Args lastArgs;
	private Args args;
	private Cron cron;
	private boolean terminate = false;
	private RunSQL rs;
	private ProcThreadMonitor ptmonitor;
	private Object lastResult = null;
	private Throwable lastError = null;

	public ProcThread(ProcThreadMonitor ptmonitor, ProcThreadManager ptmanager, String procname, Args args)
	{
		super(procname);
		this.setDaemon(true);
		this.ptmonitor = ptmonitor;
		this.lastArgs = null;
		this.args = args;
		this.rs = new RunSQL(ptmanager, procname);
	}

	public void refresh(Args args)
	{
		this.args = args;
	}

	public Object getLastResult()
	{
		return lastResult;
	}

	public Throwable getLastError()
	{
		return lastError;
	}

	public void run()
	{
		lastResult = null;
		lastError = null;
		lastArgs = null;
		cron = null;
		try
		{
			while(!terminate)
			{
				if (args != lastArgs
					&& (lastArgs == null
						|| lastArgs.schedule == null
							|| !lastArgs.schedule.equals(args.schedule)
							|| lastArgs.start_delay != args.start_delay || lastArgs.min_interval != args.min_interval))
				{
					cron = new Cron(args.schedule, args.start_delay, args.min_interval);
				}
				long dt_nextrun_starttime = cron.getNextRunStartTime();
				if (dt_nextrun_starttime <= 0)
				{
					switch((int)dt_nextrun_starttime)
					{
					case SCHEDULE_NONE_CONFIG:
						ptmonitor.procThreadWarn(ProcThread.this, "config disabled.");
						break;
					case SCHEDULE_FORMAT_ERROR:
						ptmonitor.procThreadError(this,
							"cron expression '" + args.schedule + "' format error.",
							lastError);
						break;
					case SCHEDULE_NO_NEXT_VALID:
						ptmonitor.procThreadWarn(ProcThread.this, "no valid next run time.");
						break;
					case SCHEDULE_IS_END:
						ptmonitor.procThreadInfo(ProcThread.this, "is end.");
					case SCHEDULE_RUN_ONCE:
					default:
						break;
					}
					return;
				}
				else if (System.currentTimeMillis() >= dt_nextrun_starttime)
				{
					int returncode = -1;
					ptmonitor.procThreadProcBegin(this);
					try
					{
						if (args != lastArgs
							&& args.parameters != null
								&& args.parameters.size() > 0
								&& (lastArgs == null || !args.parameters.equals(lastArgs.parameters)))
						{
							Set<Entry<String, Object>> gkvs = args.parameters.entrySet();
							for(Entry<String, Object> kv : gkvs)
							{
								rs.setVariable(kv.getKey(), kv.getValue());
							}
						}
						if (args != lastArgs
							&& args.init_script != null
								&& args.init_script.length() > 0
								&& (lastArgs == null || !args.init_script.equals(lastArgs.init_script)))
						{
							rs.evalJScript(args.init_script);
						}
						String fn = args.sqlfiles;
						if (fn != null && fn.length() > 0)
						{
							rs.setVariable("result", null);
							returncode = rs.run(args.sqlfiles);
							lastResult = rs.getVariable("result");
							if (returncode != 0)
							{
								ptmonitor.procThreadError(this, "error occurs when sql running.", null);
							}
						}
					}
					finally
					{
						ptmonitor.procThreadProcEnd(this);
					}
					// 处理过程正确运行结束，准备下次运行
					cron.setLastRunEnd();
					lastArgs = this.args;
				}
				else
				{
					// 等待下次运行
					long waittime = (dt_nextrun_starttime - System.currentTimeMillis()) / 10;
					if (waittime > 1000)
					{
						// 最长等待一秒，以检查参数是否发生变化
						waittime = 1000;
					}
					if (waittime > 0)
					{
						try
						{
							sleep(waittime);
						}
						catch(InterruptedException e)
						{
						}
					}
				}
			}
		}
		catch(Throwable e)
		{
			lastError = e;
			ptmonitor.procThreadError(this, null, e);
		}
		finally
		{
			rs.cancel();
			ptmonitor.procThreadEnd(this, lastResult);
		}
	}

	public void destroy(long wait_timeout)
	{
		terminate = true;
		this.interrupt();
		if (wait_timeout > 0)
		{
			try
			{
				join(wait_timeout);
			}
			catch(InterruptedException e)
			{
			}
		}
	}
}
