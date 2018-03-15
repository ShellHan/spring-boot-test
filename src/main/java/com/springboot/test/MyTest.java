package com.springboot.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.springboot.App;
import com.springboot.pojo.User;
import com.springboot.service.UserService;

/**
* @description: 
* @author: hanbei
* @date 创建时间：2018年3月14日
* @version 1.0 
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class MyTest {
	
	private static final Logger logger = LoggerFactory.getLogger(MyTest.class);
	
	@Autowired
	private UserService userService;
	
	@Test
	public void getUsers(){
		List<User> all = userService.getAll();
		logger.info("返回结果集：all{}",all);
	}
	
}


