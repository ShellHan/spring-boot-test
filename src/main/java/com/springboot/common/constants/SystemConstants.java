/**
 * 
 */
package com.springboot.common.constants;

/**
* @description: 
* @author: hanbei
* @date 创建时间：2018年3月12日
* @version 1.0 
*/

public class SystemConstants {
	
	/**
	 * 系统常量
	 */
	public interface System {
		int CODE_TIMEOUT = 1800; // session超时时间
		int REDIS_LOCK_EXPIRE =5;//redis 锁超时5秒钟
		int REDIS_LOCK_KEY_TIME =10;//redis 锁key值的时间
		int REDIS_LOCK_WAIT_TIME =30;//redis 锁等待最大时间15秒钟
		long DEFAULT_ACQUIRY_RESOLUTION_MILLIS=200;//redis 锁 等待时频率毫秒
		long NO_RESULT_VALUE = -9999L;
		String SYSTEM_NAME="spring_boot";
		String PRODUCT_TYPE="spring";

	}
}


