package com.springboot.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.pojo.User;
import com.springboot.repository.UserRepository;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	public List<User> getAll(){
		
		List<User> findAll = userRepository.findAll();
		
		return findAll;
	}
	
	public void deleteByid(int id){
		userRepository.delete(id);;
	}
	
	public User queryByEmail(String email){
		return userRepository.getByEmail(email);
	}
}
