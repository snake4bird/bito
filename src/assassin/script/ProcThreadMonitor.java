package assassin.script;

public interface ProcThreadMonitor
{
	/**
	 * �����߳̽���
	 */
	public void procThreadEnd(ProcThread pt, Object result);

	/**
	 * �����߳���Ϣ���
	 */
	public void procThreadInfo(ProcThread pt, String message);

	/**
	 * �����߳̾�����Ϣ���
	 */
	public void procThreadWarn(ProcThread pt, String message);

	/**
	 * �����̴߳�����Ϣ���
	 */
	public void procThreadError(ProcThread pt, String message, Throwable e);

	/**
	 * һ�δ�����̿�ʼ
	 */
	public void procThreadProcBegin(ProcThread procThread);

	/**
	 * һ�δ���������
	 */
	public void procThreadProcEnd(ProcThread procThread);
}
