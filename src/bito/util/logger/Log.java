package bito.util.logger;

import java.util.HashMap;
import java.util.Properties;
import java.util.SortedMap;

public class Log implements bito.util.logger.lvl.Log
{
	private static SortedMap<Object, bito.util.logger.impl.Log> cache = bito.util.E.V().newMapSortedByAddTime();
	//
	private bito.util.logger.impl.Log impl;

	//
	/**
	 * 输出文件确定方法：
	 * 1·在系统属性 [id.]log.file 中定义输出文件
	 * 2.1 .id以“:id”开始，在系统属性 [id.]log.file 中定义输出文件
	 * 2.2 .id以“:id”开始，在系统属性中 [id.]log.file 没有定义，在系统属性 log.file 中定义输出文件
	 * 2.3 .id以“:id”开始，在系统属性中 [id.]log.file 和 log.file 都没有定义，固定输出文件 log.txt
	 * 3.1·id中用“/”指定了路径，以 [id] 做为输出文件
	 * 3.2·id中指定了后缀文件名，以 logs/[id] 做为输出文件
	 * 3.3·id不为空，以 logs/[id]_log.txt 作为输出文件
	 * 3.4·id为空，固定输出文件 logs/log.txt
	 * 
	 * 文件名中可以包含[yyyy][MM][dd][HH][mm]作为当前时间年月日时分的替换标记
	 */
	public Log(String sid)
	{
		String id = (sid == null?"":sid);
		impl = cache.get(id);
		if (impl == null)
		{
			impl = new bito.util.logger.impl.Log();
			cache.put(id, impl);
			if (cache.size() > 100)
			{
				cache.remove(cache.firstKey());
			}
			impl.init(id);
		}
	}

	public Log(Properties config)
	{
		config = config != null?config:new Properties();
		impl = cache.get(config);
		if (impl == null)
		{
			impl = new bito.util.logger.impl.Log();
			cache.put(config, impl);
			if (cache.size() > 100)
			{
				cache.remove(cache.firstKey());
			}
			impl.init(config);
		}
	}

	public void any(String msg)
	{
		impl.log(level_all, msg, null);
	}

	public void any(String msg, Throwable e)
	{
		impl.log(level_all, msg, e);
	}

	public void any(Throwable e)
	{
		impl.log(level_all, null, e);
	}

	public void debug(String msg)
	{
		impl.log(level_debug, msg, null);
	}

	public void debug(String msg, Throwable e)
	{
		impl.log(level_debug, msg, e);
	}

	public void debug(Throwable e)
	{
		impl.log(level_debug, null, e);
	}

	public void info(String msg)
	{
		impl.log(level_info, msg, null);
	}

	public void info(String msg, Throwable e)
	{
		impl.log(level_info, msg, e);
	}

	public void info(Throwable e)
	{
		impl.log(level_info, null, e);
	}

	public void warn(String msg)
	{
		impl.log(level_warn, msg, null);
	}

	public void warn(String msg, Throwable e)
	{
		impl.log(level_warn, msg, e);
	}

	public void warn(Throwable e)
	{
		impl.log(level_warn, null, e);
	}

	public void error(String msg)
	{
		impl.log(level_error, msg, null);
	}

	public void error(String msg, Throwable e)
	{
		impl.log(level_error, msg, e);
	}

	public void error(Throwable e)
	{
		impl.log(level_error, null, e);
	}

	public void setThreadLocalCatlog(String s)
	{
		impl.setThreadLocalCatlog(s);
	}

	public int getLevel()
	{
		return impl.getLevel();
	}

	public void setLevel(int lvl)
	{
		impl.setLevel(lvl);
	}

	public void log(int level, String msg, Throwable e)
	{
		impl.log(level, msg, e);
	}
}
