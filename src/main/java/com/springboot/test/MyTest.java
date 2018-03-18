package com.springboot.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.springboot.common.constants.ResultDTO;
import com.springboot.service.UserService;

public class MyTest{
	
	private static final Logger logger = LoggerFactory.getLogger(MyTest.class);
	
	@Autowired
	private UserService userService;
	
	@Test
	public void getUserList(){
		ResultDTO result = userService.getAll();
		logger.info("调用 userService.getAll 返回信息：result{}",result.getData());
	}
	
}
