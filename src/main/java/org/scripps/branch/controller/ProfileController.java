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
public class ProfileController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProfileController.class);

	protected static final String VIEW_NAME_PROFILEPAGE = "user/profile";

	@Autowired
	UserRepository userRepo;

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public String showHomePage(WebRequest request, Model model) {

		if (request.getParameter("playerid") == null) {
			LOGGER.debug("Rendering profile.");
			UserDetails userDetails = null;
			Authentication auth = SecurityContextHolder.getContext()
					.getAuthentication();
			model.addAttribute("userId", -1);
			model.addAttribute("firstName", "Guest");
			// model.addAttribute("user_experience", null);
			if (!(auth instanceof AnonymousAuthenticationToken)) {
				userDetails = (UserDetails) auth.getPrincipal();
				User user = userRepo.findByEmail(userDetails.getUsername());
				model.addAttribute("userId", user.getId());
				model.addAttribute("firstName", user.getFirstName());
				// model.addAttribute("user_experience", null);
			}
		} else {
			User user = userRepo.findById(Long.valueOf(request
					.getParameter("playerid")));
			model.addAttribute("userId", user.getId());
			model.addAttribute("firstName", user.getFirstName());
		}

		return VIEW_NAME_PROFILEPAGE;
	}
}
