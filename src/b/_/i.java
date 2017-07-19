package b._;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class i extends b.i
{
	private final String bito_dir = this.toString();

	public i()
	{
	}

	public void a()
	{
		
	}

	/**
	 * 获取指定Class cls的package或其所在jar的所在路径，
	 * 结尾没有“/”
	 * @param cls
	 * @return
	 */
	private String cl(Class cls)
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

	public String toString()
	{
		if (bito_dir == null)
		{
			return cl(b.i.class);
		}
		return bito_dir;
	}
}
