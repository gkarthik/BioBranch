package org.scripps.branch.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.scripps.branch.classifier.ManualTree;
import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Pathway;
import org.scripps.branch.entity.Score;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.WekaObject;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.CustomClassifierRepository;
import org.scripps.branch.repository.CustomFeatureRepository;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.PathwayRepository;
import org.scripps.branch.repository.ScoreRepository;
import org.scripps.branch.repository.TreeRepository;
import org.scripps.branch.repository.UserRepository;
import org.scripps.branch.service.CustomClassifierService;
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
	UserRepository userRepo;

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
	
	@Autowired
	private CustomClassifierRepository cClassifierRepo;
	
	@Autowired
	private CustomClassifierService cClassifierService;
	
	@Autowired
	private PathwayRepository pathwayRepo;
	
	@Autowired
	private ScoreRepository scoreRepo;
	
	@RequestMapping(value = "/MetaServer", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	public @ResponseBody String scoreOrSaveTree(@RequestBody JsonNode data,
			HttpServletRequest request) throws Exception {
		String command = data.get("command").asText();
		String result_json = "";
		if (command.equals("scoretree") || command.equals("savetree")) {
			result_json = scoreSaveManualTree(data);
		} else if(command.contains("get_tree")){
			if(command.equals("get_tree_by_id")){
				Tree t = treeRepo.findById(data.get("treeid").asLong());
				result_json = mapper.writeValueAsString(t);
			} else if(command.equals("get_trees_by_search")){
				List<?> tList = treeRepo.getTreesBySearch(data.get("query").asText());
				System.out.println(tList.size());
				result_json = mapper.writeValueAsString(tList);
			}
		} else if (command.equals("get_clinical_features")) {
			result_json = getClinicalFeatures(data);
		} else if(command.contains("custom_feature_")) {
			if(command.equals("custom_feature_create")){
				HashMap mp = cfeatureService.findOrCreateCustomFeature(data.get("name").asText(), data.get("expression").asText(), data.get("description").asText(), data.get("user_id").asLong(), data.get("dataset").asText(), weka.getWeka());
				result_json = mapper.writeValueAsString(mp);
			} else if(command.equals("custom_feature_search")) {
				List<CustomFeature> cfList = cfeatureRepo.searchCustomFeatures(data.get("query").asText());
				result_json = mapper.writeValueAsString(cfList);
			} else if(command.equals("custom_feature_testcase")) {
				HashMap mp = cfeatureService.getTestCase(data.get("id").asText(), weka.getWeka());
				result_json = mapper.writeValueAsString(mp);
			}
		} else if(command.contains("custom_classifier_")) {
			if(command.equals("custom_classifier_create")){
				List entrezIds = new ArrayList();
				for(JsonNode el : data.path("unique_ids")){
					entrezIds.add(el.asText());
				}
				String name = data.get("name").asText();
				String description = data.get("description").asText();
				int player_id = data.get("user_id").asInt();
				int classifierType = data.get("type").asInt();
				String dataset = data.get("dataset").asText();
				HashMap mp = cClassifierService.getOrCreateClassifier(entrezIds, classifierType, name, description, player_id, weka.getWeka(), dataset, weka.getCustomClassifierObject());
				result_json = mapper.writeValueAsString(mp);
			} else if(command.equals("custom_classifier_search")) {
				List<CustomClassifier> cclist = cClassifierRepo.searchCustomClassifiers(data.get("query").asText());
				result_json = mapper.writeValueAsString(cclist);
			} else if(command.equals("custom_classifier_getById")) {
				HashMap mp = cClassifierService.getClassifierDetails(data.get("id").asLong(), data.get("dataset").asText(), weka.getCustomClassifierObject());
				result_json = mapper.writeValueAsString(mp);
			}
		} else if(command.contains("pathway")){
			if(command.equals("search_pathways")){
				List<Pathway> pList = pathwayRepo.searchPathways(data.get("query").asText());
				result_json = mapper.writeValueAsString(pList);
			} else if(command.equals("get_genes_of_pathway")) {
				Pathway p = pathwayRepo.findByName(data.get("pathway_name").asText());
				List<Feature> fList = p.getFeatures();
				result_json = mapper.writeValueAsString(fList);
			}
		}
		return result_json;
	}
	
	public String scoreSaveManualTree(JsonNode data) throws Exception {
		Weka wekaObj = weka.getWeka();
		JsonTree t = new JsonTree();
		ManualTree readtree = new ManualTree();
		LinkedHashMap<String, Classifier> custom_classifiers = weka.getCustomClassifierObject();
		readtree = t.parseJsonTree(wekaObj, data.get("treestruct"),
				data.get("dataset").asText(), custom_classifiers, attr, cClassifierService);
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
		Score newScore = new Score();
		double nov = 0;
		newScore.setNovelty(nov);
		newScore.setDataset(data.get("dataset").asText());
		newScore.setPct_correct(eval.pctCorrect());
		newScore.setSize(numnodes);
		double score = ((750 * (1 / numnodes)) + (500 * nov) + (1000 * eval.pctCorrect()));
		newScore.setScore(score);
		newScore = scoreRepo.saveAndFlush(newScore);
		List<Feature> fList = new ArrayList();
		t.getFeatures(treenode, fList, featureRepo);
		Tree newTree = new Tree();
		newTree.setComment(data.get("comment").asText());
		Date date = new Date();
		newTree.setCreated(new DateTime(date.getTime()));
		newTree.setFeatures(fList);
		newTree.setJson_tree(result_json);
		newTree.setPrivate_tree(false);
		User user = userRepo.findById(data.get("player_id").asLong());
		newTree.setUser(user);
		newTree.setUser_saved(false);
		newTree.setPrivate_tree(false);
		newTree.setScore(newScore);
		if(data.get("command").asText().equals("savetree")){
			newTree.setUser_saved(true);
			Tree prevTree = treeRepo.findById(data.get("previous_tree_id").asLong());
			newTree.setPrev_tree_id(prevTree);
			int privateflag = data.get("privateflag").asInt();
			if(privateflag==1){
				newTree.setPrivate_tree(true);
			}
		}
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
