package com.cfysu.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class FrontPageController {

	private static final Logger LOGGER = Logger.getLogger(FrontPageController.class);
	
	@RequestMapping("/testIndex")
	public void testIndex(){
		LOGGER.info("sssss");
		System.out.println("ddddd");
	}
}
