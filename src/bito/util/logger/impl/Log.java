package bito.util.logger.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import bito.util.cfg.SystemConfig;

import d.FileAppender;

public class Log implements bito.util.logger.lvl.Log
{
	private static HashMap level_map = new HashMap();
	static
	{
		level_map.put("all", new Integer(level_all));
		level_map.put("debug", new Integer(level_debug));
		level_map.put("info", new Integer(level_info));
		level_map.put("warn", new Integer(level_warn));
		level_map.put("error", new Integer(level_error));
		level_map.put("none", new Integer(level_none));
	}
	//
	private static HashMap facache = new HashMap();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	private static final String default_logfile = "log.txt";
	//
	private String id = null;
	private Properties config = null;
	private int level = level_all;
	private int stdout_level = level_none;
	private FileAppender fa = null;
	private ThreadLocal tlcatlog = new ThreadLocal();
	private boolean initializing = false;
	private boolean initialized = false;

	//
	private class BufferedLogItem
	{
		final int level;
		final String log;
		final Throwable e;

		public BufferedLogItem(int level, String log, Throwable e)
		{
			this.level = level;
			this.log = log;
			this.e = e;
		}
	}

	private int bufferedLogMaxLines = 100;
	private LinkedList<BufferedLogItem> bufferedLog = new LinkedList();

	public Log()
	{
	}

	public void init(String id)
	{
		synchronized(this)
		{
			if (initializing)
			{
				return;
			}
			initializing = true;
		}
		this.id = id;
		String logfile = null;
		if (id.startsWith(":"))
		{
			id = id.substring(1);
			logfile = SystemConfig.get((id.length() == 0?"":(id + ".")) + "log.file",
				SystemConfig.get("log.file", default_logfile));
		}
		else
		{
			logfile = SystemConfig.get((id.length() == 0?"":(id + ".")) + "log.file", //有明确定义
				(id.indexOf("/") >= 0)?id //id中用“/”指定了路径，[id]
						:("logs/" + ((id.indexOf(".") > 0)?id //id中指定了后缀文件名，logs/[id]
								:(((id.length() > 0)?(id + "_") //id不为空，logs/[id]_log.txt
										:("")) + "log.txt")))); //id为空，固定输出文件 logs/log.txt
		}
		Properties config = new Properties();
		config.setProperty("log.file", logfile);
		config.setProperty("log.level", SystemConfig.get(id + ".log.level", SystemConfig.get("log.level", "all")));
		config.setProperty("log.stdout", SystemConfig.get(id + ".log.stdout", SystemConfig.get("log.stdout", "true")));
		config.setProperty("log.file.maxsizekb",
			SystemConfig.get(id + ".log.file.maxsizekb", SystemConfig.get("log.file.maxsizekb", "1024")));
		config.setProperty("log.file.maxbakidx",
			SystemConfig.get(id + ".log.file.maxbakidx", SystemConfig.get("log.file.maxbakidx", "10")));
		synchronized(this)
		{
			initializing = false;
		}
		init(config);
	}

	public void init(Properties config)
	{
		synchronized(this)
		{
			if (initializing)
			{
				return;
			}
			initializing = true;
		}
		this.config = config;
		String slv = config.getProperty("log.level", "all").toLowerCase();
		Integer il = (Integer)level_map.get(slv);
		if (il == null && slv.matches("\\d+"))
		{
			il = Integer.valueOf(slv);
		}
		level = (il == null)?level_all:il.intValue();
		String soslv = config.getProperty("log.stdout", "true").toLowerCase();
		if ("true".equals(soslv))
		{
			stdout_level = level;
		}
		else
		{
			Integer soil = (Integer)level_map.get(soslv);
			if (soil == null && soslv.matches("\\d+"))
			{
				soil = Integer.valueOf(soslv);
			}
			stdout_level = (soil == null)?level_none:soil.intValue();
		}
		try
		{
			bufferedLogMaxLines = Integer.parseInt(config.getProperty("log.buffered.max.lines", "100"));
		}
		catch(NumberFormatException nfe)
		{
			bufferedLogMaxLines = 100;
		}
		long maxfilesize;
		try
		{
			maxfilesize = Long.parseLong(config.getProperty("log.file.maxsizekb", "1024")) * 1024;
		}
		catch(NumberFormatException nfe)
		{
			maxfilesize = 1024 * 1024;
		}
		int maxbackindex;
		try
		{
			maxbackindex = Integer.parseInt(config.getProperty("log.file.maxbakidx", "10"));
		}
		catch(NumberFormatException nfe)
		{
			maxbackindex = 10;
		}
		String logfilename = config.getProperty("log.file", "");
		if (logfilename.length() > 0)
		{
			fa = getFileAppender(logfilename, maxfilesize, maxbackindex);
		}
		synchronized(this)
		{
			initializing = false;
		}
		if (!initialized)
		{
			StackTraceElement[] st = new Throwable().getStackTrace();
			boolean log_init_recurve = false;
			for(int i = 1; !log_init_recurve && i < st.length; i++)
			{
				log_init_recurve = (st[i].getMethodName().equals("<clinit>") && st[i].getClassName()
					.equals(SystemConfig.class.getName()));
			}
			initialized = !log_init_recurve;
			if (initialized)
			{
				writeLog(level_none, "", null);
			}
			else
			{
				new Thread("log.init")
				{
					public void run()
					{
						try
						{
							sleep(1);
						}
						catch(InterruptedException e)
						{
						}
						reinit();
					}
				}.start();
			}
		}
	}

