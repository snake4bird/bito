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
	 * ����ļ�ȷ��������
	 * 1����ϵͳ���� [id.]log.file �ж�������ļ�
	 * 2.1 .id�ԡ�:id����ʼ����ϵͳ���� [id.]log.file �ж�������ļ�
	 * 2.2 .id�ԡ�:id����ʼ����ϵͳ������ [id.]log.file û�ж��壬��ϵͳ���� log.file �ж�������ļ�
	 * 2.3 .id�ԡ�:id����ʼ����ϵͳ������ [id.]log.file �� log.file ��û�ж��壬�̶�����ļ� log.txt
	 * 3.1��id���á�/��ָ����·������ [id] ��Ϊ����ļ�
	 * 3.2��id��ָ���˺�׺�ļ������� logs/[id] ��Ϊ����ļ�
	 * 3.3��id��Ϊ�գ��� logs/[id]_log.txt ��Ϊ����ļ�
	 * 3.4��idΪ�գ��̶�����ļ� logs/log.txt
	 * 
	 * �ļ����п��԰���[yyyy][MM][dd][HH][mm]��Ϊ��ǰʱ��������ʱ�ֵ��滻���
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
