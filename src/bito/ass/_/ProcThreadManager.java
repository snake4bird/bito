package bito.ass._;

import java.util.Map;

public interface ProcThreadManager
{
	/**
	 * �����µĴ����߳�
	 * @param procname �����߳�����
	 * @param sqlfiles ��������ļ��������ϵͳ��ǰ·��
	 * @param schedule ϵͳ����ʱ�䰲�ţ�cron���ʽ������Ϊnull
	 * @param start_delay  �״������ӳ��룬0����������-1ֻ��cron���ʽ����
	 * @param min_interval �����������ִ�е���Сʱ������
	 * @param parameters ָ��������̲���
	 */
	public void start(String procname, String sqlfiles, String schedule, long start_delay, long min_interval,
		Map parameters);

	/**
	 * ���ش����������״̬ 
	 * 		"N" (none���̲߳�����) 
	 * 		"W" (waiting���߳��Ѿ���������δִ�д������) 
	 * 		"R" (running�������������ִ��) 
	 * 		"E" (error����������쳣����) 
	 * 		"C" (completed�������߳���������) 
	 * �����߳̽�����״̬����һ�죬��ʱ��״̬��Ϊ"N" (none)
	 */
	public String status(String procname);

	/**
	 * ��ǰ������̽�������ֹ�����̣߳�
	 * ����ֹͣ�����̣߳��������ж�����ִ�еĴ������
	 */
	public void stop(String procname);

	/**
	 * ��ȡ�������ݣ��ڶ���������֮�乲������
	 */
	public Object share(String sharename);

	/**
	 * ���ù������ݣ��ڶ���������֮�乲������
	 */
	public void share(String sharename, Object object);

	/**
	 * �������еĴ������
	 */
	public String[] runningProcs();
}
