package com.springboot.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

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

import com.springboot.common.annoation.ServiceCache;
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

	        logger.debug("pointcut start...");

	        long startTime = System.currentTimeMillis();

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
	        Object firstParamObj = params[0];
	        //方法返回值
	        Object returnValue = null;
	        Method objMethod = null;

	        try {
	            objMethod = classTarget.getMethod(methodName, par);
	        } catch (NoSuchMethodException e) {
	            logger.error("", e);
	            return null;
	        }

	        //获取缓存注解信息
	        ServiceCache serviceCache = objMethod.getAnnotation(ServiceCache.class);
	        if (serviceCache != null){
	            String spelExpress = serviceCache.keyName();
	            fee = serviceCache.fee();
	            cacheTime = serviceCache.cacheTime();

	            if (spelExpress != null){

	                keyName = getCacheKeyBySpEL(firstParamObj, spelExpress);
	                logger.debug("spelExpress:{}, fee:{}, cacheTime:{}, cacheKeyName:{}", spelExpress, fee, cacheTime, keyName);
	                //从缓存获取值
	                String cacheValue = redisBusiness.getValue(keyName);
	                if (cacheValue != null){
	                	String oResult = JSONObject.parseObject(cacheValue, new TypeReference(){});
	                    logger.debug("from cache, value = {}", cacheValue);
	                    long endTime = System.currentTimeMillis();
	                    if (params != null && params.length > 0){
	                        String paramClassName = firstParamObj.getClass().getName();
	                        //组装BigData数据
	                        bigData = assembleCreditSourceBigData(bigData, paramClassName, firstParamObj, objMethod);
	                    }
	                    bigData.setClacTime(endTime - startTime);
	                    bigData.setInStr(JSONObject.toJSONString(firstParamObj));
	                    bigData.setOutStr(StringUtils.isBlank(cacheValue) ? "null" : cacheValue);
	                    bigData.setCreateTime(new Date());
	                    bigData.setDataSource("cache");
	                    //TODO 埋点发送到大数据
	                    creditSendBigDataBusiness.sendBigData(bigData);
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
	        if (creditSourceCache != null){
	            long endTime = System.currentTimeMillis();
	            if (params != null && params.length > 0){
	                String paramClassName = firstParamObj.getClass().getName();
	                //组装BigData数据
	                bigData = assembleCreditSourceBigData(bigData, paramClassName, firstParamObj, objMethod);
	            }
	            bigData.setClacTime(endTime - startTime);
	            bigData.setInStr(JSONObject.toJSONString(firstParamObj));
	            bigData.setOutStr(returnValue == null ? "null" : JSONObject.toJSONString(returnValue));
	            bigData.setCreateTime(new Date());
	            bigData.setDataSource("interface");
	            //TODO 埋点发送到大数据
	            creditSendBigDataBusiness.sendBigData(bigData);

	            //TODO 解析各征信源返回报文,做缓存信息，剔除错误信息，只缓存正确的信息
	            if (returnValue != null){
	                OriginalResult<String>  originalResult = (OriginalResult<String>)returnValue;
	                boolean checkFlag = false;
	                try {
	                    checkFlag = checkResultCanCache(originalResult, bigData.getCreditMainName(), bigData.getCreditChildName());
	                }catch (Exception e) {
	                    logger.error("", e);
	                }
	                if(checkFlag){
	                    logger.debug("缓存到redis, keyName={}, value={}, cacheTime={}", keyName, originalResult, cacheTime);
	                    redisBusiness.setValue(keyName, JSONObject.toJSONString(originalResult), cacheTime);
	                }
	            }
	        }
	        logger.debug("切点结束。。。");
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


	    /**
	    * @description: 组装BigData数据信息
	    * @author: qinyukun
	    * @date 创建时间：2017-11-6
	    * @version 1.0
	    * @param
	    * @return
	    */
	    private  CreditSourceBigData  assembleCreditSourceBigData(CreditSourceBigData bigData, String className, Object firstParamObj, Method  objMethod){

	        try {
	            Class clazz = Class.forName(className);
	            Field[] superFields = clazz.getSuperclass().getDeclaredFields();
	            for (Field field : superFields){
	                field.setAccessible(true);
	                String  fName = field.getName();
	                Object fieldValue =  field.get(firstParamObj);
	                if (fieldValue == null){
	                    logger.debug("属性名：{} = {}", fName, fieldValue);

	                }else {
	                    logger.debug("属性名：{} = {}", fName, fieldValue);
	                    if ("userId".equals(fName)){
	                        bigData.setUserId(fieldValue.toString());
	                    }
	                    if ("addProductType".equals(fName)){
	                        bigData.setProductType(fieldValue.toString());
	                    }
	                    if ("addProduct".equals(fName)){
	                        bigData.setFromSource(fieldValue.toString());
	                    }
	                    if ("phoneNumber".equals(fName)){
	                        bigData.setPhone(fieldValue.toString());
	                    }
	                }
	            }

	            CreditSourceType creditSourceType =  objMethod.getAnnotation(CreditSourceType.class);
	            logger.debug("征信源类型：creditSourceType:{}", creditSourceType);
	            if (creditSourceType != null){
	                String mainName = creditSourceType.mainName();
	                String childName = creditSourceType.childName();
	                bigData.setCreditMainName(mainName);
	                bigData.setCreditChildName(childName);
	            }else{
	                bigData.setCreditChildName("unknown");
	                bigData.setCreditMainName("unknown");
	            }

	        } catch (ClassNotFoundException e) {
	            logger.error("", e);
	        } catch (IllegalAccessException e) {
	            logger.error("", e);
	        }
	        return  bigData;
	    }


	    /**
	    * @description: 校验返回结果是否可以存储
	    * @author: qinyukun
	    * @date 创建时间：2017-11-6
	    * @version 1.0
	    * @param originalResult 切点方法返回的结果
	     *@param creditType 调用的征信源类型，不同征信源返回的结果报文有差异
	    * @return
	    */
	    private boolean checkResultCanCache(OriginalResult<String>  originalResult, String creditName, String creditType){
	        boolean flag = false;
	        logger.debug("切面调用  checkResultCanCache(), creditName = {}, creditType = {}", creditName, creditType);
	        switch (creditName){
	            case CreditSourceName.BaiDu:
	                return CheckCreditSourceResultUtil.checkBaiDuResult(originalResult, creditType);
	            case CreditSourceName.BaiRong:
	                return CheckCreditSourceResultUtil.checkBaiRongResult(originalResult, creditType);
	            case CreditSourceName.ChuBao:
	                return CheckCreditSourceResultUtil.checkChuBaoResult(originalResult);
	            case CreditSourceName.HaiXin:
	                return CheckCreditSourceResultUtil.checkHaiXinResult(originalResult, creditType);
	            case CreditSourceName.JiGuang:
	                return CheckCreditSourceResultUtil.checkJiGuangResult(originalResult, creditType);
	            case CreditSourceName.JuXinLi:
	                return CheckCreditSourceResultUtil.checkJuXinLiResult(originalResult);
	            case CreditSourceName.KeXin:
	                return CheckCreditSourceResultUtil.checkKeXinResult(originalResult, creditType);
	            case CreditSourceName.PingAn:
	                return CheckCreditSourceResultUtil.checkPingAnResult(originalResult, creditType);
	            case CreditSourceName.QianHai:
	                return CheckCreditSourceResultUtil.checkQianHaiResult(originalResult, creditType);
	            case CreditSourceName.TengXun:
	                return CheckCreditSourceResultUtil.checkTengXunResult(originalResult, creditType);
	            case CreditSourceName.TongDun:
	                return CheckCreditSourceResultUtil.checkTongDunResult(originalResult, creditType);
	            case CreditSourceName.UserEdu:
	                return CheckCreditSourceResultUtil.checkUserEduResult(originalResult, creditType);
	            case CreditSourceName.YinLian:
	                return CheckCreditSourceResultUtil.checkYinLianResult(originalResult, creditType);
	            case CreditSourceName.ZhiMa:
	                return CheckCreditSourceResultUtil.checkZhiMaChengResult(originalResult, creditType);
	            case CreditSourceName.ZhongZhiCheng:
	                return CheckCreditSourceResultUtil.checkZhongZhiChengResult(originalResult, creditType);
	            case CreditSourceName.XiaoShiKeJi:
	                return CheckCreditSourceResultUtil.checkXiaoShiKeJiResult(originalResult, creditType);
	            case CreditSourceName.SuanHua:
	                return CheckCreditSourceResultUtil.checkSuanHuaResult(originalResult, creditType);
	            case CreditSourceName.MobileInfo:
	                return CheckCreditSourceResultUtil.checkMobileInfoResult(originalResult, creditType);
	            default: break;
	        }
	        return flag;
	    }

	
}


