package org.scripps.branch.controller;

import org.scripps.branch.entity.User;
import org.scripps.branch.repository.UserRepository;
import org.scripps.branch.service.PathwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;

@Controller
public class PopulatePathwayFromCPdb {
	
	
	@Autowired
	PathwayService pSer;
	
	@Autowired
	UserRepository userRepo;
	
	@RequestMapping(value = "/populate-pathway", method = RequestMethod.POST)
	public String get(String source, Model model)
			throws Exception {
		UserDetails userDetails = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			userDetails = (UserDetails) auth.getPrincipal();
			User user = userRepo.findByEmail(userDetails.getUsername());
			if(!user.getFirstName().equals("admin")){
				return "redirect:/";
			}
		}
		if(source.equals("cpdb")){
			pSer.importFromFile();
			model.addAttribute("success",true);
		    model.addAttribute("message","Pathways Populated.");
		    return "tutorial/tutorials";
		}
		model.addAttribute("success",false);
	    model.addAttribute("message","Server Error.");
	    return "tutorial/tutorials";
	}
}
