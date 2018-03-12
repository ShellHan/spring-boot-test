package com.springboot.interceptor;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
	
}


