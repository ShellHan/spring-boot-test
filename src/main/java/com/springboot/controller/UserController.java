package com.springboot.controller;

import java.util.List;

import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.common.constants.ResultDTO;
import com.springboot.pojo.User;
import com.springboot.service.UserService;

@RestController
public class UserController {
	
	private static final  Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/hello")
	public String hello() {
		return "index";
	}
	
	@RequestMapping("/listAll")
	@ResponseBody
	public ResultDTO getAll(){
		return userService.getAll();
	}
	
	@RequestMapping(value="/queryByEmail")
	@ResponseBody
	public User queryByEmail(@PathParam("email") String email){
		User user = userService.queryByEmail(email);
		LOGGER.debug("调用  userService.queryByEmail 返回结果，email{}", email); 
		return user;
	}
	
	@RequestMapping("/deleteByid/{id}")
	@ResponseBody
	public int deleteByid(@PathVariable("id")Integer id){
		try {
			userService.deleteByid(id);
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return 0;
	}
	
}
