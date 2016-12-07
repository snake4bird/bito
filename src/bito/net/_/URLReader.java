package bito.net._;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

/**
 * <br>
 * Created on 2004-8-30 <br>
 * basic authentication supported on 2007-1-4 <br>
 * https supported on 2009-8-27 <br>
 * range get supported on 2011-3-7 <br>
 * https auto certificate authentication supported on 2011-8-30 <br>
 * fixed class location bug in SimpleSSLSocketFactory 2012-1-12 <br>
 * add parameter "tempfile_prefix" special prefix for temp files 2012-4-27 <br>
 * combine the class SimpleSSLSocketFactory in one 2012-4-27 <br>
 * add field that default read stream timeout 2013-2-21 <br>
 * hide implements in deva 2014-7-17 <br>
 * add debug output control 2016-8-10 <br>
 * 
 * @author bird
 */
public class URLReader extends bito.net.URLReader
{
	protected int DefaultReadStreamTimeout = 3000;
	protected String tempfile_prefix = b.i.t + "/";
	protected HashSet debug_setting = new HashSet(Arrays.asList("Process,RequestHeader,ResponseHeader".split(",")));
	protected PrintStream dbgout = null;
	protected long ab_dbgtime = 0;
	protected String ab_url = null;
	protected URL ab_lasturl = null;
	protected String ab_cookie = null;
	protected String ab_basicAuth = null;
	protected String ab_proxybasicAuth = null;
	protected HttpURLConnection ab_huc = null;
	protected InputStream ab_is = null;
	protected String ab_contentrange = null;
	protected String ab_contentlength = null;
	protected String ab_contenttype = null;
	protected String ab_lastmodified = null;
	protected byte[] ab_retbytes = new byte[0];
	protected Map ab_responseheaderfields = null;
	protected int ab_recurcount = 0;
	private boolean cancel = false;
	private Properties request_properties = new Properties();

	public URLReader()
	{
		this(null);
	}

	public URLReader(String tempfile_prefix)
	{
		if (tempfile_prefix != null)
		{
			this.tempfile_prefix = tempfile_prefix;
		}
	}

	public byte[] ReadURL(String url) throws Exception
	{
		return ReadURL(url, null);
	}

	public byte[] ReadURL(String url, byte[] post) throws Exception
	{
		return ReadURL(url, null, null, post);
	}

	public byte[] ReadURL(String url, String user, String password, byte[] post) throws Exception
	{
		return ReadURL(url,
			user,
			password,
			null,
			null,
			null,
			null,
			post,
			ab_lasturl == null?null:ab_lasturl.getPath(),
			-1,
			-1,
			-1);
	}

	public byte[] ReadURL(String url, byte[] post, String ref) throws Exception
	{
		return ReadURL(url, null, null, null, null, null, null, post, ref, -1, -1, -1);
	}

	/**
	 * 不会返回null，可能返回0长度byte[]
	 * 
	 * @param url
	 * @param post
	 * @param ref
	 * @return
	 * @throws Exception
	 */
	public byte[] ReadURL(String url, String user, String password, String proxyhost, String proxyport,
		String proxyuser, String proxypswd, byte[] post, String ref, long rstart, long rend, int timeout)
		throws Exception
	{
		return ReadURL(url,
			null,
			user,
			password,
			proxyhost,
			proxyport,
			proxyuser,
			proxypswd,
			null,
			post,
			ref,
			rstart,
			rend,
			timeout);
	}

	/**
	 * 不会返回null，可能返回0长度byte[]
	 * 
	 * @param url
	 * @param post
	 * @param ref
	 * @return
	 * @throws Exception
	 */
	public byte[] ReadURL(String url, String method, String user, String password, String proxyhost, String proxyport,
		String proxyuser, String proxypswd, Map header, byte[] post, String ref, long rstart, long rend, int timeout)
		throws Exception
	{
		ab_url = url;
		readURLData(url,
			method,
			user,
			password,
			proxyhost,
			proxyport,
			proxyuser,
			proxypswd,
			header,
			post,
			ref,
			rstart,
			rend,
			timeout);
		return ab_retbytes;
	}

