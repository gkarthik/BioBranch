package org.scripps.branch.controller;

import java.util.HashMap;

import org.scripps.branch.entity.User;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.DatasetMap;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class HomeController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(HomeController.class);

	protected static final String VIEW_NAME_HOMEPAGE = "index";

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	DatasetMap wekaMap;
	
	@Autowired
	DatasetRepository dRepo;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showHomePage(WebRequest request, Model model) {
		LOGGER.debug("Rendering homepage.");
		UserDetails userDetails = null;
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		userDetails = (UserDetails) auth.getPrincipal();
		User user = userRepo.findByEmail(userDetails.getUsername());
		model.addAttribute("userId", user.getId());
		model.addAttribute("firstName", user.getFirstName());
		if(request.getParameter("dataset")!=null){
			long id = Long.valueOf(request.getParameter("dataset"));
			Weka weka = wekaMap.getWeka(id);
			if(weka==null){
				return "redirect:/datasets";
			}
			model.addAttribute("pos", weka.getOrigTrain().classAttribute().value(1));
			model.addAttribute("neg", weka.getOrigTrain().classAttribute().value(0));
			model.addAttribute("dataset", dRepo.findById(id));
		} else {
			return "redirect:/datasets";
		}
		LOGGER.debug("Role {}",user.getRole());
		return VIEW_NAME_HOMEPAGE;
	}
	
	@RequestMapping(value = "/access-denied", method = RequestMethod.GET)
	public String showAccessDeniedPage(WebRequest request, Model model) {
		return "error/AccessDenied";
	}
}