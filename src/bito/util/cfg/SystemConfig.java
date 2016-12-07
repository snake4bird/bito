package bito.util.cfg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public class SystemConfig
{
	private static LinkedHashSet<File> root_config_files = new LinkedHashSet();
	private static LinkedHashMap<File, ConfigFileParser> cfps = new LinkedHashMap();
	private static LinkedHashMap<File, Long> cfpstamp = new LinkedHashMap();
	private static LinkedHashMap<File, Properties> cfprops = new LinkedHashMap();
	private static long cfstamp = 0;
	private static long lastRefreshTime = 0;
	//
	private static Properties default_sysprop = null;
	private static Properties sysprop = new SystemProperties();

	private static class SystemProperties extends Properties
	{
		SystemProperties()
		{
			super(System.getProperties());
			default_sysprop = defaults;
			System.setProperties(this);
			merge(default_sysprop);
		}

		public void merge(Properties p)
		{
			Enumeration keys = p.keys();
			while(keys.hasMoreElements())
			{
				Object k = keys.nextElement();
				if (k instanceof String)
				{
					setProperty((String)k, p.getProperty((String)k));
				}
			}
		}

		private void refresh()
		{
			if (default_sysprop == null)
			{
				if (defaults != null)
				{
					System.setProperties(defaults);
				}
			}
			else
			{
				SystemConfig.refresh();
			}
		}

		public String getProperty(String name)
		{
			refresh();
			return super.getProperty(name);
		}

		public String getProperty(String name, String defaultValue)
		{
			refresh();
			return super.getProperty(name, defaultValue);
		}
	}

	static
	{
		File cf = new File(System.getProperty("config.file", "config.txt"));
		if (cf.exists())
		{
			addConfigFile(cf.getAbsolutePath());
		}
	}

	private static void refresh()
	{
		boolean refresh = false;
		synchronized(SystemConfig.class)
		{
			if (System.currentTimeMillis() > lastRefreshTime + 2500)
			{
				lastRefreshTime = System.currentTimeMillis();
				refresh = true;
			}
		}
		if (refresh)
		{
			rescan_config_files();
		}
		synchronized(SystemConfig.class)
		{
			if (refresh)
			{
				lastRefreshTime = System.currentTimeMillis();
			}
		}
	}

	public static void addConfigFile(String configfile)
	{
		File cf = new File(configfile);
		synchronized(root_config_files)
		{
			root_config_files.remove(cf);
			root_config_files.add(cf);
		}
		rescan_config_files();
	}

	public static void removeConfigFile(String configfile)
	{
		File cf = new File(configfile);
		synchronized(root_config_files)
		{
			root_config_files.remove(cf);
		}
		rescan_config_files();
	}

	private static void rescan_config_files()
	{
		String lastcfpstampstring = cfpstamp.toString();
		LinkedHashMap<File, ConfigFileParser> tmp_cfps = new LinkedHashMap();
		LinkedHashMap<File, Long> tmp_cfpstamp = new LinkedHashMap();
		LinkedHashMap<File, Properties> tmp_cfprops = new LinkedHashMap();
		synchronized(root_config_files)
		{
			Iterator<File> cfs = root_config_files.iterator();
			while(cfs.hasNext())
			{
				File cf = cfs.next();
				rescan_include_config_files(tmp_cfps, tmp_cfpstamp, tmp_cfprops, cf);
			}
		}
		if (!tmp_cfpstamp.toString().equals(lastcfpstampstring))
		{
			Properties ret_props = new Properties();
			File[] cfs = tmp_cfps.keySet().toArray(new File[0]);
			for(int i = 0; i < cfs.length; i++)
			{
				File cf = cfs[i];
				Properties new_props = tmp_cfprops.get(cf);
				mergeConfigure(ret_props, new_props);
				long lcstamp = tmp_cfpstamp.get(cf);
				if (cfstamp < lcstamp)
				{
					cfstamp = lcstamp;
				}
			}
			{
				sysprop.putAll(default_sysprop);
				sysprop.putAll(ret_props);
				Enumeration keys = sysprop.keys();
				while(keys.hasMoreElements())
				{
					Object k = keys.nextElement();
					if (k instanceof String && !default_sysprop.containsKey(k) && !ret_props.containsKey(k))
					{
						sysprop.remove(k);
					}
				}
			}
			cfps = tmp_cfps;
			cfprops = tmp_cfprops;
			cfpstamp = tmp_cfpstamp;
		}
	}

	private static void mergeConfigure(Properties ret_props, Properties new_props)
	{
		Enumeration keys = new_props.propertyNames();
		while(keys.hasMoreElements())
		{
			String k = (String)keys.nextElement();
			ret_props.setProperty(k, new_props.getProperty(k));
		}
	}

	private static void rescan_include_config_files(LinkedHashMap<File, ConfigFileParser> tmp_cfps,
		LinkedHashMap<File, Long> tmp_cfpstamp, LinkedHashMap<File, Properties> tmp_cfprops, File cf)
	{
		ConfigFileParser cfp = cfps.get(cf);
		if (cfp == null)
		{
			cfp = new ConfigFileParser(cf);
			cfp.setPrimaryKey(new String[]{});
			cfps.put(cf, cfp);
		}
		long lcstamp = cfp.getConfigureStamp();
		Properties props = cfprops.get(cf);
		Long stamp = cfpstamp.get(cf);
		if (props == null || stamp == null || stamp < lcstamp)
		{
			props = getConfigProperties(cfp);
			cfprops.put(cf, props);
			cfpstamp.put(cf, lcstamp);
		}
		{
			tmp_cfps.remove(cf);
			tmp_cfps.put(cf, cfp);
			tmp_cfpstamp.remove(cf);
			tmp_cfpstamp.put(cf, lcstamp);
			tmp_cfprops.remove(cf);
			tmp_cfprops.put(cf, props);
		}
		{
			String config_include = props.getProperty("config.include");
			if (config_include != null && config_include.length() > 0)
			{
				String[] nicfs = config_include.split("[;]");
				for(int i = 0; i < nicfs.length; i++)
				{
					String cfs = nicfs[i].trim();
					if (cfs.length() > 0)
					{
						rescan_include_config_files(tmp_cfps, tmp_cfpstamp, tmp_cfprops, new File(cfs));
					}
				}
			}
		}
	}

	private static Properties getConfigProperties(ConfigFileParser cfp)
	{
		Properties p = new Properties();
		SortedMap[] cms = cfp.getConfigure();
		if (cms != null && cms.length > 0)
		{
			for(Map m : cms)
			{
				Iterator i = m.entrySet().iterator();
				while(i.hasNext())
				{
					Map.Entry me = (Map.Entry)i.next();
					String k = (String)me.getKey();
					String v = (String)me.getValue();
					p.setProperty(k, v);
				}
			}
		}
		else
		{
			// do nothing
		}
		return p;
	}

	public static long getStamp()
	{
		return cfstamp;
	}

	public static double getDouble(String key, double defaultvalue)
	{
		try
		{
			String s = System.getProperty(key);
			return Double.parseDouble(s);
		}
		catch(Exception e)
		{
			return defaultvalue;
		}
	}

	public static long getLong(String key, long defaultvalue)
	{
		try
		{
			String s = System.getProperty(key);
			return Long.parseLong(s);
		}
		catch(Exception e)
		{
			return defaultvalue;
		}
	}

	public static int getInt(String key, int defaultvalue)
	{
		try
		{
			String s = System.getProperty(key);
			return Integer.parseInt(s);
		}
		catch(Exception e)
		{
			return defaultvalue;
		}
	}

	public static String get(String key)
	{
		return System.getProperty(key);
	}

	public static String get(String key, String defaultvalue)
	{
		return System.getProperty(key, defaultvalue);
	}

	public static Properties getProperties()
	{
		return System.getProperties();
	}

	public static boolean getBoolean(String key, boolean b)
	{
		String s = System.getProperty(key);
		if (b)
		{
			return !("n".equalsIgnoreCase(s)
				|| "no".equalsIgnoreCase(s)
					|| "f".equalsIgnoreCase(s)
					|| "false".equalsIgnoreCase(s) || "0".equals(s));
		}
		else
		{
			return ("y".equalsIgnoreCase(s)
				|| "yes".equalsIgnoreCase(s)
					|| "t".equalsIgnoreCase(s)
					|| "true".equalsIgnoreCase(s) || "1".equals(s));
		}
	}
}
