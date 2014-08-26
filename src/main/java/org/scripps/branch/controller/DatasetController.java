package org.scripps.branch.controller;

import java.util.List;

import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.User;
import org.scripps.branch.repository.CollectionRepository;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@Controller
public class DatasetController {

	@Autowired
	DatasetRepository dRepo;

	@RequestMapping(value = "/datasets", method = RequestMethod.GET)
	public String addCollection(WebRequest request, Model model) {
		List<Dataset> dList = dRepo.findAll();
		model.addAttribute("datasets", dList);
		return "user/Datasets";
	}
}