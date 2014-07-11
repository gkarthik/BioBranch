package org.scripps.branch.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.scripps.branch.classifier.ManualTree;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.WekaObject;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.CustomFeatureRepository;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.TreeRepository;
import org.scripps.branch.service.CustomFeatureService;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.scripps.branch.viz.JsonTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class MetaServerController {

	@Autowired
	private WekaObject weka;

	@Autowired
	@Qualifier("attributeRepository")
	private AttributeRepository attr;

	@Autowired
	@Qualifier("treeRepository")
	private TreeRepository treeRepo;

	@Autowired
	@Qualifier("featureRepository")
	private FeatureRepository featureRepo;

	@Autowired
	private HibernateAwareObjectMapper mapper;
	
	@Autowired
	private CustomFeatureService cfeatureService;
	
	@Autowired
	private CustomFeatureRepository cfeatureRepo;
	
	@RequestMapping(value = "/MetaServer", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	public @ResponseBody String scoreOrSaveTree(@RequestBody JsonNode data,
			HttpServletRequest request) throws Exception {
		String command = data.get("command").asText();
		String result_json = "";
		if (command.equals("scoretree") || command.equals("savetree")) {
			result_json = scoreSaveManualTree(data);
		} else if (command.equals("get_clinical_features")) {
			result_json = getClinicalFeatures(data);
		} else if(command.contains("custom_feature_")) {
			if(command.equals("custom_feature_create")){
				HashMap mp = cfeatureService.findOrCreateCustomFeature(data.get("name").asText(), data.get("expression").asText(), data.get("description").asText(), data.get("user_id").asLong(), data.get("dataset").asText(), weka.getWeka());
				result_json = mapper.writeValueAsString(mp);
			} else if(command.equals("custom_feature_search")) {
				List<CustomFeature> cfList = cfeatureRepo.searchCustomFeatures(data.get("query").asText());
				result_json = mapper.writeValueAsString(cfList);
			}
		}
		return result_json;
	}
	
	public String scoreSaveManualTree(JsonNode data) throws Exception {
		Weka wekaObj = WekaObject.getWeka();
		JsonTree t = new JsonTree();
		ManualTree readtree = new ManualTree();
		LinkedHashMap<String, Classifier> custom_classifiers = new LinkedHashMap<String, Classifier>();
		readtree = t.parseJsonTree(wekaObj, data.get("treestruct"),
				data.get("dataset").asText(), custom_classifiers, attr);
		Evaluation eval = new Evaluation(wekaObj.getTrain());
		eval.evaluateModel(readtree, wekaObj.getTrain());
		JsonNode treenode = readtree.getJsontree();
		HashMap distributionData = readtree.getDistributionData();
		int numnodes = readtree.numNodes();
		ObjectNode result = mapper.createObjectNode();
		result.put("pct_correct", eval.pctCorrect());
		result.put("size", numnodes);
		result.put("text_tree", readtree.toString());
		result.put("treestruct", treenode);
		result.put("distribution_data", mapper.valueToTree(distributionData));
		result.put("treestruct", treenode);
		String result_json = "";
		try {
			result_json = mapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Feature> fList = new ArrayList();
		t.getFeatures(treenode, fList, featureRepo);
		Tree newTree = new Tree();
		newTree.setComment(data.get("comment").asText());
		Date date = new Date();
		newTree.setCreated(new DateTime(date.getTime()));
		newTree.setFeatures(fList);
		newTree.setJson_tree(result_json);
		newTree.setPrivate_tree(false);
		newTree.setUser(null);
		newTree.setUser_saved(false);
		treeRepo.saveAndFlush(newTree);
		return result_json;
	}
	
	public String getClinicalFeatures(JsonNode data) {
		ArrayList<Feature> fList = featureRepo.getMetaBricClinicalFeatures();
		// System.out.println(fList.size());
		String result_json = "";
		try {
			result_json = mapper.writeValueAsString(fList);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result_json;
	}
}