	private void reinit()
	{
		if (id != null)
		{
			init(id);
		}
		else
		{
			init(config);
		}
	}

	private long lastRefreshConfigTime = System.currentTimeMillis();

	private void reInitialize()
	{
		if (System.currentTimeMillis() > lastRefreshConfigTime + 1000)
		{
			reinit();
			lastRefreshConfigTime = System.currentTimeMillis();
		}
	}

	private FileAppender getFileAppender(String logfilename, long maxfilesize, int maxbackindex)
	{
		synchronized(facache)
		{
			FileAppender fa = (FileAppender)facache.get(logfilename);
			if (fa == null)
			{
				fa = d.E.V().newFileAppender(logfilename, maxfilesize, maxbackindex);
				facache.put(logfilename, fa);
			}
			return fa;
		}
	}

	public void any(String msg)
	{
		log(level_all, msg, null);
	}

	public void any(String msg, Throwable e)
	{
		log(level_all, msg, e);
	}

	public void any(Throwable e)
	{
		log(level_all, null, e);
	}

	public void debug(String msg)
	{
		log(level_debug, msg, null);
	}

	public void debug(String msg, Throwable e)
	{
		log(level_debug, msg, e);
	}

	public void debug(Throwable e)
	{
		log(level_debug, null, e);
	}

	public void info(String msg)
	{
		log(level_info, msg, null);
	}

	public void info(String msg, Throwable e)
	{
		log(level_info, msg, e);
	}

	public void info(Throwable e)
	{
		log(level_info, null, e);
	}

	public void warn(String msg)
	{
		log(level_warn, msg, null);
	}

	public void warn(String msg, Throwable e)
	{
		log(level_warn, msg, e);
	}

	public void warn(Throwable e)
	{
		log(level_warn, null, e);
	}

	public void error(String msg)
	{
		log(level_error, msg, null);
	}

	public void error(String msg, Throwable e)
	{
		log(level_error, msg, e);
	}

	public void error(Throwable e)
	{
		log(level_error, null, e);
	}

	public void setThreadLocalCatlog(String s)
	{
		tlcatlog.set(s);
	}

	public int getLevel()
	{
		return this.level;
	}

	public void setLevel(int lvl)
	{
		this.level = lvl;
	}

	public void log(int level, String msg, Throwable e)
	{
		reInitialize();
		if (this.level < level && this.stdout_level < level)
		{
			return;
		}
		if (msg == null)
		{
			if (e == null)
			{
				return;
			}
			try
			{
				msg = e.getMessage();
			}
			catch(Exception ex)
			{
				msg = e.getClass().getName() + " : " + ex.getClass().getName();
			}
		}
		String s = (String)tlcatlog.get();
		if (s != null && s.length() > 0)
		{
			msg = s + " " + msg;
		}
		else
		{
			msg = "[" + Thread.currentThread().getName() + "] " + msg;
		}
		String log = sdf.format(new Date()) + " " + getLevelString(level) + " - " + msg + "\r\n";
		writeLog(level, log, e);
	}

	private void writeBufferedLog(int level, String log, Throwable e) throws IOException
	{
		BufferedLogItem bli = new BufferedLogItem(level, log, e);
		synchronized(bufferedLog)
		{
			bufferedLog.add(bli);
			if (bufferedLog.size() > bufferedLogMaxLines)
			{
				bufferedLog.removeFirst();
			}
		}
	}

	private void writeLog(int level, String log, Throwable e)
	{
		try
		{
			if (!initialized)
			{
				writeBufferedLog(level, log, e);
			}
			else
			{
				synchronized(bufferedLog)
				{
					while(bufferedLog.size() > 0)
					{
						BufferedLogItem bli = bufferedLog.getFirst();
						outputLog(bli.log, bli.e, this.level >= bli.level, this.stdout_level >= bli.level);
						bufferedLog.removeFirst();
					}
				}
				try
				{
					outputLog(log, e, this.level >= level, this.stdout_level >= level);
				}
				catch(Exception ioe)
				{
					writeBufferedLog(level, log, e);
					writeBufferedLog(level_error, "write log error.", ioe);
				}
			}
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}

	private void outputLog(String log, Throwable e, boolean fileout, boolean stdout) throws IOException
	{
		if ((log == null || log.length() == 0) && e == null)
		{
			return;
		}
		if (stdout)
		{
			System.out.print(log);
			if (e != null)
			{
				e.printStackTrace(System.out);
			}
		}
		if (fa != null && fileout)
		{
			if (log.length() > 0 || e != null)
			{
				synchronized(fa)
				{
					fa.appendfile(log, e);
				}
			}
		}
	}

	private String getLevelString(int level)
	{
		switch(level)
		{
		case level_debug:
			return "DEBUG";
		case level_info:
			return "INFO ";
		case level_warn:
			return "WARN ";
		case level_error:
			return "ERROR";
		default:
			return "[" + level + "]";
		}
	}
}
