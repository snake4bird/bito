package d;

import bito.util.logger.Log;

public abstract class BufferProcessThread extends Thread
{
	private enum RunningStatus {
		ready, starting, running, stopped
	}

	private Object lock = new Object();
	private RunningStatus running = RunningStatus.ready;
	private boolean terminate = false;
	private byte[] message_buffer = new byte[0];
	//
	protected Log log = new Log("");

	public BufferProcessThread()
	{
		setDaemon(true);
	}

	public void start()
	{
		running = RunningStatus.starting;
		super.start();
	}

	public void destroy()
	{
		synchronized(lock)
		{
			// 没有启动，无需终止
			if (running != RunningStatus.starting && running != RunningStatus.running)
			{
				return;
			}
			// 等待进入运行状态
			while(running != RunningStatus.running)
			{
				try
				{
					lock.wait();
				}
				catch(InterruptedException e)
				{
				}
			}
			// 等待信息处理完成
			while(message_buffer.length > 0)
			{
				try
				{
					lock.wait();
				}
				catch(InterruptedException e)
				{
				}
			}
			// 发出终止命令
			terminate = true;
			lock.notifyAll();
			// 等待运行结束
			while(running == RunningStatus.running)
			{
				try
				{
					lock.wait();
				}
				catch(InterruptedException e)
				{
				}
			}
		}
	}

	private boolean isTerminate()
	{
		synchronized(lock)
		{
			return terminate && message_buffer.length == 0;
		}
	}

	public void run()
	{
		synchronized(lock)
		{
			terminate = false;
			running = RunningStatus.running;
			lock.notifyAll();
		}
		while(!isTerminate())
		{
			try
			{
				bufferProc();
			}
			catch(Throwable e)
			{
				log.error(getName() + "消息处理过程出错：", e);
			}
		}
		synchronized(lock)
		{
			running = RunningStatus.stopped;
			lock.notifyAll();
		}
	}

	protected void appendBuffer(byte[] bs)
	{
		synchronized(lock)
		{
			byte[] nmbbs = new byte[message_buffer.length + bs.length];
			System.arraycopy(message_buffer, 0, nmbbs, 0, message_buffer.length);
			System.arraycopy(bs, 0, nmbbs, message_buffer.length, bs.length);
			message_buffer = nmbbs;
			lock.notifyAll();
		}
	}

	protected void bufferProc() throws Exception
	{
		int mbsize = 0;
		synchronized(lock)
		{
			if (message_buffer.length == 0 && !terminate)
			{
				lock.wait();
			}
			mbsize = message_buffer.length;
		}
		while(mbsize > 0)
		{
			int section_length = 0;
			byte[] proc_buf;
			synchronized(lock)
			{
				section_length = message_buffer.length;
				proc_buf = message_buffer;
			}
			try
			{
				// 截取并返回下一段可处理数据长度。
				// 如果截取过程异常，则会清除mbuffer中的所有数据
				section_length = nextSectionBuffer(proc_buf);
				if (section_length > 0)
				{
					byte[] bs = new byte[section_length];
					System.arraycopy(proc_buf, 0, bs, 0, bs.length);
					// 处理部分数据，返回处理完成的数据长度。
					// 如果处理过程异常，则会将该段数据跳过。
					section_length = sectionBufferProc(bs);
				}
			}
			finally
			{
				synchronized(lock)
				{
					if (section_length > 0)
					{
						if (message_buffer.length > section_length)
						{
							//处理完成后或处理过程异常，截断处理过的数据
							byte[] nmbbs = new byte[message_buffer.length - section_length];
							System.arraycopy(message_buffer, section_length, nmbbs, 0, nmbbs.length);
							message_buffer = nmbbs;
						}
						else
						{
							message_buffer = new byte[0];
						}
						mbsize = message_buffer.length;
						lock.notifyAll();
					}
					else
					{
						//如果数据没有改变（新增），则等待数据改变后继续
						if (mbsize == message_buffer.length)
						{
							lock.wait();
						}
						mbsize = message_buffer.length;
					}
				}
			}
		}
	}

	/**
	 * 截取并返回下一段可处理数据长度。
	 * 如果截取过程异常，则会清除mbuffer中的所有数据
	 */
	protected abstract int nextSectionBuffer(byte[] mbuffer);

	/**
	 * 处理部分数据，返回处理完成的数据长度。
	 * 如果处理过程异常，则会将该段数据跳过。
	 */
	protected abstract int sectionBufferProc(byte[] sbuf);
}
