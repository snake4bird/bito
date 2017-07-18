package assassin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.SortedMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bito.util.logger.Log;

public class LocalFileProxy extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private HashMap<String, byte[]> cache_file = new HashMap();
	private SortedMap<String, Long> cache_time = bito.util.E.V().newMapSortedByAddTime();
	private long cache_size = 0;
	private String[] file_path_replaceFirst_regx;
	private String[] file_path_replaceFirst_with;
	private Log logger = new Log(":nocachefile");

	public void init() throws ServletException
	{
		super.init();
		ArrayList<String> als_regx = new ArrayList();
		ArrayList<String> als_with = new ArrayList();
		Enumeration eipns = this.getInitParameterNames();
		while(eipns.hasMoreElements())
		{
			String ipn = (String)eipns.nextElement();
			if (ipn.startsWith("file.path.replaceFirst.regx."))
			{
				String fpr_regx = this.getInitParameter(ipn);
				String fpr_with = this.getInitParameter(ipn.replaceFirst("\\.regx\\.", ".with."));
				if (fpr_regx != null && fpr_regx.length() > 0 && fpr_with != null)
				{
					als_regx.add(fpr_regx);
					als_with.add(fpr_with);
				}
			}
		}
		file_path_replaceFirst_regx = als_regx.toArray(new String[0]);
		file_path_replaceFirst_with = als_with.toArray(new String[0]);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		//
		String filepath;
		if (file_path_replaceFirst_regx.length > 0)
		{
			filepath = request.getRequestURI().substring(request.getContextPath().length());
			for(int i = 0; i < file_path_replaceFirst_regx.length; i++)
			{
				filepath = filepath.replaceFirst(file_path_replaceFirst_regx[i], file_path_replaceFirst_with[i]);
			}
		}
		else
		{
			String servletpath = request.getServletPath();
			filepath = this.getServletContext().getRealPath(servletpath);
		}
		byte[] bs;
		try
		{
			//清除10分钟前的缓存
			//最大缓存数据 100MB
			synchronized(cache_file)
			{
				if (cache_time.size() > 0)
				{
					String ctfk = cache_time.firstKey();
					Long ctft = cache_time.get(ctfk);
					if ((ctft != null && ctft < System.currentTimeMillis() + 10 * 60 * 1000)
						|| cache_size > 100 * 1024 * 1024)
					{
						byte[] rbs = cache_file.remove(ctfk);
						if (rbs != null)
						{
							cache_size -= rbs.length;
						}
						cache_time.remove(ctfk);
					}
				}
			}
			//每次读数
			bs = bito.util.E.V().readBytes(filepath);
			//更新缓存
			synchronized(cache_file)
			{
				byte[] rbs = cache_file.remove(filepath);
				if (rbs != null)
				{
					cache_size -= rbs.length;
				}
				cache_file.put(filepath, bs);
				cache_size += bs.length;
				if (cache_time.containsKey(filepath))
				{
					cache_time.remove(filepath);
				}
				cache_time.put(filepath, System.currentTimeMillis());
			}
		}
		catch(IOException e)
		{
			//IO出错时从缓存取数
			synchronized(cache_file)
			{
				bs = cache_file.get(filepath);
			}
			if (bs == null)
			{
				logger.error(e);
				if (e instanceof FileNotFoundException)
				{
					response.sendError(404);
					return;
				}
				else
				{
					throw e;
				}
			}
		}
		response.getWriter().print(new String(bs, "UTF-8"));
	}
}