package com.springboot.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.springboot.common.annoation.ServiceCache;
import com.springboot.common.constants.ResultDTO;
import com.springboot.pojo.User;
import com.springboot.repository.UserRepository;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	
	public ResultDTO getAll(){		
		List<User> findAll = userRepository.findAll();
		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setData(JSON.toJSONString(findAll));
		resultDTO.setErrorCode("00000000");
		resultDTO.setStatus("400");
		resultDTO.setMsg("success");
		return resultDTO;
	}
	
	public void deleteByid(int id){
		userRepository.delete(id);
	}
	
	@ServiceCache(keyName = "User_email_#{#email}", cacheTime = 259201)
	public User queryByEmail(String email){
		return userRepository.getByEmail(email);
	}
}
