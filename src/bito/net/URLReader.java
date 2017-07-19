package bito.net;

import java.io.PrintStream;
import java.util.Map;

public class URLReader
{
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: URLReader http[s]://host[:port]/... [/D for debug]");
			return;
		}
		URLReader ur = new URLReader();
		try
		{
			if (args.length > 1 && "/D".equals(args[1]))
			{
				ur.setDebugOutput(System.out, null);
			}
			byte[] bs = ur.ReadURL(args[0], System.getProperty("username"), System.getProperty("password"), null);
			System.out.println(new String(bs));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private URLReader ur = (URLReader)b.i.t.o();

	public URLReader()
	{
	}

	public byte[] ReadURL(String url) throws Exception
	{
		return ur.ReadURL(url);
	}

	public byte[] ReadURL(String url, byte[] post) throws Exception
	{
		return ur.ReadURL(url, post);
	}

	public byte[] ReadURL(String url, String user, String password, byte[] post) throws Exception
	{
		return ur.ReadURL(url, user, password, post);
	}

	public byte[] ReadURL(String url, byte[] post, String ref) throws Exception
	{
		return ur.ReadURL(url, post, ref);
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
		return ur.ReadURL(url,
			user,
			password,
			proxyhost,
			proxyport,
			proxyuser,
			proxypswd,
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
		return ur.ReadURL(url,
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
	}

	public String ReadString(String url, String method, String user, String password, String proxyhost,
		String proxyport, String proxyuser, String proxypswd, Map header, String post, String ref, long rstart,
		long rend, int timeout) throws Exception
	{
		return new String(ur.ReadURL(url,
			method,
			user,
			password,
			proxyhost,
			proxyport,
			proxyuser,
			proxypswd,
			header,
			post != null?post.getBytes():null,
			ref,
			rstart,
			rend,
			timeout));
	}

	public byte[] getDataBytes()
	{
		return ur.getDataBytes();
	}

	public Map getResponseHeaderFields()
	{
		return ur.getResponseHeaderFields();
	}

	public String getCookie()
	{
		return ur.getCookie();
	}

	public String getContentType()
	{
		return ur.getContentType();
	}

	public void setCookie(String cookie)
	{
		ur.setCookie(cookie);
	}

	public void cancel()
	{
		ur.cancel();
	}

	public void reset()
	{
		ur.reset();
	}

	public void setDefaultReadStreamTimeout(int i)
	{
		ur.setDefaultReadStreamTimeout(i);
	}

	public void setRequestProperty(String key, String value)
	{
		ur.setRequestProperty(key, value);
	}

	public void setDebugOutput(PrintStream os, String Process_RequestHeader_ResponseHeader)
	{
		ur.setDebugOutput(os, Process_RequestHeader_ResponseHeader);
	}
}