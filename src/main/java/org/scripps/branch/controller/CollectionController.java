package org.scripps.branch.controller;

import java.util.List;

import org.scripps.branch.entity.Collection;
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
import org.springframework.web.context.request.WebRequest;

@Controller
public class CollectionController {

	protected static final String ADD = "user/addCollection";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CollectionController.class);

	protected static final String VIEW = "user/view";

	protected static final String VIEW_PAGE = "user/collection";
	protected static final String VIEW_PUBLIC_COLLECTION = "user/publicCollection";
	@Autowired
	CollectionRepository collRepo;
	@Autowired
	UserRepository userRepo;

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addCollection(@RequestParam("name") String name,
			@RequestParam("description") String description) {

		// /LOGGER.debug("UserID at Add"+user_id);
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
		return "redirect:/";
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
	public String showViewCollection(
			@RequestParam(value = "user_id", required = false) Long user_id,
			WebRequest request, Model model) {
		LOGGER.debug("Rendering Collection View.");
		LOGGER.debug("user_id" + user_id);
		UserDetails userDetails = null;
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		User user = null;
		List<Collection> colObj = null;

		if (!(auth instanceof AnonymousAuthenticationToken)) {
			userDetails = (UserDetails) auth.getPrincipal();
			user = userRepo.findByEmail(userDetails.getUsername());
			LOGGER.debug("user_id" + user_id);
			LOGGER.debug("sign in :" + user.getId());
			if (user_id.longValue() == user.getId()) {
				LOGGER.debug("same user id");
				colObj = collRepo.findByUser(user);
				model.addAttribute("result", colObj);
				colObj = collRepo.getPublicCollection();
				model.addAttribute("publicCollection", colObj);

			} else {
				LOGGER.debug("diff user id");
				user = userRepo.findById(user_id);
				colObj = collRepo.getOnlyPublicCollections(user);
				model.addAttribute("result", colObj);
				colObj = collRepo.getPublicCollection();
				model.addAttribute("publicCollection", colObj);
			}
		}
		return VIEW;
	}
}