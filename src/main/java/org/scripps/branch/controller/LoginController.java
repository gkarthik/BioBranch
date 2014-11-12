package org.scripps.branch.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LoginController.class);

	protected static final String VIEW_NAME_LOGIN_PAGE = "user/login";

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLoginPage(ModelMap model,HttpServletRequest request) {
		String referrer = request.getHeader("Referer");
		if(referrer!=null){
			if(!referrer.contains("/login") && !referrer.contains("/datasets") ){
			    request.getSession().setAttribute("url_prior_login", referrer);
			}
		}
		return VIEW_NAME_LOGIN_PAGE;
	}
}

