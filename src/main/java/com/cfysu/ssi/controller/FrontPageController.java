package com.cfysu.ssi.controller;

import com.cfysu.ssi.model.User;
import com.cfysu.ssi.service.UserService;
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
	@Resource
	private UserService userService;
	@RequestMapping("/testIndex")
	public String testIndex(){
		//LOGGER.info("sssss");
        System.out.print("testIndex:" + new Date());
		return "testIndex";
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

	/**
	 * 上传文件
	 * @return
	 */
	@RequestMapping("/toUpload")
	public String toUpload(){
		return "upload";
	}

	@RequestMapping("/upload")
	@ResponseBody
	public String uploadFile(@RequestParam(value = "Filedata") MultipartFile file){
		if(file == null){
			LOGGER.info("上传文件为空");
			return "文件为空";
		}
		LOGGER.info("fileName:{}", file.getOriginalFilename());
		URL url = FrontPageController.class.getClassLoader().getResource("/");
		try {
			file.transferTo(new File(url.getPath() + File.separator + file.getOriginalFilename()));
		} catch (IOException e) {
			e.printStackTrace();
			return "fail";
		}
		LOGGER.info("上传成功");
		return "success";
	}

}
