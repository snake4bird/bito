package d;

import java.util.HashMap;

/**
 * Distributed Computation
 */
public interface DC
{
	/**
	 * ע�����DC
	 */
	public void register(DC nc) throws Exception;

	/**
	 * DC id
	 */
	public String id();

	/**
	 * service - workload�������ܹ��������Ϣ���� - ���ڴ���ĻỰ������
	 * null,[]: ���������κ���Ϣ��
	 */
	public HashMap<String, Integer> getService();

	/**
	 * ����Ҫ���������Ϣ����
	 */
	public void callService(String service) throws Exception;

	/**
	 * ������ֹ
	 */
	public void reset() throws Exception;

	/**
	 * ���ûỰID
	 */
	public void setSession(String sessionid) throws Exception;

	/**
	 * ��ȡ��ǰ�ỰID
	 */
	public String getSession() throws Exception;

	/**
	 * ���ñ���
	 */
	public void setVariable(String key, Object value) throws Exception;

	/**
	 * ��ȡ����
	 */
	public Object getVariable(String key) throws Exception;
}
