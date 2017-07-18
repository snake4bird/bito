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

import bito.util.cfg.SystemConfig;
import bito.util.logger.Log;

public class ASSServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private ASSRunner ar;
	private Thread assrunner;

	public void init() throws ServletException
	{
		super.init();
		SystemConfig.addConfigFile("proc/config.txt");
		ar = new ASSRunner();
		assrunner = new Thread("assassin.runner")
		{
			public void run()
			{
				ar.runner.setParameter("webapp", ASSServlet.this.getServletContext().getServletContextName());
				ar.runner.setParameter("servlet", ASSServlet.this.getServletName());
				ar.runner.setCommonIncludeScriptFilename("proc/init.js");
				ar.run(null);
			}
		};
		assrunner.start();
	}

	public void destroy()
	{
		ar.runner.cleanupAll();
		super.destroy();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		super.doGet(request, response);
	}
}