	protected void readURLInputStream(int timeout) throws Exception
	{
		long size = -1;
		try
		{
			size = Long.parseLong(ab_contentlength);
		}
		catch(NumberFormatException nfe)
		{
		}
		if (dbgout != null && debug_setting.contains("Process"))
		{
			dbgout.println((System.currentTimeMillis() - ab_dbgtime)
				+ "ms  --  read stream "
					+ (size)
					+ " bytes in "
					+ timeout
					+ "ms from "
					+ ab_url);
		}
		ab_retbytes = readStream(ab_is, size, timeout);
		if (dbgout != null && debug_setting.contains("Process"))
		{
			dbgout.println((System.currentTimeMillis() - ab_dbgtime)
				+ "ms  --  received "
					+ (ab_retbytes.length)
					+ " bytes from "
					+ ab_url);
		}
	}

	public byte[] getDataBytes()
	{
		return ab_retbytes;
	}

	protected synchronized void readURLData(String url, String method, String user, String password, String proxyhost,
		String proxyport, String proxyuser, String proxypswd, Map requestProperties, byte[] post, String ref,
		long rstart, long rend, int timeout) throws Exception
	{
		ab_huc = null;
		ab_is = null;
		ab_retbytes = new byte[0];
		//prepare
		if (dbgout != null && debug_setting.contains("Process"))
		{
			ab_dbgtime = System.currentTimeMillis();
			dbgout.println("0ms  --  Open " + url);
		}
		if (url == null || url.length() == 0)
		{
			throw new Exception("Parameter Error. URL not special.");
		}
		if (cancel)
		{
			throw new Exception("Canceled.");
		}
		// open
		if (proxyhost != null && proxyport != null)
		{
			Properties prop = System.getProperties();
			prop.put("http.proxyHost", proxyhost);
			prop.put("http.proxyPort", proxyport);
			prop.put("https.proxyHost", proxyhost);
			prop.put("https.proxyPort", proxyport);
		}
		URL u = (ab_lasturl == null)?new URL(url):new URL(ab_lasturl, url);
		ab_lasturl = u;
		if (cancel)
		{
			throw new Exception("Canceled.");
		}
		ab_huc = (HttpURLConnection)u.openConnection();
		if (dbgout != null && debug_setting.contains("Process"))
		{
			dbgout.println((System.currentTimeMillis() - ab_dbgtime) + "ms  --  Got connection " + url);
		}
		if (cancel)
		{
			throw new Exception("Canceled.");
		}
		OutputStream os = null;
		InputStream is = null;
		try
		{
			ab_recurcount++;
			if (ab_huc instanceof HttpsURLConnection)
			{
				httpsPrepare((HttpsURLConnection)ab_huc);
			}
			if (cancel)
			{
				throw new Exception("Canceled.");
			}
			setRequestProperty(ab_huc,
				user,
				password,
				proxyhost,
				proxyport,
				proxyuser,
				proxypswd,
				requestProperties,
				post,
				ref,
				rstart,
				rend);
			// post
			if (cancel)
			{
				throw new Exception("Canceled.");
			}
			String rmethod = method;
			if (post != null)
			{
				if (rmethod == null)
				{
					rmethod = "POST";
				}
				ab_huc.setRequestMethod(rmethod);
				ab_huc.setDoOutput(true);
				os = ab_huc.getOutputStream();
				os.write(post);
				os.close();
				os = null;
				if (dbgout != null && debug_setting.contains("Process"))
				{
					dbgout.println(
						(System.currentTimeMillis() - ab_dbgtime) + "ms  --  Post data " + post.length + " bytes");
				}
			}
			else
			{
				if (rmethod == null)
				{
					rmethod = "GET";
				}
				ab_huc.setRequestMethod(rmethod);
			}
			// read
			if (dbgout != null && debug_setting.contains("Process"))
			{
				dbgout.println(
					(System.currentTimeMillis() - ab_dbgtime) + "ms  --  Request ready for " + rmethod + " " + url);
			}
			if (dbgout != null && debug_setting.contains("RequestHeader"))
			{
				dbgout.println("request:\r\n" + ab_huc.getRequestProperties());
			}
			if (cancel)
			{
				throw new Exception("Canceled.");
			}
			is = ab_huc.getInputStream();
			if (cancel)
			{
				throw new Exception("Canceled.");
			}
			ab_responseheaderfields = ab_huc.getHeaderFields();
			if (dbgout != null && debug_setting.contains("Process"))
			{
				dbgout.println((System.currentTimeMillis() - ab_dbgtime) + "ms --  Got response " + url);
			}
			if (dbgout != null && debug_setting.contains("ResponseHeader"))
			{
				dbgout.println("response:\r\n" + ab_responseheaderfields);
			}
			String cookie = ab_huc.getHeaderField("Set-Cookie");
			if (cookie != null)
			{
				ab_cookie = cookie;
			}
			String last_modified = ab_huc.getHeaderField("Last-Modified");
			if (last_modified != null)
			{
				ab_lastmodified = last_modified;
			}
			ab_contentrange = ab_huc.getHeaderField("Content-Range");
			String content_lenth = ab_huc.getHeaderField("Content-Length");
			if (content_lenth != null)
			{
				ab_contentlength = content_lenth;
			}
			String content_type = ab_huc.getHeaderField("Content-Type");
			if (content_type != null)
			{
				ab_contenttype = content_type;
			}
			if (cancel)
			{
				throw new Exception("Canceled.");
			}
			int rcode = ab_huc.getResponseCode();
			if (rcode >= 300)
			{
				String Redirect = ab_huc.getHeaderField("Location"); // 302
				if (Redirect != null && Redirect.length() > 0)
				{
					if (ab_recurcount > 50)
					{
						throw new Exception("跳转次数太多了，怀疑服务器端有程序错误，强制停止！");
					}
					is.close();
					is = null;
					readURLData(Redirect,
						method,
						user,
						password,
						proxyhost,
						proxyport,
						proxyuser,
						proxypswd,
						null,
						null,
						url,
						rstart,
						rend,
						timeout);
				}
				else if (rcode == 500)
				{
					throw new Exception("服务器端有程序错误！");
				}
				else
				{
					throw new Exception("[" + rcode + "]" + ab_huc.getResponseMessage());
				}
			}
			else
			{
				ab_is = is;
				readURLInputStream(timeout);
				ab_is.close();
				ab_is = null;
			}
		}
		finally
		{
			if (os != null)
			{
				os.close();
			}
			if (is != null)
			{
				is.close();
			}
			// 不要断开物理连接 ab_huc.disconnect();
			if (ab_huc != null)
			{
				InputStream es = ab_huc.getErrorStream();
				if (es != null)
				{
					es.close();
				}
			}
			ab_recurcount--;
		}
	}

