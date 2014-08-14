package org.scripps.branch.controller;

import org.scripps.branch.service.PathwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;

@Controller
public class PopulatePathwayFromCPdb {
	
	
	@Autowired
	PathwayService pSer;
	
	@RequestMapping(value = "/populate-pathway", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	public @ResponseBody String get(@RequestBody JsonNode data)
			throws Exception {
		String message = "Specify Source";
		if(data.get("source").asText().equals("cpdb")){
			pSer.importFromFile();
			message = "Successful";
		}
		return message;
	}
}
