package com.cfysu.controller;

import com.alibaba.fastjson.JSON;
//import com.cfysu.model.User;
import com.cfysu.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.PipedReader;
import java.util.Date;

@Controller
@RequestMapping("/")
public class FrontPageController {

	//private static final Logger LOGGER = Logger.getLogger(FrontPageController.class);
	//@Resource
	//private UserService userService;
	@RequestMapping("/testIndex")
    @ResponseBody
	public String testIndex(){
		//LOGGER.info("sssss");
        System.out.print("testIndex:" + new Date());
		return "testIndex";
	}

	@RequestMapping("/testDB")
    @ResponseBody
	public String  testDB(){
		//User user = userService.selectByPrimaryKey(1);
		//System.out.println(JSON.toJSONString(user));
        return "testDB";
	}

}
