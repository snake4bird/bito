package assassin.script;

public interface ProcThreadMonitor
{
	/**
	 * 处理线程结束
	 */
	public void procThreadEnd(ProcThread pt, Object result);

	/**
	 * 处理线程信息输出
	 */
	public void procThreadInfo(ProcThread pt, String message);

	/**
	 * 处理线程警告信息输出
	 */
	public void procThreadWarn(ProcThread pt, String message);

	/**
	 * 处理线程错误信息输出
	 */
	public void procThreadError(ProcThread pt, String message, Throwable e);

	/**
	 * 一次处理过程开始
	 */
	public void procThreadProcBegin(ProcThread procThread);

	/**
	 * 一次处理过程完成
	 */
	public void procThreadProcEnd(ProcThread procThread);
}
