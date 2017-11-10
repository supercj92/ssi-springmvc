package com.cfysu.ssi.controller;

import com.alibaba.fastjson.JSON;
import com.cfysu.ssi.model.User;
import com.cfysu.ssi.model.dto.QRCodeZxingDto;
import com.cfysu.ssi.service.UserService;
import com.cfysu.ssi.util.PageResult;
import com.cfysu.ssi.util.QRCodeZxingUtil;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;
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
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
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

	/**
	 * 备忘
	 * 1.使用ResponseBody注解返回的是纯文本，响应头里指定了Content-Type:text/html;charset=UTF-8
	 * 	 使用流返回则不会加上面的Content-Type，会被jquery解析为json对象。可以修改@ResponseBody配置响应头application/json;charset=UTF-8
	 * 2.实现跨域有两种方式。一是在响应头中加Access-Control-Allow-Origin。二是使用jsonp的方式
	 */

	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	@ResponseBody
	public User  getUser(@PathVariable Long id, Model model, HttpServletResponse response){
		User user = userService.selectByPrimaryKey(id);
		//model.addAttribute("user",user);
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST");
		return user;
	}

	@RequestMapping("/jsonp/user/{id}")
	public void  getUserJsonp(@PathVariable Long id, @RequestParam String callback, HttpServletResponse response){
		User user = userService.selectByPrimaryKey(id);
		StringBuilder stringBuilder = new StringBuilder(callback);
		stringBuilder.append("(").append(JSON.toJSONString(user)).append(")");
		try {
			response.getWriter().println(stringBuilder.toString());
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	@RequestMapping("/qrCode")
	public void qrCode(HttpServletResponse response){
		QRCodeZxingDto zxing = new QRCodeZxingDto();
		zxing.setContents("http://www.baidu.com");
		zxing.setCharacterSet("UTF-8");
		zxing.setErrorCorrectionLevel(ErrorCorrectionLevel.H);
		zxing.setFlag(true);
		zxing.setFormat("jpg");
		zxing.setMargin(0);
		zxing.setWidth(300);
		zxing.setHeight(300);
		//zxing.setPath("f:/qrcode/");
		//zxing.setLogoPath("f:/qrcode/3.gif");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST");
		BufferedImage bufferedImage = QRCodeZxingUtil.encode(zxing);
		try {
			ImageIO.write(bufferedImage,"jpg", response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/include")
	public void include(HttpServletRequest request){
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setCharacterEncoding("UTF-8");
		String htmlStr = null;
		try {
			request.getRequestDispatcher("/index.jsp").include(request, response);
			htmlStr = response.getContentAsString();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOGGER.info("html:{}", htmlStr);
	}
}
