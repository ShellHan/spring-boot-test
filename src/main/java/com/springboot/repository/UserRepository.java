package com.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.springboot.pojo.User;

@Component("userRepository")
public interface UserRepository extends JpaRepository<User, Integer>{
	
	public User getByEmail(String email);
	
	public User getByLastName(String lastName);
	
}
