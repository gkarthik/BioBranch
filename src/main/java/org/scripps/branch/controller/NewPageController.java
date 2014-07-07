package org.scripps.branch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class NewPageController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(NewPageController.class);

	protected static final String VIEW_NAME_NEW_PAGE = "user/new";

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String showNewPage() {
		LOGGER.debug("Rendering new page.");
		return VIEW_NAME_NEW_PAGE;
	}

}