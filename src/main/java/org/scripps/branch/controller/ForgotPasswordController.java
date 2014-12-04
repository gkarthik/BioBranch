package org.scripps.branch.controller;

import java.util.List;

import javax.validation.Valid;

import org.scripps.branch.entity.Tutorial;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.forms.ForgotPasswordForm;
import org.scripps.branch.entity.forms.TutorialForm;
import org.scripps.branch.repository.DatasetRepository;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
public class ForgotPasswordController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ForgotPasswordController.class);
	
	String fPassPage="user/forgotPassword";

	@Autowired
	DatasetRepository dRepo;
	
	@Autowired
	UserRepository uRepo;

	@RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
	public String sendPage(WebRequest request, Model model) {
		model.addAttribute("forgotPassword", new ForgotPasswordForm());
		return fPassPage;
	}
	
	@RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public String saveTutorialAction(
            @Valid @ModelAttribute("forgotPassword") ForgotPasswordForm fPassForm,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return fPassPage;
        }
        User u = uRepo.findByEmail(fPassForm.getEmail());
        if(u!=null){
        	model.addAttribute("success",true);
            model.addAttribute("msg","An email has been sent with instructions.");
        } else {
        	model.addAttribute("success", false);
            model.addAttribute("msg","The email id you entered does not seem to exist in our database. Please use the email id you registered your account with.");
        }
        return fPassPage;
    }
}