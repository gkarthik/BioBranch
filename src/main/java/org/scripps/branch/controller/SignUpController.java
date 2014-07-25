package org.scripps.branch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SignUpController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SignUpController.class);

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String redirectRequestToRegistrationPage() {
		LOGGER.debug("Redirecting request to registration page.");

		return "redirect:/user/register";
	}

}