	protected void setRequestProperty(HttpURLConnection huc, String user, String password, String proxyhost,
		String proxyport, String proxyuser, String proxypswd, Map temp_requestProperties, byte[] post, String ref,
		long rstart, long rend)
	{
		ab_huc.setDoInput(true);
		ab_huc.setAllowUserInteraction(true);
		ab_huc.setInstanceFollowRedirects(false);
		ab_huc.setUseCaches(false);
		ab_huc.setRequestProperty("User-Agent", "ab");
		ab_huc.setRequestProperty("Accept", "*/*");
		ab_huc.setRequestProperty("Connection", "Keep-Alive");
		if (rstart >= 0)
		{
			ab_huc.setRequestProperty("RANGE", "bytes=" + rstart + (rend > 0?("-" + rend):"-"));
		}
		if (ab_cookie != null)
		{
			ab_huc.setRequestProperty("Cookie", ab_cookie);
		}
		if (ref != null)
		{
			ab_huc.setRequestProperty("Referer", ref);
		}
		if (user != null && password != null)
		{
			ab_basicAuth = "Basic " + base64((user + ":" + password).getBytes());
			ab_huc.setRequestProperty("Authorization", ab_basicAuth);
		}
		if (proxyuser != null && proxypswd != null)
		{
			ab_proxybasicAuth = "Basic " + base64((proxyuser + ":" + proxypswd).getBytes());
			ab_huc.setRequestProperty("Proxy-Authorization", ab_proxybasicAuth);
		}
		{
			Iterator mesi = request_properties.entrySet().iterator();
			while(mesi.hasNext())
			{
				Entry me = (Entry)mesi.next();
				huc.setRequestProperty((String)me.getKey(), (String)me.getValue());
			}
		}
		if (temp_requestProperties != null)
		{
			Iterator mesi = temp_requestProperties.entrySet().iterator();
			while(mesi.hasNext())
			{
				Entry me = (Entry)mesi.next();
				huc.setRequestProperty((String)me.getKey(), (String)me.getValue());
			}
		}
	}

