package com.springboot.service.redis;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Service;

import com.springboot.common.constants.SystemConstants;
import com.springboot.common.utils.DateKit;

/**
* @description: 
* @author: hanbei
* @date 创建时间：2018年3月12日
* @version 1.0 
*/
@Service
public class RedisBusinessImpl implements RedisBusiness {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RedisTemplate<String,String> redisClientTemplate;
	
	/**
	 * 获取redis值
	* @author: hanbei
	* @date 创建时间：2018年3月12日
	* @version 1.0 
	* @parameter  
	* @return
	 */
	public String getValue(String key) {
		return redisClientTemplate.opsForValue().get(key);
	}

	/**
	 * 获取redis共享锁-乐观
	* @author: hanbei
	* @date 创建时间：2018年3月12日
	* @version 1.0 
	* @parameter  
	* @return 成功返回1 如果存在 和 发生异常 返回 0
	 */
	public int lock(final String key) {
		logger.info("进入lock：key:{}", key);
		int flag = 0 ;
		if (redisClientTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();
                byte[] value = jdkSerializer.serialize(getLockValue());
                return connection.setNX(key.getBytes(), value);
            }

        })) {
			flag = 1;
		}
		logger.info("进入lock：key:{},{}", key,"setnx状态:"+flag);
		if(flag==1){//拿到锁
			redisClientTemplate.expire(key, SystemConstants.System.REDIS_LOCK_EXPIRE, TimeUnit.MILLISECONDS);
			return flag;
		}
		logger.info("进入lock：key:{},{}", key,"setnx 未获取锁");
		//key值存在判断是否过期
		String oldTime = redisClientTemplate.opsForValue().get(key).toString();
		if(oldTime==null){
			return flag;
		}
		logger.info("进入lock：key:{},{}", key,"oldTime:"+ DateKit.compareTime(oldTime));
		if(DateKit.compareTime(oldTime)){
			oldTime = redisClientTemplate.opsForValue().getAndSet(key, getLockValue());
			//判断时间是否过期，如果过期则拿到锁
			logger.info("进入lock：key:{},{}", key,"如果过期则拿到锁:"+DateKit.compareTime(oldTime));
			if(DateKit.compareTime(oldTime)){
				redisClientTemplate.expire(key, SystemConstants.System.REDIS_LOCK_EXPIRE, TimeUnit.MILLISECONDS);
				flag=1;
			}
		}
		logger.info("进入lock：key:{},{}", key,"结果:"+flag);
		return flag;
	}
	
	/**
	 * 生成共享锁的value
	* @author: hanbei
	* @date 创建时间：2017年5月4日
	* @version 1.0 
	* @parameter  
	* @return
	 */
	private String getLockValue(){
		return (DateKit.getNowLongTime()+SystemConstants.System.REDIS_LOCK_KEY_TIME)+"";
	}
	/**
	 * 获取redis共享锁-悲观
	* @author: hanbei
	* @date 创建时间：2018年3月12日
	* @version 1.0 
	* @parameter  
	* @return 成功返回1 如果存在 和 发生异常 返回 0
	 * @throws InterruptedException 
	 */
	public int lockWait(String key) throws InterruptedException {
		int flag = 0;
		logger.info("进入lockWait：key:{}", key);
		int timeout= 5*1000;
		while (timeout >= 0) {
			flag=lock(key);
			logger.info("进入lockWait：key:{},while value:{}", key,flag);
			if(flag==1){
				return flag;
			}
            timeout -= SystemConstants.System.DEFAULT_ACQUIRY_RESOLUTION_MILLIS;
            /*延迟200 毫秒,  这里使用随机时间可能会好一点,可以防止饥饿进程的出现,即,当同时到达多个进程,
             *只会有一个进程获得锁,其他的都用同样的频率进行尝试,后面有来了一些进行,也以同样的频率申请锁,这将可能导致前面来的锁得不到满足.
             *使用随机的等待时间可以一定程度上保证公平性
             */
            Thread.sleep(SystemConstants.System.DEFAULT_ACQUIRY_RESOLUTION_MILLIS);
        }
		logger.info("进入lockWait：key:{},{}", key,"结果:"+flag);

		return flag;
	}
	
	
	/**
	 *redis共享锁-解
	* @author: hanbei
	* @date 创建时间：2018年3月12日
	* @version 1.0 
	* @parameter  
	 */
	public void unlock(String key) {
		logger.debug("释放锁：key = {}", key);
		redisClientTemplate.delete(key);
	}
	
	/**
	 * 设置reids值
	* @author: hanbei
	* @date 创建时间：2018年3月12日
	* @version 1.0 
	* @parameter  
	* @return
	 */
	public void setValue(String key, String msg) {
		setValue(key, msg, 0);
	}

	/**
	 * 设置reids值--有效期
	* @author: hanbei
	* @date 创建时间：2018年3月12日
	* @version 1.0 
	* @parameter  
	* @return
	 */
	public void setValue(String key, String msg, int time) {
		redisClientTemplate.opsForValue().set(key, msg);
		if (time != 0)
			redisClientTemplate.expire(key, time, TimeUnit.MILLISECONDS);
	}

	
	/**
	 * 删除redis值
	* @author: hanbei
	* @date 创建时间：2018年3月12日
	* @version 1.0 
	* @parameter  
	* @return
	 */
	public void delValue(String key) {
		redisClientTemplate.delete(key);
	}

}
