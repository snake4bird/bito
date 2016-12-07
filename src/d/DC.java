package d;

import java.util.HashMap;

/**
 * Distributed Computation
 */
public interface DC
{
	/**
	 * 注册相关DC
	 */
	public void register(DC nc) throws Exception;

	/**
	 * DC id
	 */
	public String id();

	/**
	 * service - workload：代表能够处理的信息类型 - 正在处理的会话数量。
	 * null,[]: 代表不处理任何信息。
	 */
	public HashMap<String, Integer> getService();

	/**
	 * 即将要请求处理的信息类型
	 */
	public void callService(String service) throws Exception;

	/**
	 * 请求终止
	 */
	public void reset() throws Exception;

	/**
	 * 设置会话ID
	 */
	public void setSession(String sessionid) throws Exception;

	/**
	 * 获取当前会话ID
	 */
	public String getSession() throws Exception;

	/**
	 * 设置变量
	 */
	public void setVariable(String key, Object value) throws Exception;

	/**
	 * 获取变量
	 */
	public Object getVariable(String key) throws Exception;
}
