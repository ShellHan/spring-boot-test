package com.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.pojo.User;
import com.springboot.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public List<User> getAll(){
		return userRepository.findAll();
	}
	
	public void deleteByid(int id){
		userRepository.delete(id);;
	}
	
	public User queryByEmail(String email){
		return userRepository.getByEmail(email);
	}
}
