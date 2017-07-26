package com.cfysu.controller;

import com.alibaba.fastjson.JSON;
import com.cfysu.model.User;
import com.cfysu.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;

@Controller
public class FrontPageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FrontPageController.class);
	@Resource
	private UserService userService;
	@RequestMapping("/testIndex")
	public String testIndex(){
		//LOGGER.info("sssss");
        System.out.print("testIndex:" + new Date());
		return "testIndex";
	}

	@RequestMapping("/testDB")
    @ResponseBody
	public User  testDB(){
		User user = userService.selectByPrimaryKey(1);
		System.out.println(JSON.toJSONString(user));
        return user;
	}

	@RequestMapping("/insertUser")
	@ResponseBody
	public String insertUser(){
		User user = new User();
		user.setUserName("rose");
		user.setPwd("456");
		userService.insert(user);

		return "ok";
	}

	@RequestMapping("/toUpload")
	public String toUpload(){

		return "upload";
	}
}
