package org.scripps.branch.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.validation.Valid;

import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.DatasetRequest;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.forms.DatasetRequestForm;
import org.scripps.branch.entity.forms.PasswordResetForm;
import org.scripps.branch.repository.CollectionRepository;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.DatasetRequestRepository;
import org.scripps.branch.repository.UserRepository;
import org.scripps.branch.service.MailService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@Controller
public class DatasetController {

	@Autowired
	DatasetRepository dRepo;
	
	@Autowired
	DatasetRequestRepository drRepo;
	
	@Autowired
	MailService mailService;

	@RequestMapping(value = "/datasets", method = RequestMethod.GET)
	public String addCollection(WebRequest request, Model model) {
		List<Dataset> dList = dRepo.findAll();
		 Collections.sort(dList,new Comparator<Dataset>() {
				@Override
				public int compare(Dataset d1, Dataset d2) {
					if(d2.getCollection().getId()!=d1.getCollection().getId()){
						return (int) (d2.getCollection().getId()-d1.getCollection().getId());
					} else {
						return (d2.getName().compareTo(d1.getName()));
					}
				}
	        });
		model.addAttribute("datasets", dList);
		return "user/Datasets";
	}
	
	@RequestMapping(value = "/request-dataset", method = RequestMethod.GET)
	public String requestDataset(WebRequest request, Model model) {
		model.addAttribute("requestDataset", new DatasetRequestForm());
		return "requestDataset";
	}
	
	@RequestMapping(value = "/request-dataset", method = RequestMethod.POST)
	public String acceptRequest(@Valid @ModelAttribute("requestDataset") DatasetRequestForm requestDataset,
            BindingResult bindingResult, WebRequest request, Model model) {
		if (bindingResult.hasErrors()) {
            return "requestDataset";
        }
		DatasetRequest dr = new DatasetRequest(requestDataset.getDataDescription(), requestDataset.getReason(), requestDataset.getPrivateToken(), requestDataset.getEmail(), requestDataset.getFirstName(), requestDataset.getLastName());
		dr = drRepo.saveAndFlush(dr);
		if(dr!=null){
			mailService.startSendDatasetRequestMail(dr);
        	model.addAttribute("success",true);
            model.addAttribute("msg","An email has been sent with instructions.");
		} else {
			model.addAttribute("success",false);
            model.addAttribute("msg","Sending Request failed. Please try again later. ");
		}
		model.addAttribute("requestDataset", new DatasetRequestForm());
		return "requestDataset";
	}
	
	
}