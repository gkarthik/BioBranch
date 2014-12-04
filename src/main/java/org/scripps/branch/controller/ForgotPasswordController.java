package org.scripps.branch.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.scripps.branch.entity.Token;
import org.scripps.branch.entity.Tutorial;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.forms.ForgotPasswordForm;
import org.scripps.branch.entity.forms.PasswordResetForm;
import org.scripps.branch.entity.forms.TutorialForm;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.TokenRepository;
import org.scripps.branch.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
public class ForgotPasswordController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ForgotPasswordController.class);
	
	String fPassPage="user/forgotPassword";
	String resetPassPage="user/resetPassword";
	
	private Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
	
	@Autowired
	private PasswordEncoder pEncoder;

	@Autowired
	DatasetRepository dRepo;
	
	@Autowired
	UserRepository uRepo;
	
	@Autowired
	TokenRepository tRepo;

	@RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
	public String sendPage(WebRequest request, Model model) {
		model.addAttribute("forgotPassword", new ForgotPasswordForm());
		return fPassPage;
	}
	
	@RequestMapping(value = "/authenticate/{uid}", method = RequestMethod.GET)
	public String authenticateToken(@PathVariable("uid") String uid, WebRequest request, Model model) {		
		if(tRepo.findByUid(uid)==null){
			return "redirect:/login";
		}
		model.addAttribute("passwordReset", new PasswordResetForm());
		return resetPassPage;
	}
	
	@RequestMapping(value = "/authenticate/{uid}", method = RequestMethod.POST)
    public String resetPassword(@Valid @ModelAttribute("passwordReset") PasswordResetForm resetPassForm,
    		@PathVariable("uid") String uid,
            BindingResult bindingResult, Model model) {
		Token t = tRepo.findByUid(uid);
		if(t==null){
			return "redirect:/login";
		}
        if (bindingResult.hasErrors()) {
            return resetPassPage;
        }
        User u = t.getUser();
        u.setPassword(pEncoder.encode(resetPassForm.getPassword()));
        uRepo.saveAndFlush(u);
        model.addAttribute("msg", "Password has been reset. You can now login with new password.");
		return resetPassPage;
	}
	
	@RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public String createToken(
            @Valid @ModelAttribute("forgotPassword") ForgotPasswordForm fPassForm,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return fPassPage;
        }
        User u = uRepo.findByEmail(fPassForm.getEmail());
        Date date= new Date();
        if(u!=null){
        	Token oldToken = u.getToken();
        	Token t = new Token();
        	String uid = new Timestamp(date.getTime())+u.getPassword();
        	LOGGER.debug("UID {}", passwordEncoder.encodePassword(uid, "forgot-branch"));
        	t.setUid(passwordEncoder.encodePassword(uid, "forgot-branch"));
        	t = tRepo.saveAndFlush(t);
        	u.setToken(t);
        	uRepo.saveAndFlush(u);
        	LOGGER.debug("Token {}", u.getToken());
        	if(oldToken!=null){
        		tRepo.delete(oldToken);
        	}
        	model.addAttribute("success",true);
            model.addAttribute("msg","An email has been sent with instructions.");
        } else {
        	model.addAttribute("success", false);
            model.addAttribute("msg","The email id you entered does not seem to exist in our database. Please use the email id you registered your account with.");
        }
        return fPassPage;
    }
}