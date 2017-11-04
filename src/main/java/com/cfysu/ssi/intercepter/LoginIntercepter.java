package com.cfysu.ssi.intercepter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.cfysu.ssi.model.User;

public class LoginIntercepter implements HandlerInterceptor{

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginIntercepter.class);
	
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object object) throws Exception {
		User user = (User)request.getSession().getAttribute("userName");
		LOGGER.info("sessionId:{}", request.getSession().getId());

		//设置cookie
		response.addCookie(new Cookie("cookie-key", "cookie-value"));

		if(user == null){
			LOGGER.info("userName is null");
		}else {
			LOGGER.info("user is online");
		}
		return true;
	}

}
