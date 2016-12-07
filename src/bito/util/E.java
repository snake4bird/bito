package bito.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import bito.json.JSON;
import bito.util.cfg.SystemConfig;
import bito.util.logger.Log;

public class E
{
	private static E o = (E)b.i.t.o();

	public static E V()
	{
		return o;
	}

	public void copyFile(File source, File target) throws IOException
	{
		copyFile(source, target, 0);
	}

	private void copyFile(File source, File target, long lastModified) throws IOException
	{
		FileInputStream fis = new FileInputStream(source);
		File targetdir = target.getParentFile();
		if (targetdir != null && !targetdir.exists())
		{
			targetdir.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(target);
		try
		{
			pipe(fis, fos);
			fos.flush();
		}
		finally
		{
			fos.close();
			fis.close();
			target.setLastModified(lastModified != 0?lastModified:source.lastModified());
		}
	}

	public void pipe(InputStream is, OutputStream os) throws IOException
	{
		int c = is.read();
		while(c >= 0)
		{
			os.write(c);
			while(is.available() > 0)
			{
				byte[] bs = new byte[is.available()];
				is.read(bs);
				os.write(bs);
			}
			c = is.read();
		}
	}

	/**
	 * 获取指定Class cls的package或其所在jar的所在路径，
	 * 结尾没有“/”
	 * @param cls
	 * @return
	 */
	public String getClassLocation(Class cls)
	{
		ClassLoader cl = cls.getClassLoader();
		if (cl == null)
		{
			cl = ClassLoader.getSystemClassLoader();
		}
		String cln = cls.getName();
		String rsn = cln.replace('.', '/') + ".class";
		URL url = cl.getResource(rsn);
		if (url == null)
		{
			rsn = cln.replace('.', '/') + ".gif";
			url = cl.getResource(rsn);
		}
		String classpath = url.getPath();
		int i_cut_start = 0;
		int i_cut_end = classpath.length() - (rsn.length());
		classpath = classpath.substring(i_cut_start, i_cut_end);
		if (classpath.indexOf(".jar!/") > 0)
		{
			if (classpath.startsWith("file:"))
			{
				i_cut_start = "file:".length();
			}
			i_cut_end = classpath.lastIndexOf("!/");
			classpath = classpath.substring(i_cut_start, i_cut_end);
		}
		try
		{
			classpath = URLDecoder.decode(classpath, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
		}
		i_cut_end = classpath.lastIndexOf("/");
		classpath = classpath.substring(0, i_cut_end);
		return classpath;
	}

	public Class getProxyClass(String proxyName, Class superClass, Class[] interfaces, Class[] overrideClass,
		Method[] overrideMethods)
	{
		return ProxyClassGenerator.getProxyClass(proxyName, superClass, interfaces, overrideClass, overrideMethods);
	}

	public byte[] readBytes(String filename) throws IOException
	{
		FileInputStream fis = new FileInputStream(filename);
		try
		{
			return readBytes(fis);
		}
		finally
		{
			fis.close();
		}
	}

	/**
	 * 不读完不返回，不会关闭InputStream
	 */
	public byte[] readBytes(InputStream is) throws IOException
	{
		int c = is.read();
		if (c == -1)
		{
			return new byte[0];
		}
		byte[] rbs = new byte[]{(byte)c};
		while(c >= 0)
		{
			int a = is.available();
			if (a > 0)
			{
				byte[] bs = new byte[rbs.length + a];
				System.arraycopy(rbs, 0, bs, 0, rbs.length);
				c = is.read(bs, rbs.length, a);
				if (c == 0)
				{
					// 没有读出数据
				}
				else if (c > 0 && c < a)
				{
					// 一次读不出所有数据
					byte[] nbs = new byte[rbs.length + c];
					System.arraycopy(bs, 0, nbs, 0, nbs.length);
					rbs = nbs;
				}
				else
				{
					// 读出了所有available数据
					rbs = bs;
				}
			}
			else
			{
				c = is.read();
				if (c >= 0)
				{
					byte[] bs = new byte[rbs.length + 1];
					System.arraycopy(rbs, 0, bs, 0, rbs.length);
					bs[bs.length - 1] = (byte)c;
					rbs = bs;
				}
			}
		}
		return rbs;
	}

	private long stamp = System.currentTimeMillis() * 1000;
	private long boot_nano = System.nanoTime();

	public synchronized long getStamp()
	{
		long t = System.currentTimeMillis() * 1000 + (Math.abs(System.nanoTime() - boot_nano) % 1000000) / 1000;
		if (t > stamp)
		{
			stamp = t;
		}
		else
		{
			stamp++;
		}
		return stamp;
	}

	private static final String[] defaultdtstyle = new String[]{"yyyy-MM-dd HH:mm:ss.SSS",
																"yyyy-MM-dd HH:mm:ss",
																"yyyy-MM-dd HH:mm",
																"yyyy-MM-dd HH",
																"yyyy-MM-dd",
																"yyyy-MM",
																"HH:mm:ss.SSS",
																"HH:mm:ss",
																"mm:ss",};

	public java.util.Date parseDateTime(String dtstring)
	{
		return parseDateTime(dtstring, null);
	}

	public java.util.Date parseDateTime(String dtstring, String[] dtstyle) throws RuntimeException
	{
		if (dtstring.matches("\\d+"))
		{
			long t = Long.parseLong(dtstring);
			return new java.util.Date(t);
		}
		if (dtstyle == null || dtstyle.length == 0)
		{
			dtstyle = defaultdtstyle;
		}
		for(int i = 0; i < dtstyle.length; i++)
		{
			try
			{
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(dtstyle[i]);
				return sdf.parse(dtstring);
			}
			catch(java.text.ParseException e)
			{
			}
		}
		throw new RuntimeException("Unparseable datetime: \"" + dtstring + "\"");
	}

	public void config()
	{
	}

	public static EscapeSequence es = new EscapeSequence();

	public int run(String[] command, String[] env, String workdir, OutputStream out, OutputStream err)
	{
		int reti = 0;
		try
		{
			{
				Process p = Runtime.getRuntime().exec(command, env, workdir == null?null:new File(workdir));
				boolean isrunning = true;
				while(isrunning)
				{
					try
					{
						InputStream is = p.getInputStream();
						if (is.available() > 0)
						{
							byte[] bs = new byte[is.available()];
							is.read(bs);
							out.write(bs);
						}
						InputStream es = p.getErrorStream();
						if (es.available() > 0)
						{
							byte[] bs = new byte[es.available()];
							es.read(bs);
							err.write(bs);
						}
						reti = p.exitValue();
						out.flush();
						err.flush();
						isrunning = false;
					}
					catch(IllegalThreadStateException itse)
					{
						try
						{
							Thread.sleep(1);
						}
						catch(InterruptedException e)
						{
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(new PrintStream(err));
		}
		return reti;
	}

	private Map logcache = new HashMap();

	public Log log(String id)
	{
		Log log = (Log)logcache.get(id);
		if (log == null)
		{
			log = new Log(id);
			logcache.put(id, log);
		}
		return log;
	}

	public Comparator getCommonComparator()
	{
		return CommonComparator.comparator;
	}

	public SortedMap newMapSortedByAddTime()
	{
		return new MapSortedByAddTime();
	}

	public String toString(Object o)
	{
		return transO2A(o).toString();
	}

	protected Map transNO2Map(Map no)
	{
		Map m = newMapSortedByAddTime();
		Iterator noesi = no.entrySet().iterator();
		while(noesi.hasNext())
		{
			Map.Entry me = (Map.Entry)noesi.next();
			Object k = me.getKey();
			Object v = me.getValue();
			m.put(transO2A(k), transO2A(v));
		}
		return m;
	}

	private List transNA2List(Object[] na)
	{
		List l = new ArrayList();
		for(int i = 0; i < na.length; i++)
		{
			Object o = na[i];
			l.add(transO2A(o));
		}
		return l;
	}

	private Object transO2A(Object jo)
	{
		if (jo == null)
		{
			return "<NULL>";
		}
		else if (jo instanceof Map)
		{
			return transNO2Map((Map)jo);
		}
		else if (jo instanceof Object[])
		{
			return transNA2List((Object[])jo);
		}
		else if (jo instanceof Collection)
		{
			return transNA2List(((Collection)jo).toArray());
		}
		return jo;
	}

	private String inputPassword() throws IOException, InterruptedException
	{
		System.out.println("Password:");
		while(System.in.available() == 0)
		{
			Thread.sleep(1);
		}
		byte[] bs = new byte[System.in.available()];
		System.in.read(bs);
		return new String(bs).replaceAll("[\r\n]", "");
	}

	private void decryptFile(String srcfile, String desfile, String key) throws Exception
	{
		if (key == null)
		{
			key = inputPassword();
			if (key.length() == 0)
			{
				key = null;
			}
		}
		byte[] bs = readBytes(srcfile);
		bs = GIFEncrypt.decode(bs);
		bs = key == null?bs:DESX.Decrypt(bs, key.getBytes());
		FileOutputStream fos = new FileOutputStream(desfile);
		fos.write(bs);
		fos.close();
	}

	private void encryptFile(String srcfile, String desfile, String key, String comments) throws Exception
	{
		if (key == null)
		{
			key = inputPassword();
			if (key.length() == 0)
			{
				key = null;
			}
		}
		byte[] bs = readBytes(srcfile);
		byte[] zbs = key == null?bs:DESX.Encrypt(bs, key.getBytes());
		String s;
		if (comments == null)
		{
			s = "";
		}
		else if (comments.length() == 0)
		{
			if (key.trim().length() > 0)
			{
				s = new String(zbs, 0, zbs.length > 1000?1000:zbs.length);
			}
			else
			{
				s = new String(bs, 0, bs.length > 1000?1000:bs.length);
			}
		}
		else
		{
			s = comments;
		}
		bs = GIFEncrypt.encode(s, zbs, key == null?1:2);
		FileOutputStream fos = new FileOutputStream(desfile);
		fos.write(bs);
		fos.close();
	}

	private void outputfile(String filename, long maxsize, int maxback, boolean stdout) throws IOException
	{
		String input_encoding = System.getProperty("input.encoding");
		String output_encoding = System.getProperty("output.encoding");
		FileAppender fa = null;
		if (filename != null && filename.length() > 0)
		{
			fa = new FileAppender(filename, maxsize, maxback, output_encoding);
		}
		try
		{
			int c;
			while((c = System.in.read()) != -1)
			{
				byte[] bs = new byte[System.in.available() + 1];
				bs[0] = (byte)c;
				System.in.read(bs, 1, bs.length - 1);
				String s = input_encoding == null?new String(bs):new String(bs, input_encoding);
				if (fa != null)
				{
					fa.appendfile(s, null);
				}
				if (stdout)
				{
					try
					{
						System.out.print(s);
					}
					catch(Throwable e)
					{
					}
				}
			}
		}
		catch(Throwable e)
		{
			fa.appendfile("Failure:", e);
			fa.appendfile("Available charsets: " + Charset.availableCharsets().keySet().toString(), null);
		}
	}

	private Map<String, Long> readfilestamp = new HashMap();
	private Map<String, String> readfilecache = new HashMap();
	private SortedMap<String, Long> readfilecachestamp = newMapSortedByAddTime();

	public String readfile(File file) throws IOException
	{
		String fn = file.getCanonicalPath();
		synchronized(readfilecachestamp)
		{
			readfilecachestamp.remove(fn);
			readfilecachestamp.put(fn, System.currentTimeMillis());
			int count = readfilecachestamp.size();
			while(count > 0)
			{
				String key = readfilecachestamp.firstKey();
				Long cachestamp = readfilecachestamp.get(key);
				if (cachestamp == null
					|| System.currentTimeMillis() > cachestamp.longValue()
						+ 1000L * SystemConfig.getLong("read.file.cache.seconds", 1800)
						|| count > SystemConfig.getInt("read.file.cache.count", 20))
				{
					//清除超过1小时未访问的cache文件
					readfilestamp.remove(key);
					readfilecache.remove(key);
					readfilecachestamp.remove(key);
					count--;
				}
				else
				{
					break;
				}
			}
		}
		Long fstamp;
		String fcache;
		synchronized(readfilecachestamp)
		{
			fstamp = readfilestamp.get(fn);
			fcache = readfilecache.get(fn);
		}
		if (fstamp != null
			&& fstamp.longValue() == file.lastModified()
				&& fcache != null
				&& file.length() == fcache.length())
		{
			return fcache;
		}
		synchronized(readfilecachestamp)
		{
			readfilestamp.put(fn, file.lastModified());
		}
		String s;
		FileInputStream fis = new FileInputStream(file);
		byte[] bs;
		try
		{
			bs = readBytes(fis);
		}
		finally
		{
			fis.close();
		}
		String cs = checkTextFileCharset(bs);
		try
		{
			if ("UTF-8".equals(cs))
			{
				s = new String(bs, 3, bs.length - 3, cs);
			}
			else if ("UTF-16BE".equals(cs) || "UTF-16LE".equals(cs))
			{
				s = new String(bs, 2, bs.length - 2, cs);
			}
			else if ("UTF-32BE".equals(cs) || "UTF-32LE".equals(cs))
			{
				s = new String(bs, 4, bs.length - 4, cs);
			}
			else if (cs != null)
			{
				s = new String(bs, cs);
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
		synchronized(readfilecachestamp)
		{
			readfilecache.put(fn, s);
		}
		return s;
	}

	public String checkTextFileCharset(String filename)
	{
		try
		{
			FileInputStream fis = new FileInputStream(filename);
			try
			{
				byte[] bs = new byte[fis.available() > 4?4:fis.available()];
				fis.read(bs);
				return checkTextFileCharset(bs);
			}
			finally
			{
				fis.close();
			}
		}
		catch(IOException e)
		{
		}
		return null;
	}

	public String checkTextFileCharset(byte[] bs)
	{
		if (bs == null || bs.length < 2)
		{
			return null;
		}
		if (bs[0] == (byte)0xFE && bs[1] == (byte)0xFF)
		{
			return "UTF-16BE";
		}
		else if (bs[0] == (byte)0xFF && bs[1] == (byte)0xFE)
		{
			return "UTF-16LE";
		}
		else if (bs.length >= 3 && (bs[0] == (byte)0xEF && bs[1] == (byte)0xBB && bs[2] == (byte)0xBF))
		{
			return "UTF-8";
		}
		else if (bs.length >= 4
			&& (bs[0] == (byte)0x00 && bs[1] == (byte)0x00 && bs[2] == (byte)0xFE && bs[3] == (byte)0xFF))
		{
			return "UTF-32BE";
		}
		else if (bs.length >= 4
			&& (bs[0] == (byte)0xFF && bs[1] == (byte)0xFE && bs[2] == (byte)0x00 && bs[3] == (byte)0x00))
		{
			return "UTF-32BE";
		}
		return null;
	}

	private Map<String, Long> writefilestamp = new HashMap();
	private Map<String, String> writefilecache = new HashMap();
	private SortedMap<String, Long> writefilecachestamp = newMapSortedByAddTime();

	public void writefile(File file, String content) throws IOException
	{
		String fn = file.getCanonicalPath();
		if (content == null)
		{
			if (file.exists())
			{
				file.delete();
			}
			return;
		}
		file = new File(fn);
		if (!file.getParentFile().exists())
		{
			file.getParentFile().mkdirs();
		}
		Long fstamp;
		String fcache;
		synchronized(writefilecachestamp)
		{
			fstamp = writefilestamp.get(fn);
			fcache = writefilecache.get(fn);
		}
		if (file.exists()
			&& fstamp != null
				&& fstamp.longValue() == file.lastModified()
				&& fcache != null
				&& file.length() == fcache.length()
				&& content.equals(fcache))
		{
			return;
		}
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(content.getBytes());
		fos.close();
		synchronized(writefilecachestamp)
		{
			writefilestamp.put(fn, file.lastModified());
			writefilecache.put(fn, content);
			writefilecachestamp.remove(fn);
			writefilecachestamp.put(fn, System.currentTimeMillis());
			int count = writefilecachestamp.size();
			while(count > 0)
			{
				String key = writefilecachestamp.firstKey();
				Long cachestamp = writefilecachestamp.get(key);
				if (cachestamp == null
					|| System.currentTimeMillis() > cachestamp.longValue()
						+ 1000L * SystemConfig.getLong("write.file.cache.seconds", 1800)
						|| count > SystemConfig.getInt("write.file.cache.count", 20))
				{
					//清除超过1小时未访问的cache文件
					writefilestamp.remove(key);
					writefilecache.remove(key);
					writefilecachestamp.remove(key);
					count--;
				}
				else
				{
					break;
				}
			}
		}
	}

	private DocumentBuilderFactory dbf;

	public Properties loadXMLConfig(InputStream bytestream) throws Exception
	{
		Properties p = new Properties();
		if (dbf == null)
		{
			dbf = DocumentBuilderFactory.newInstance();
		}
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource isrc = new InputSource(bytestream);
		Document doc = db.parse(isrc);
		NodeList nl = doc.getElementsByTagName("entry");
		for(int ni = 0; ni < nl.getLength(); ni++)
		{
			Node n = nl.item(ni);
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				Element e = (Element)n;
				String key = e.getAttribute("key");
				NodeList cns = e.getChildNodes();
				String value = null;
				for(int cni = 0; cni < cns.getLength(); cni++)
				{
					String nv = cns.item(cni).getNodeValue();
					if (nv != null)
					{
						if (value == null)
						{
							value = nv;
						}
						else
						{
							value += nv;
						}
					}
				}
				if (value != null)
				{
					p.setProperty(key, value);
				}
			}
		}
		return p;
	}

	public String objectString(Object o)
	{
		if (o == null)
		{
			return "<null>";
		}
		if (o instanceof String)
		{
			return (String)o;
		}
		HashSet objstack = new HashSet();
		return objectString("", objstack, o) + "\r\n";
	}

	private String objectString(String indent, HashSet objstack, Object o)
	{
		String nextindent = indent + "  ";
		String s = indent + o.toString() + ": \r\n";
		objstack.add(o);
		int n = 0;
		Method[] mtds = o.getClass().getMethods();
		for(int i = 0; i < mtds.length; i++)
		{
			String getname = mtds[i].getName();
			if (getname.startsWith("get"))
			{
				String name = getname.substring(3);
				if (name.length() > 0)
				{
					Class[] clspts = mtds[i].getParameterTypes();
					if (clspts.length == 0)
					{
						if (n > 0)
						{
							s += ",\r\n";
						}
						n++;
						s += nextindent + getname + "()=";
						Object ro = null;
						try
						{
							ro = mtds[i].invoke(o, new Object[0]);
						}
						catch(Exception e)
						{
						}
						if (ro == null)
						{
							s += "<null>";
						}
						else if (objstack.contains(ro))
						{
							s += "<circle:" + ro.toString() + ">";
						}
						else
						{
							Class retcls = ro.getClass();
							if (retcls.isArray())
							{
								s += "[]";
							}
							else if (retcls.isPrimitive() || retcls.getName().startsWith("java.lang."))
							{
								s += ro;
							}
							else
							{
								s += "{\r\n"
									+ objectString(nextindent + "  ", objstack, ro)
										+ "\r\n"
										+ nextindent
										+ "}";
							}
						}
					}
				}
			}
		}
		return s;
	}

	public String toJSONString(Object obj)
	{
		return JSON.toString(obj);
	}

	public Object parseJSONString(String json)
	{
		return JSON.parse(json);
	}

	public String MD5(String s)
	{
		return DESX.MD5(s);
	}

	public String DESxEncrypt(String data, String key) throws Exception
	{
		return DESX.Encrypt(data, key);
	}

	public byte[] DESxEncrypt(byte[] data, byte[] key) throws Exception
	{
		return DESX.Encrypt(data, key);
	}

	public String DESxDecrypt(String data, String key) throws Exception
	{
		return DESX.Decrypt(data, key);
	}

	public byte[] DESxDecrypt(byte[] data, byte[] key) throws Exception
	{
		return DESX.Decrypt(data, key);
	}

	public URLReader newURLReader()
	{
		return new URLReader();
	}

	public String[] match(String input, String regex)
	{
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		ArrayList al = new ArrayList();
		if (m.matches())
		{
			for(int i = 1; i <= m.groupCount(); i++)
			{
				String s = m.group(i);
				al.add(s);
			}
		}
		return (String[])al.toArray(new String[al.size()]);
	}
}
