package com.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.pojo.User;
import com.springboot.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/hello")
	public String hello() {
		return "index";
	}
	
	@RequestMapping("/listAll")
	@ResponseBody
	public List<User> getAll(){
		return userService.getAll();
	}
	
	@RequestMapping(value="/queryByEmail")
	@ResponseBody
	public User queryByEmail(String email){
		System.out.println(email);
		User user = userService.queryByEmail(email);
		System.out.println(user);
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
