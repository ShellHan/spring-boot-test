package com.springboot.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.springboot.App;
import com.springboot.common.constants.ResultDTO;
import com.springboot.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@EnableAutoConfiguration
public class MyTest {

	private static final Logger logger = LoggerFactory.getLogger(MyTest.class);
	
	@Autowired
	private UserService userService;
	
	@Test
	public void getUserList(){
		ResultDTO result = userService.getAll();
		logger.info("调用 userService.getAll 返回信息：result{}",result.getData());
	}
	
}
