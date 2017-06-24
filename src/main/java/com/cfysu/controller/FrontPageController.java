package com.cfysu.controller;

import com.alibaba.fastjson.JSON;
import com.cfysu.model.User;
import com.cfysu.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.PipedReader;

@Controller
@RequestMapping("/")
public class FrontPageController {

	private static final Logger LOGGER = Logger.getLogger(FrontPageController.class);
	@Resource
	private UserService userService;
	@RequestMapping("/testIndex")
	public String testIndex(){
		//LOGGER.info("sssss");
		return "testIndex";
	}

	@RequestMapping("/testDB")
	public void testDB(){
		User user = userService.selectByPrimaryKey(1);
		System.out.println(JSON.toJSONString(user));
	}
}
