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
			// û��������������ֹ
			if (running != RunningStatus.starting && running != RunningStatus.running)
			{
				return;
			}
			// �ȴ���������״̬
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
			// �ȴ���Ϣ�������
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
			// ������ֹ����
			terminate = true;
			lock.notifyAll();
			// �ȴ����н���
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
				log.error(getName() + "��Ϣ������̳���", e);
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
				// ��ȡ��������һ�οɴ������ݳ��ȡ�
				// �����ȡ�����쳣��������mbuffer�е���������
				section_length = nextSectionBuffer(proc_buf);
				if (section_length > 0)
				{
					byte[] bs = new byte[section_length];
					System.arraycopy(proc_buf, 0, bs, 0, bs.length);
					// ���������ݣ����ش�����ɵ����ݳ��ȡ�
					// �����������쳣����Ὣ�ö�����������
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
							//������ɺ��������쳣���ضϴ����������
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
						//�������û�иı䣨����������ȴ����ݸı�����
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
	 * ��ȡ��������һ�οɴ������ݳ��ȡ�
	 * �����ȡ�����쳣��������mbuffer�е���������
	 */
	protected abstract int nextSectionBuffer(byte[] mbuffer);

	/**
	 * ���������ݣ����ش�����ɵ����ݳ��ȡ�
	 * �����������쳣����Ὣ�ö�����������
	 */
	protected abstract int sectionBufferProc(byte[] sbuf);
}
