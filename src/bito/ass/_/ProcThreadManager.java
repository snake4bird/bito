package bito.ass._;

import java.util.Map;

public interface ProcThreadManager
{
	/**
	 * 启动新的处理线程
	 * @param procname 处理线程名称
	 * @param sqlfiles 处理过程文件名，相对系统当前路径
	 * @param schedule 系统运行时间安排，cron表达式，可以为null
	 * @param start_delay  首次启动延迟秒，0立即启动，-1只按cron表达式运行
	 * @param min_interval 处理过程两次执行的最小时间间隔秒
	 * @param parameters 指定处理过程参数
	 */
	public void start(String procname, String sqlfiles, String schedule, long start_delay, long min_interval,
		Map parameters);

	/**
	 * 返回处理过程运行状态 
	 * 		"N" (none，线程不存在) 
	 * 		"W" (waiting，线程已经启动，但未执行处理过程) 
	 * 		"R" (running，处理过程正在执行) 
	 * 		"E" (error，处理过程异常结束) 
	 * 		"C" (completed，处理线程正常结束) 
	 * 处理线程结束后，状态保留一天，超时后状态归为"N" (none)
	 */
	public String status(String procname);

	/**
	 * 当前处理过程结束后，终止处理线程，
	 * 即，停止处理线程，但不会中断正在执行的处理过程
	 */
	public void stop(String procname);

	/**
	 * 获取共享数据，在多个处理过程之间共享数据
	 */
	public Object share(String sharename);

	/**
	 * 设置共享数据，在多个处理过程之间共享数据
	 */
	public void share(String sharename, Object object);

	/**
	 * 正在运行的处理过程
	 */
	public String[] runningProcs();
}
