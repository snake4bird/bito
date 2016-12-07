package bito.net;

import java.io.PrintStream;
import java.util.Map;

public class URLReader
{
	private d.URLReader ur = d.E.V().newURLReader();

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