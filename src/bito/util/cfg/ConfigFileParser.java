package bito.util.cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bito.util.EscapeSequence;
import bito.util.logger.Log;

/**
 * <br>��ֵ KEY = VALUE mapping
 * 
 * <br>һ��key���Զ�Ӧ���value
 * <br>��ֵ�Ե���������˳���
 * 
 * <br>ÿ������ǰһ���ֵ�����ظ��ļ������ָ�Ϊһ��
 * 
 * <br>������Ϣ��Ӧ�ý���
 * 
 * <br>֧������ ��ֵ��
 * <br>key��value�Ŀ�ͷ�ͽ�β�հ��ַ��ᱻ���ˣ���Ҫ�Ŀհ��á�\\u0020,\\t,\\r,\\n,\\b������
 * <br>key�еĵ��ںš�=�� �� ��\\u003D������
 * <br>#��ͷΪע����
 * <br>valueת�峣�������ַ� \\uXXXX,\\t,\\r,\\n,\\\\
 * 
 * 
 * @author bird
 *
 */
public class ConfigFileParser
{
	private Log log = new Log("");
	private File resource_mappingfile = null;
	private long resource_mappingfile_mod_time = 0;
	private ArrayList configure = null;
	private String[] primaryKeys = null;
	private String defaultCharsetName = null;

	public ConfigFileParser(String filename)
	{
		resource_mappingfile = new File(filename);
	}

	public ConfigFileParser(File file)
	{
		resource_mappingfile = file;
	}

	public ConfigFileParser(String filename, String defaultCharsetName)
	{
		resource_mappingfile = new File(filename);
		this.defaultCharsetName = defaultCharsetName;
	}

	public ConfigFileParser(File file, String defaultCharsetName)
	{
		resource_mappingfile = file;
		this.defaultCharsetName = defaultCharsetName;
	}

	public void setLogger(Log log)
	{
		this.log = log;
	}

	private String loadMapping() throws IOException
	{
		if (!resource_mappingfile.exists())
		{
			// �����ļ�û���ҵ�
			throw new FileNotFoundException(resource_mappingfile.getCanonicalPath());
		}
		if (resource_mappingfile.lastModified() == resource_mappingfile_mod_time)
		{
			// �����ļ�û�и���
			return null;
		}
		long file_mod_time = resource_mappingfile.lastModified();
		ArrayList al = new ArrayList();
		FileInputStream fis = new FileInputStream(resource_mappingfile);
		String s;
		byte[] bs = bito.util.E.V().readBytes(fis);
		try
		{
			String csn = bito.util.E.V().checkTextFileCharset(bs);
			if ("UTF-8".equals(csn))
			{
				s = new String(bs, 3, bs.length - 3, csn);
			}
			else if (("UTF-16BE".equals(csn) || "UTF-16LE".equals(csn)))
			{
				s = new String(bs, 2, bs.length - 2, csn);
			}
			else if (("UTF-32BE".equals(csn) || "UTF-32LE".equals(csn)))
			{
				s = new String(bs, 4, bs.length - 4, csn);
			}
			else if (csn != null)
			{
				s = new String(bs, csn);
			}
			else if (defaultCharsetName != null)
			{
				s = new String(bs, defaultCharsetName);
			}
			else
			{
				s = new String(bs, "GBK");
			}
		}
		catch(UnsupportedEncodingException e)
		{
			s = new String(bs);
		}
		BufferedReader br = new BufferedReader(new StringReader(s));
		try
		{
			// ����Mapping
			String mapfile_format_warning = loadMapping(br, al);
			resource_mappingfile_mod_time = file_mod_time;
			configure = al;
			return mapfile_format_warning;
		}
		finally
		{
			br.close();
		}
	}

	private String loadMapping(BufferedReader br, ArrayList hmlist) throws IOException
	{
		HashMap pkset = new HashMap();
		SortedMap hm = bito.util.E.V().newMapSortedByAddTime();
		String[] mapfile_format_warning = new String[]{""};
		String clsname = null;
		String sub_source = null;
		String result = null;
		String line = null;
		String contline = null;
		for(int i = 0; (line = br.readLine()) != null; i++)
		{
			line = line.trim();
			if (line.startsWith("#") || line.length() == 0)
			{
				continue;
			}
			while(isContinueLine(line) && (contline = br.readLine()) != null)
			{
				i++;
				line = line.substring(0, line.length() - "\\".length());
				line += contline.trim();
			}
			if (line.length() > 0)
			{
				String[] item = line.split("=", 2);
				if (item.length > 0)
				{
					String key = EscapeSequence.decode(item[0].trim(), true);
					String val = "";
					if (item.length == 2)
					{
						val = EscapeSequence.decode(item[1].trim(), true);
					}
					if (hm.containsKey(key))
					{
						checkPrimaryKeysRedefined(pkset, hm, mapfile_format_warning);
						hmlist.add(hm);
						hm = bito.util.E.V().newMapSortedByAddTime();
					}
					hm.put(key, replaceVariable(val, hm, hmlist));
				}
			}
		}
		if (!hm.isEmpty())
		{
			checkPrimaryKeysRedefined(pkset, hm, mapfile_format_warning);
			hmlist.add(hm);
		}
		return mapfile_format_warning[0];
	}

