package org.scripps.branch.controller;

import java.util.List;

import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.User;
import org.scripps.branch.repository.CollectionRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@Controller
public class CollectionController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CollectionController.class);

	@Autowired
	UserRepository userRepo;

	@Autowired
	CollectionRepository collRepo;

	protected static final String VIEW_PAGE = "user/collection";
	protected static final String VIEW_PUBLIC_COLLECTION="user/publicCollection";


	protected static final String VIEW = "user/view";
	protected static final String ADD = "user/addCollection";

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody String addCollection(
			@RequestParam("name") String name,
			@RequestParam("description") String description) {

		UserDetails userDetails = null;
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		User user = null;
		Collection colObj = new Collection();

		if (!(auth instanceof AnonymousAuthenticationToken)) {
			userDetails = (UserDetails) auth.getPrincipal();
			user = userRepo.findByEmail(userDetails.getUsername());
			colObj.setName(name);
			colObj.setDescription(description);
			colObj.setUser(user);
			colObj = collRepo.saveAndFlush(colObj);

		}
		return "successfully added collection";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String showAdd(WebRequest request, Model model) {
		LOGGER.debug("Rendering Add.");

		return ADD;
	}

	@RequestMapping(value = "/collection", method = RequestMethod.GET)
	public String showCollection(WebRequest request, Model model) {
		LOGGER.debug("Rendering Collection.");

		return VIEW_PAGE;
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String showView(WebRequest request, Model model) {
		LOGGER.debug("Rendering View.");

		return VIEW;
	}

	@RequestMapping(value = "/view", method = RequestMethod.POST)
	public String showViewCollection(WebRequest request, Model model) {
		LOGGER.debug("Rendering Collection View.");
		UserDetails userDetails = null;
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		User user = null;
		List<Collection> colObj=null;

		if (!(auth instanceof AnonymousAuthenticationToken)) {
			userDetails = (UserDetails) auth.getPrincipal();
			user = userRepo.findByEmail(userDetails.getUsername());
			colObj = collRepo.findByUserId(user);
			model.addAttribute("result", colObj);
		}
		
		colObj = collRepo.getPublicCollection();
		model.addAttribute("publicCollection", colObj);
				

		LOGGER.debug("user_id = " + user.getId());

		return VIEW;

	}
}