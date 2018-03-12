package com.springboot.service.redis;
/**
* @description: 
* @author: hanbei
* @date 创建时间：2018年3月12日
* @version 1.0 
*/
public interface RedisBusiness {

	public String getValue(String key);

	public void setValue(String key, String msg);

	public void setValue(String key, String msg, int time);

	public void delValue(String key);
	
	
	/**
	 * 获取redis共享式-悲观
	* @author: liuyafei
	* @date 创建时间：2017年3月29日
	* @version 1.0 
	* @parameter  
	* @return 成功返回1 如果存在 和 发生异常 返回 0
	 * @throws InterruptedException 
	 */
	public int lockWait(String key) throws InterruptedException;
	
	/**
	 * 获取redis共享锁-乐观
	* @author: liuyafei
	* @date 创建时间：2017年3月29日
	* @version 1.0 
	* @parameter  
	* @return 成功返回1 如果存在 和 发生异常 返回 0
	 */
	public int lock(String key);
	
	/**
	 *redis共享锁-解
	* @author: liuyafei
	* @date 创建时间：2017年3月29日
	* @version 1.0 
	* @parameter  
	 */
	public void unlock(String key);
}
