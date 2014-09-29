package org.scripps.branch.controller;

import javax.validation.Valid;

import org.scripps.branch.entity.Tutorial;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.entity.forms.RegistrationForm;
import org.scripps.branch.entity.forms.TutorialForm;
import org.scripps.branch.repository.TutorialRepository;
import org.scripps.branch.repository.UserRepository;
import org.scripps.branch.service.SocialMediaService;
import org.scripps.branch.service.UserService;
import org.scripps.branch.utilities.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.WebRequest;

import Validation.DuplicateEmailException;

@Controller
public class TutorialController {

	protected static final String SaveTutorialPage = "tutorial/newtutorial";
	protected static final String tutorialPage = "tutorial/tutorials";

	private static final Logger logger = LoggerFactory
			.getLogger(TutorialController.class);
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	TutorialRepository tRepo;
	
	@RequestMapping(value = "/tutorial", method = RequestMethod.GET)
    public String showTutorials(WebRequest request, Model model) {
		UserDetails userDetails = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			userDetails = (UserDetails) auth.getPrincipal();
			User user = userRepo.findByEmail(userDetails.getUsername());
			if(!user.getFirstName().equals("admin")){
				return "user/login";
			}
		}		
		model.addAttribute("tutorials", tRepo.findAll());
        return tutorialPage;
    }
	
	@RequestMapping(value = "/tutorial/new", method = RequestMethod.GET)
    public String saveTutorialPage(Model model) {
        model.addAttribute("tutorial", new TutorialForm());
        return SaveTutorialPage;
    }
 
    @RequestMapping(value = "/tutorial/new", method = RequestMethod.POST)
    public String saveTutorialAction(
            @Valid @ModelAttribute("tutorial") TutorialForm tutorial,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return SaveTutorialPage;
        }
        Tutorial t = new Tutorial();
        t.setTitle(tutorial.getTitle());
        t.setDescription(tutorial.getDescription());
        t.setUrl(tutorial.getUrl());
        tRepo.saveAndFlush(t);
        model.addAttribute("tutorial", t);
        model.addAttribute("message","success");
        return "redirect:/tutorial/";
    }
}