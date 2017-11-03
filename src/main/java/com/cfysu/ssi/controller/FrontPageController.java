package com.cfysu.ssi.controller;

import com.cfysu.ssi.model.User;
import com.cfysu.ssi.service.UserService;
import com.cfysu.ssi.util.PageResult;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Controller
public class FrontPageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FrontPageController.class);

	//count是多个请求共享的，因为controller是singleton的
	private Integer count = 0;

	@Resource
	private UserService userService;

	@RequestMapping("/testIndex")
	@ResponseBody
	public String testIndex(){
		try {
			LOGGER.info("---------before sleep:{}--------threadId:{}", count, Thread.currentThread().getId());
			//模拟耗时操作
			Thread.sleep(10000);
			LOGGER.info("---------after sleep:{}--------threadId:{}", count, Thread.currentThread().getId());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//改变成员变量
		count++;
        System.out.print("testIndex:" + new Date());
		return "index";
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public String  getUser(@PathVariable Long id, Model model){
		User user = userService.selectByPrimaryKey(id);
		model.addAttribute("user",user);
		return "upload";
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

	@RequestMapping("/listUser")
	public String listUser(User user, Model model){

		if(user.getPageNum() ==0 && user.getPageSize() == 0){
			user.setPageNum(1);
			user.setPageSize(10);
		}

		PageResult pageResult = userService.queryByPage(user);
		model.addAttribute("pageResult", pageResult);
		return "listUser";
	}
}