	private static Pattern pvar = Pattern.compile("\\$\\{([^\\}]+)\\}");

	private String replaceVariable(String val, SortedMap hm, ArrayList hmlist) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		Matcher m = pvar.matcher(val);
		int n = 0;
		while(m.find())
		{
			for(int i = 1; i <= m.groupCount(); i++)
			{
				sb.append(val.substring(n, m.start())).append(getProperty(m.group(i), hm, hmlist));
				n = m.end();
			}
		}
		sb.append(val.substring(n));
		return sb.toString();
	}

	private String getProperty(String key, SortedMap hm, ArrayList hmlist) throws IOException
	{
		if ("this.file".equals(key))
		{
			return resource_mappingfile.getCanonicalPath();
		}
		if ("this.file.dir".equals(key))
		{
			return resource_mappingfile.getCanonicalFile().getParent();
		}
		String value = (String)hm.get(key);
		if (value != null)
		{
			return value;
		}
		for(int i = hmlist.size() - 1; i >= 0; i--)
		{
			value = (String)((Map)hmlist.get(i)).get(key);
			if (value != null)
			{
				return value;
			}
		}
		return System.getProperty(key, "${" + key + "}");
	}

	private void checkPrimaryKeysRedefined(HashMap pkset, SortedMap hm, String[] mapfile_format_warning)
	{
		String pk = getPKs(hm);
		if (pk != null && pkRedefined(pkset, pk, hm))
		{
			if (pk.length() == 0)
			{
				pk = hm.toString();
			}
			mapfile_format_warning[0] += "\r\n#" + " primary key redefined:" + " " + pk;
		}
	}

	private String getPKs(SortedMap hm)
	{
		if (primaryKeys != null)
		{
			String pks = "";
			for(int i = 0; i < primaryKeys.length; i++)
			{
				pks += "\r\n#\t";
				pks += primaryKeys[i] + "=" + hm.get(primaryKeys[i]);
			}
			return pks;
		}
		return null;
	}

	private boolean pkRedefined(HashMap pkset, String pk, SortedMap hm)
	{
		if (pkset.containsKey(pk))
		{
			return true;
		}
		pkset.put(pk, hm);
		return false;
	}

	private static boolean isContinueLine(String line)
	{
		boolean b = true;
		for(int i = line.length() - 1; i >= 0 && line.charAt(i) == '\\'; b = !b, i--);
		return !b;
	}

	/**
	 * ���������ļ��Ƿ���ڡ�
	 * �����ļ���ȡʧ��ʱ����־�б����쳣��Ϣ����־������ʱֱ���׳��쳣��
	 */
	private boolean loadConfigureMapping()
	{
		try
		{
			// ����mapping
			String mapfile_format_warning = loadMapping();
			if (mapfile_format_warning == null)
			{
				// �����ļ�û�и���
			}
			else
			{
				if (mapfile_format_warning.length() > 0)
				{
					// �����ļ����ڸ�ʽ����
					if (log != null)
					{
						log.warn("configure mapping file '"
							+ resource_mappingfile
								+ "' format warning:"
								+ mapfile_format_warning);
					}
				}
				else
				{
					// �����ļ��޸�ʽ����
				}
				if (log != null)
				{
					log.debug("load configure mapping [" + resource_mappingfile + "]:\r\n" + configure);
				}
			}
		}
		catch(FileNotFoundException fnfe)
		{
			// �����ļ�������
			if (log != null)
			{
				log.warn("Not found resource mapping file '" + resource_mappingfile + "'.");
			}
			return false;
		}
		catch(IOException e)
		{
			if (log != null)
			{
				log.warn("Failed load configure file '" + resource_mappingfile + "'.", e);
			}
			else
			{
				throw new RuntimeException(e);
			}
		}
		finally
		{
		}
		return true;
	}

	public synchronized long getConfigureStamp()
	{
		if (loadConfigureMapping())
		{
			return resource_mappingfile_mod_time;
		}
		else
		{
			return 0;
		}
	}

	public File getConfigFile()
	{
		return resource_mappingfile;
	}

	/**
	 * �����ļ�������ʱ����null.
	 * @return
	 * @throws RuntimeException setLogger(null) IOException will be throws
	 */
	public synchronized SortedMap[] getConfigure()
	{
		if (!loadConfigureMapping())
		{
			return null;
		}
		if (configure == null)
		{
			return null;
		}
		return (SortedMap[])configure.toArray(new SortedMap[0]);
	}

	public void setPrimaryKey(String[] pks)
	{
		this.primaryKeys = pks;
	}
}