	protected void httpsPrepare(HttpsURLConnection huc) throws Exception
	{
		URL url = huc.getURL();
		int port = url.getPort();
		if (port == -1)
		{
			port = 443;
		}
		SSLSocketFactory simpleSSLSocketFactory = SSLSF.getSSLSocketFactory(tempfile_prefix, url.getHost(), port);
		huc.setSSLSocketFactory(simpleSSLSocketFactory);
		huc.setHostnameVerifier(new HostnameVerifier()
		{
			public boolean verify(String host, SSLSession ssls)
			{
				return true;
			}
		});
	}

	public Map getResponseHeaderFields()
	{
		return ab_responseheaderfields;
	}

	public String getCookie()
	{
		return ab_cookie;
	}

	public String getContentType()
	{
		return ab_contenttype;
	}

	public void setCookie(String cookie)
	{
		ab_cookie = cookie;
	}

	public void cancel()
	{
		cancel = true;
		if (ab_huc != null)
		{
			ab_huc.disconnect();
		}
	}

	public void reset()
	{
		cancel = false;
	}

	public void setDefaultReadStreamTimeout(int i)
	{
		DefaultReadStreamTimeout = i;
	}

	/**
	* 获取指定Class cls的package或其所在jar的所在路径，
	* @param cls
	* @return
	*/
	private static String getClassLocation(Class cls)
	{
		URL url = cls.getClassLoader().getResource(cls.getName().replace('.', '/') + ".class");
		String classpath = url.getPath();
		int nl = classpath.length() - (cls.getName().length() + ".class".length());
		int i_cut_start = 0;
		int i_cut_end = nl;
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
			i_cut_end = classpath.lastIndexOf("/");
			classpath = classpath.substring(0, i_cut_end);
		}
		catch(UnsupportedEncodingException e)
		{
		}
		return classpath;
	}

	private byte[] readStream(InputStream is, long size, int timeout) throws Exception
	{
		if (timeout <= 0)
		{
			timeout = DefaultReadStreamTimeout;
		}
		byte[] ret = new byte[0];
		int xn = 0;
		long t = System.currentTimeMillis();
		while(t > 0)
		{
			while((xn = is.available()) > 0 || size < 0)
			{
				if (xn <= 0 && size < 0)
				{
					xn = 1024;
				}
				byte[] b = new byte[xn];
				int i = is.read(b);
				if (i < 0)
				{
					break;
				}
				byte[] tbs = new byte[ret.length + i];
				System.arraycopy(ret, 0, tbs, 0, ret.length);
				System.arraycopy(b, 0, tbs, ret.length, i);
				ret = tbs;
				Thread.sleep(1);
				t = System.currentTimeMillis();
			}
			if ((size > 0 && ret.length < size) && (timeout > 0 && System.currentTimeMillis() - t < timeout))
			{
				Thread.sleep(10);
			}
			else
			{
				t = 0;
			}
		}
		return ret;
	}

	/**
	 * Creates the Base64 value.
	 */
	private static String base64(byte[] value)
	{
		StringBuffer cb = new StringBuffer();
		int i = 0;
		for(i = 0; i + 2 < value.length; i += 3)
		{
			long chunk = value[i];
			chunk = (chunk << 8) | (255 & value[i + 1]);
			chunk = (chunk << 8) | (255 & value[i + 2]);
			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append(encode(chunk >> 6));
			cb.append(encode(chunk));
		}
		if (i + 1 < value.length)
		{
			long chunk = value[i];
			chunk = (chunk << 8) | (255 & value[i + 1]);
			chunk <<= 8;
			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append(encode(chunk >> 6));
			cb.append('=');
		}
		else if (i < value.length)
		{
			long chunk = value[i];
			chunk <<= 16;
			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append('=');
			cb.append('=');
		}
		return cb.toString();
	}

	private static char encode(long d)
	{
		d &= 0x3f;
		if (d < 26)
			return (char)(d + 'A');
		else if (d < 52)
			return (char)(d + 'a' - 26);
		else if (d < 62)
			return (char)(d + '0' - 52);
		else if (d == 62)
			return '+';
		else
			return '/';
	}

	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("URLReader \"http://server:port/url?query\" [/D] [< \"post data file\"]");
			return;
		}
		try
		{
			URLReader r = new URLReader();
			byte[] bs = r.readStream(System.in, -1, -1);
			String url = args[0];
			if (args.length > 1 && "/D".equals(args[1]))
			{
				r.dbgout = System.out;
				System.out.println("ISO-8859-1:" + new String(bs, "ISO-8859-1"));
				System.out.println("UTF-8:" + new String(bs, "UTF-8"));
				System.out.println("GBK:" + new String(bs, "GBK"));
			}
			bs = r.ReadURL(url,
				null,
				null,
				"proxy.stats.gov.cn",
				"9010",
				null,
				null,
				bs == null || bs.length == 0?null:bs,
				null,
				0,
				100,
				-1);
			System.out.println(new String(bs));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static class SSLSF
	{
		public static boolean debug = false;
		private static final HashMap keystore_checked_map = new HashMap();
		private static final char[] passphrase = "changeit".toCharArray();

		//
		public static SSLSocketFactory getSSLSocketFactory(String tempfile_prefix, String host, int port)
			throws Exception
		{
			//取得文件名
			File f = new File(tempfile_prefix + "jssecacerts");
			String HTTPS_KEYSTORE_FILE = f.getAbsolutePath();
			return getSSLSocketFactory(HTTPS_KEYSTORE_FILE, host, port, true);
		}

		private static SSLSocketFactory getSSLSocketFactory(String HTTPS_KEYSTORE_FILE, String host, int port,
			boolean retry) throws Exception
		{
			KeyStore ks = loadDefaultKeyStore(HTTPS_KEYSTORE_FILE);
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
			STM savingTrustManager = new STM(defaultTrustManager);
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[]{savingTrustManager}, null);
			SSLSocketFactory factory = context.getSocketFactory();
			if (keystore_checked_map.containsKey(HTTPS_KEYSTORE_FILE + ":" + host + ":" + port))
			{
				return factory;
			}
			if (check_keystore(factory, host, port))
			{
				keystore_checked_map.put(HTTPS_KEYSTORE_FILE + ":" + host + ":" + port, null);
				return factory;
			}
			X509Certificate[] chain = savingTrustManager.chain;
			if (chain == null)
			{
				throw new Exception("Could not obtain server certificate chain");
			}
			for(int i = 0; i < chain.length; i++)
			{
				X509Certificate cert = chain[i];
				String alias = host + "-" + (i + 1);
				ks.setCertificateEntry(alias, cert);
				OutputStream out = new FileOutputStream(HTTPS_KEYSTORE_FILE);
				ks.store(out, passphrase);
				out.close();
				if (debug)
				{
					System.out.println(" " + (i + 1) + " Subject " + cert.getSubjectDN());
					System.out.println("   Issuer  " + cert.getIssuerDN());
					System.out.println();
					System.out.println(
						"Added certificate to keystore '" + HTTPS_KEYSTORE_FILE + "' using alias '" + alias + "'");
				}
			}
			if (retry)
			{
				return getSSLSocketFactory(HTTPS_KEYSTORE_FILE, host, port, false);
			}
			return factory;
		}

		private static boolean check_keystore(SSLSocketFactory factory, String host, int port) throws IOException
		{
			if (debug)
			{
				System.out.println("Opening connection to " + host + ":" + port + "...");
			}
			SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
			socket.setSoTimeout(10000);
			try
			{
				if (debug)
				{
					System.out.println("Starting SSL handshake...");
				}
				socket.startHandshake();
				socket.close();
				if (debug)
				{
					System.out.println("No errors, certificate is already trusted");
				}
				return true;
			}
			catch(SSLException e)
			{
				if (debug)
				{
					System.out.println(e.getMessage());
				}
			}
			return false;
		}

		private static KeyStore loadDefaultKeyStore(String HTTPS_KEYSTORE_FILE)
			throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
		{
			File file = new File(HTTPS_KEYSTORE_FILE);
			if (file.isFile() == false)
			{
				char SEP = File.separatorChar;
				File dir = new File(System.getProperty("java.home") + SEP + "lib" + SEP + "security");
				if (!dir.exists())
				{
					dir = new File(System.getProperty("java.home") + SEP + "jre" + SEP + "lib" + SEP + "security");
				}
				file = new File(dir, "jssecacerts");
				if (file.isFile() == false)
				{
					file = new File(dir, "cacerts");
				}
			}
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream in = new FileInputStream(file);
			try
			{
				ks.load(in, passphrase);
			}
			finally
			{
				in.close();
			}
			return ks;
		}

		private static class STM implements X509TrustManager
		{
			private final X509TrustManager tm;
			private X509Certificate[] chain;

			STM(X509TrustManager tm)
			{
				this.tm = tm;
			}

			public X509Certificate[] getAcceptedIssuers()
			{
				return tm.getAcceptedIssuers();
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
			{
				tm.checkClientTrusted(chain, authType);
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
			{
				this.chain = chain;
				tm.checkServerTrusted(chain, authType);
			}
		}
		/**
		private static final String _;
		static
		{
			try
			{
				_ = Class.forName("_", true, new ClassLoader()
				{
					protected synchronized Class loadClass(String name,
						boolean resolve) throws ClassNotFoundException
					{
						Class c;
						if (name.equals("_"))
						{
							try
							{
								byte[] cbs = new byte[]{};
								byte[] bs = new byte[0];
								Inflater inflater = new Inflater();
								inflater.setInput(cbs);
								inflater.inflate(bs);
								inflater.end();
								c = defineClass(null, bs, 0, bs.length);
							}
							catch(Throwable e)
							{
								throw new ClassNotFoundException(e.getMessage());
							}
						}
						else
						{
							c = super.loadClass(name);
						}
						if (resolve)
						{
							resolveClass(c);
						}
						return c;
					}
				}).newInstance().toString();
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		 * @throws Exception 
		*/
	}

	public void setRequestProperty(String key, String value)
	{
		if (value == null || value.length() == 0)
		{
			request_properties.remove(key);
		}
		else
		{
			request_properties.put(key, value);
		}
	}

	public void setDebugOutput(PrintStream os, String Process_RequestHeader_ResponseHeader)
	{
		dbgout = os;
		if (Process_RequestHeader_ResponseHeader != null && Process_RequestHeader_ResponseHeader.length() > 0)
		{
			debug_setting = new HashSet(Arrays.asList(Process_RequestHeader_ResponseHeader.split(",")));
		}
	}
}