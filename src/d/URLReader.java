package d;

import java.io.PrintStream;
import java.util.Map;

public interface URLReader
{
	/**
	 * 不会返回null，可能返回0长度byte[]
	 */
	public byte[] ReadURL(String url) throws Exception;

	public byte[] ReadURL(String url, byte[] post) throws Exception;

	public byte[] ReadURL(String url, String user, String password, byte[] post) throws Exception;

	public byte[] ReadURL(String url, byte[] post, String ref) throws Exception;

	public byte[] ReadURL(String url, String user, String password, String proxyhost, String proxyport,
		String proxyuser, String proxypswd, byte[] post, String ref, long rstart, long rend, int timeout)
		throws Exception;

	public byte[] ReadURL(String url, String method, String user, String password, String proxyhost, String proxyport,
		String proxyuser, String proxypswd, Map header, byte[] post, String ref, long rstart, long rend, int timeout)
		throws Exception;

	public byte[] getDataBytes();

	public Map getResponseHeaderFields();

	public String getCookie();

	public String getContentType();

	public void setCookie(String cookie);

	public void cancel();

	public void reset();

	public void setDefaultReadStreamTimeout(int i);

	public void setRequestProperty(String key, String value);

	public void setDebugOutput(PrintStream os, String Process_RequestHeader_ResponseHeader);
}
