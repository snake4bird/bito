package d;

import bito.util.logger.Log;

public abstract class XThread extends Thread
{
	private Log log = null;
	//
	private EVI evi = d.E.V();
	private boolean terminate = false;
	private boolean isterminated = true;

	public XThread(String name)
	{
		super(name);
		this.setDaemon(true);
	}

	public void setLogger(Log log)
	{
		this.log = log;
	}

	public void destroy(long timeout)
	{
		terminate(timeout);
	}

	protected void terminate(long timeout)
	{
		terminate = true;
		synchronized(this)
		{
			long t = System.currentTimeMillis();
			while(!isterminated && (timeout == 0 || System.currentTimeMillis() - t < timeout))
			{
				try
				{
					wait(1000);
				}
				catch(InterruptedException e)
				{
				}
			}
		}
	}

	public void run()
	{
		terminate = false;
		isterminated = false;
		try
		{
			startloop();
			while(!terminate && evi == d.E.V())
			{
				try
				{
					loop();
				}
				catch(Throwable e)
				{
					if (log != null)
					{
						log.error("error in loop", e);
					}
				}
				if (!terminate)
				{
					try
					{
						interloop();
					}
					catch(Throwable e)
					{
						if (log != null)
						{
							log.error("error inter loop", e);
						}
					}
				}
			}
			endloop();
		}
		catch(Throwable t)
		{
			if (log != null)
			{
				log.error("fault in run", t);
			}
		}
		finally
		{
			try
			{
				synchronized(this)
				{
					isterminated = true;
					notifyAll();
				}
			}
			catch(Throwable t)
			{
			}
		}
	}

	protected void interloop() throws Exception
	{
		sleep(55);
	}

	protected void startloop() throws Exception
	{
		if (log != null)
		{
			log.debug("Thread " + getName() + " start.");
		}
	}

	protected void endloop() throws Exception
	{
		if (log != null)
		{
			log.debug("Thread " + getName() + " end.");
		}
	}

	protected abstract void loop() throws Exception;
}
