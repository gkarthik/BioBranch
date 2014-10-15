package org.scripps.branch.controller;

import org.scripps.branch.entity.User;
import org.scripps.branch.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

@Controller
public class HeaderController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(HeaderController.class);

	protected static final String VIEW_NAME_PROFILEPAGE = "help";

	@RequestMapping(value = "/features", method = RequestMethod.GET)
	public String showHomePage(WebRequest request, Model model) {
		return VIEW_NAME_PROFILEPAGE;
	}
	
	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String showContactPage(WebRequest request, Model model) {
		return "contact";
	}
}
