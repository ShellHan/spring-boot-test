package com.springboot.interceptor;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.springboot.common.annoation.ServiceCache;
import com.springboot.common.constants.ResultDTO;
import com.springboot.service.redis.RedisBusiness;

/**
* @description: 
* @author: hanbei
* @date 创建时间：2018年3月12日
* @version 1.0 
*/
@Aspect
@Component
public class MyInterceptor {
	
	public static final String MY_CREDIT_SOURCE = "execution(* com.springboot.service.*.*(..))";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RedisBusiness redisBusiness;

    @Around(value = MY_CREDIT_SOURCE)
    public  Object  sendLogData(ProceedingJoinPoint jp){

        logger.info("pointcut start...");
        //缓存信息
        int cacheTime = 60*60*24;
        String keyName = null;
        String fee = "FEE";

        //获取当前类
        Class<?> classTarget = jp.getTarget().getClass();
        //获取目标方法体参数
        Object[] params = jp.getArgs();
        //获取方法名
        String methodName = jp.getSignature().getName();
        //获取方法签名类型
        Class<?>[] par = ((MethodSignature) jp.getSignature()).getParameterTypes();
        //方法前面第一个参数
        Object firstParamObj = null;
        if(params != null && params.length > 0 ){
        	firstParamObj = params[0];
        }
        
        //方法返回值
        Object returnValue = null;
        Method objMethod = null;

        try {
            objMethod = classTarget.getMethod(methodName, par);
        } catch (NoSuchMethodException e) {
            logger.error("", e);
            return null;
        }
        
        //获取返回值类型
        Class<?> returnClass = objMethod.getReturnType();
        
        //获取缓存注解信息
        ServiceCache serviceCache = objMethod.getAnnotation(ServiceCache.class);
        
        if (serviceCache != null){
            String spelExpress = serviceCache.keyName();
            fee = serviceCache.fee();
            cacheTime = serviceCache.cacheTime();

            if (spelExpress != null){

                keyName = getCacheKeyBySpEL(firstParamObj, spelExpress);
                logger.info("spelExpress:{}, fee:{}, cacheTime:{}, cacheKeyName:{}", spelExpress, fee, cacheTime, keyName);
                //从缓存获取值
                String cacheValue = redisBusiness.getValue(keyName);
                if (cacheValue != null){
                	logger.info("从缓存中取值 cacheValue：{}",cacheValue);
                	ResultDTO oResult = JSONObject.parseObject(cacheValue, ResultDTO.class);
                    return oResult;
                }
            }
        }


        try {
            returnValue = jp.proceed();
        } catch (Throwable throwable) {
            logger.error("切面调用方法error", throwable);
            return null;
        }
       if (serviceCache != null){

            //TODO 解析各征信源返回报文,做缓存信息，剔除错误信息，只缓存正确的信息
            if (returnValue != null){
                String  originalResult = JSONObject.toJSONString(returnValue);
                if(originalResult != null){
                    logger.info("缓存到redis, keyName={}, value={}, cacheTime={}", keyName, originalResult, cacheTime);
                    redisBusiness.setValue(keyName, originalResult, cacheTime);
                }
            }
        }
        logger.info("切点结束。。。");
        
		return returnValue;
    }


    /**
    * @description: EL表达式转换并生成redisKey
    * @author: qinyukun
    * @date 创建时间：2017-11-6
    * @version 1.0
    * @param
    * @return
    */
    private  String  getCacheKeyBySpEL(Object object, String spelExpress){

        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("entity", object);
        Expression expression = parser.parseExpression(spelExpress, new TemplateParserContext());
        String  key = expression.getValue(context, String.class);
        logger.debug("表达式转换后的key:{}", key);
        return key;
    }
}


