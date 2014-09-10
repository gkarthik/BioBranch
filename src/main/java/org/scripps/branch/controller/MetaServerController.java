package org.scripps.branch.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;
import org.scripps.branch.classifier.ManualTree;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Component;
import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.CustomSet;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Pathway;
import org.scripps.branch.entity.Score;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.DatasetMap;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.CustomClassifierRepository;
import org.scripps.branch.repository.CustomFeatureRepository;
import org.scripps.branch.repository.CustomSetRepository;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.PathwayRepository;
import org.scripps.branch.repository.ScoreRepository;
import org.scripps.branch.repository.SerializedCustomClassifierRepository;
import org.scripps.branch.repository.TreeRepository;
import org.scripps.branch.repository.UserRepository;
import org.scripps.branch.service.CustomClassifierService;
import org.scripps.branch.service.CustomFeatureService;
import org.scripps.branch.service.FeatureService;
import org.scripps.branch.service.TreeService;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.scripps.branch.viz.JsonTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class MetaServerController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MetaServerController.class);

	@Autowired
	@Qualifier("attributeRepository")
	private AttributeRepository attr;

	@Autowired
	private CustomClassifierRepository cClassifierRepo;

	@Autowired
	private CustomClassifierService cClassifierService;

	@Autowired
	private CustomFeatureRepository cfeatureRepo;

	@Autowired
	private CustomFeatureService cfeatureService;

	@Autowired
	@Qualifier("featureRepository")
	private FeatureRepository featureRepo;

	@Autowired
	private HibernateAwareObjectMapper mapper;

	@Autowired
	private PathwayRepository pathwayRepo;

	@Autowired
	private ScoreRepository scoreRepo;

	@Autowired
	@Qualifier("treeRepository")
	private TreeRepository treeRepo;

	@Autowired
	private TreeService treeService;
	
	@Autowired
	private AttributeRepository attrRepo;
	
	@Autowired
	private CustomSetRepository customSetRepo;
	
	@Autowired
	private SerializedCustomClassifierRepository sccRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	private DatasetMap weka;
	
	@Autowired
	private DatasetRepository dataRepo;
	
	@Autowired
	private FeatureService fSer;

	@RequestMapping(value = "/MetaServer", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	public @ResponseBody String metaServerAPI(@RequestBody JsonNode data)
			throws Exception {
		String command = data.get("command").asText();
		String result_json = "";
		if (command.equals("scoretree") || command.equals("savetree")) {
			result_json = scoreSaveManualTree(data);
		} else if (command.contains("get_tree")) {
			if (command.equals("get_tree_by_id")) {
				Tree t = treeRepo.findById(data.get("treeid").asLong());
				result_json = mapper.writeValueAsString(t);
			} else if (command.equals("get_trees_by_search")) {
				List<Tree> tList = treeRepo.getTreesBySearch(data.get("query")
						.asText());
				result_json = mapper.writeValueAsString(tList);
			} else if (command.equals("get_trees_user_id")) {
				User user = userRepo.findById(data.get("user_id").asLong());
				UserDetails userDetails = null;
				Boolean privateTrees = false;
				Authentication auth = SecurityContextHolder.getContext()
						.getAuthentication();
				if (!(auth instanceof AnonymousAuthenticationToken)
						&& user != null) {
					userDetails = (UserDetails) auth.getPrincipal();
					User authUser = userRepo.findByEmail(userDetails
							.getUsername());
					if (authUser.getId() == user.getId()) {
						privateTrees = true;
					} else {
						privateTrees = false;
					}
				} else {
					privateTrees = false;
				}
				List<Tree> tList = new ArrayList();
				if (privateTrees == true) {
					tList = treeRepo.findByUser(user);
				} else {
					tList = treeRepo.getByOtherUser(user);
				}
				int ctr = 1;
				for (Tree t : tList) {
					t.setRank(ctr);
					ctr++;
				}
				result_json = mapper.writeValueAsString(tList);
			} else if (command.equals("get_trees_with_range")) {
				List<Tree> tList = treeRepo.getAllTrees();
				int ctr = 1;
				for (Tree t : tList) {
					t.setRank(ctr);
					ctr++;
				}
				result_json = mapper.writeValueAsString(tList);
			}
		} else if (command.equals("get_clinical_features")) {
			Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt()));
			List<Feature> fList = featureRepo.getNonGeneFeatures(d);
			List<String> entrezIds = new ArrayList<String>();
			for(Feature f: fList){
				entrezIds.add(f.getUnique_id());
			}
			result_json = mapper.writeValueAsString(fSer.rankFeatures(null/*getReqInstances(data)*/, entrezIds, d));
		} else if (command.contains("custom_feature_")) {
			if (command.equals("custom_feature_create") || command.equals("custom_feature_preview")) {
				List<Component> cList = new ArrayList<Component>();
				Component c;Boolean toAdd = false;
				for (JsonNode el : data.path("components")) {
					toAdd = false;
					c = new Component();
					if(el.get("id").asText().contains("custom_feature")){
						c.setCfeature(cfeatureRepo.findById(Long.valueOf(el.get("id").asText().replace("custom_feature",""))));
					} else {
						c.setFeature(featureRepo.findByUniqueId(el.get("id").asText()));
					}
					c.setUpperLimit(null);
					c.setLowerLimit(null);
					if(!el.get("uLimit").isNull()){
						LOGGER.debug("NOT NULL");
						c.setUpperLimit(el.get("uLimit").asLong());
						toAdd = true;
					}
					if(!el.get("uLimit").isNull()){
						c.setLowerLimit(el.get("lLimit").asLong());
						toAdd = true;
					}
					if(toAdd && command.equals("custom_feature_preview")){
						cList.add(c);
					} else if(command.equals("custom_feature_create")) {
						cList.add(c);
					}
				}
				Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt()));
				HashMap mp = new HashMap();
				if(command.equals("custom_feature_create")){
					mp = cfeatureService.findOrCreateCustomFeature(data
							.get("name").asText(), data.get("expression").asText(),
							data.get("description").asText(), data.get("user_id")
									.asLong(), d, cList, weka
									.getWeka(d.getId()));
				} else if(command.equals("custom_feature_preview")) {
					ArrayList l = cfeatureService.previewCustomFeature(data.get("name").asText(), data.get("expression").asText(),
							 cList, weka.getWeka(d.getId()).getOrigTrain(), d);
					mp.put("isNominal", false);
					mp.put("dataArray", l);
				}
				result_json = mapper.writeValueAsString(mp);
			} else if (command.equals("custom_feature_search")) {
				List<CustomFeature> cfList = cfeatureRepo
						.searchCustomFeatures(data.get("query").asText());
				result_json = mapper.writeValueAsString(cfList);
			} else if (command.equals("custom_feature_testcase")) {
				Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt()));
//				HashMap mp = cfeatureService.getTestCase(data.get("id")
//						.asText(), weka.getWeka(d.getId()));
//				result_json = mapper.writeValueAsString(mp);			
			} 
		} else if (command.contains("custom_classifier_")) {
			if (command.equals("custom_classifier_create")) {
				List entrezIds = new ArrayList();
				for (JsonNode el : data.path("unique_ids")) {
					entrezIds.add(el.asText());
				}
				String name = data.get("name").asText();
				String description = data.get("description").asText();
				int player_id = data.get("user_id").asInt();
				int classifierType = data.get("type").asInt();
				String dataset = data.get("dataset").asText();
				Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt()));
				HashMap mp = cClassifierService.getOrCreateClassifier(
						entrezIds, classifierType, name, description,
						player_id, weka.getMap(), d,
						weka.getCustomClassifierObject());
				result_json = mapper.writeValueAsString(mp);
			} else if (command.equals("custom_classifier_search")) {
				List<CustomClassifier> cclist = cClassifierRepo
						.searchCustomClassifiers(data.get("query").asText());
				result_json = mapper.writeValueAsString(cclist);
			} else if (command.equals("custom_classifier_getById")) {
				Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt()));
				HashMap mp = cClassifierService.getClassifierDetails(
						data.get("id").asLong(), d,
						weka.getCustomClassifierObject());
				result_json = mapper.writeValueAsString(mp);
			}
		} else if(command.contains("custom_set_")){
			if(command.equals("custom_set_create")){
				CustomSet c = new CustomSet();
				c.setConstraints(data.get("constraints").toString());
				List<Feature> fList = new ArrayList<Feature>();
				for(JsonNode el : data.path("unique_ids")){
					fList.add(featureRepo.findByUniqueId(el.asText()));
				}
				c.setFeatures(fList);
				User user = userRepo.findById(data.get("player_id").asLong());
				c.setUser(user);
				c = customSetRepo.saveAndFlush(c);
				result_json = mapper.writeValueAsString(c);
			} else if(command.equals("custom_set_get")) {
				CustomSet c = new CustomSet();
				c = customSetRepo.findById(data.get("customset_id").asLong());
				result_json = mapper.writeValueAsString(c);
			}
		} else if (command.contains("pathway")) {
			if (command.equals("search_pathways")) {
				List<Pathway> pList = pathwayRepo.searchPathways(data.get(
						"query").asText());
				result_json = mapper.writeValueAsString(pList);
			} else if (command.equals("get_genes_of_pathway")) {
				Pathway p = pathwayRepo.findById(data.get("pathway_id").asLong());
				Dataset d = dataRepo.findById(data.get("dataset").asLong());
				List<Feature> fList = p.getFeatures();
				Boolean exists = false;
				List<String> entrezIds = new ArrayList<String>();
				for(Feature f: fList){
					for(Attribute a: f.getAttributes()){
						if(a.getDataset().getId() == d.getId()){
							exists = true;
							entrezIds.add(f.getUnique_id());
						}
					}
					if(!exists){
						fList.remove(f);
					}
				}
				result_json = mapper.writeValueAsString(fSer.rankFeatures(null/*getReqInstances(data)*/, entrezIds, d));
			}
		} else if (command.contains("rank_")){
			if(command.equals("rank_attributes")){
				Dataset d = dataRepo.findById(data.get("dataset").asLong());
				List<String> entrezIds = new ArrayList<String>();
				for(JsonNode el : data.path("unique_ids")){
					entrezIds.add(el.asText());
					LOGGER.debug(el.asText());
				}
				result_json = mapper.writeValueAsString(fSer.rankFeatures(null/*getReqInstances(data)*/, entrezIds, d));
			}
		} else if(command.contains("get_dataset")) {
			if(command.equals("get_dataset_training")){
				Dataset d = dataRepo.findById(data.get("dataset").asLong());
				List<Dataset> dList= dataRepo.findByCollection(d.getCollection());
				for(Dataset temp: dList){
					if(temp.equals(d)){
						dList.remove(d);
						break;
					}
				}
				result_json = mapper.writeValueAsString(dList);
			}
		} else if (command.equals("validate_features")) {
			Dataset d = dataRepo.findById(data.get("dataset").asLong());
			HashMap mp = new HashMap();
			mp.put("genes", true);
			mp.put("non_genes", true);
			long nonGeneCount = featureRepo.getCountOfNonGeneFeature(d);
			long total_count = featureRepo.getCountOfFeatures(d);
			if(nonGeneCount == 0){
				mp.put("non_genes", false);
			} else if(total_count==nonGeneCount){
				mp.put("genes", false);
			}
			List<Attribute> aList = attrRepo.findByDatasetOrderByRelieffDesc(d);
			mp.put("infoGainMax", aList.get(0).getRelieff());
			mp.put("infoGainMin", aList.get(aList.size()-1).getRelieff());
			result_json = mapper.writeValueAsString(mp);
		}
		return result_json;
	}
	
	public Instances getReqInstances(JsonNode data) throws Exception{
		Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt()));
		Weka wekaObj = weka.getWeka(d.getId());
		JsonTree t = new JsonTree();
		ManualTree readtree = new ManualTree();
		LinkedHashMap<String, Classifier> custom_classifiers = weka
				.getCustomClassifierObject();
		Instances train = wekaObj.getOrigTrain();
		switch (data.get("testOptions").get("value").asInt()) {
		case 0:
			wekaObj.setTrain(train);
			wekaObj.setTest(train);
			break;
		case 1:
			wekaObj.setTrain(train);
			long testsetid = data.get("testsetid").asLong();
			wekaObj.setTest(weka.getWeka(testsetid).getOrigTrain());
			break;
		case 2:
			float limitPercent = (data.get("testOptions").get("percentSplit")
					.asLong()) / (float) 100;
			Instances[] classLimits = wekaObj.getInstancesInClass();
			float numLimit = 0;
			numLimit = limitPercent * train.numInstances();
			numLimit = Math.round(numLimit);
			Instances newTrain = new Instances(train, Math.round(numLimit));
			Instances newTest = new Instances(train, train.numInstances()
					- Math.round(numLimit));
			for (int j = 0; j < classLimits.length; j++) {
				numLimit = limitPercent * classLimits[j].numInstances();
				for (int i = 0; i < classLimits[j].numInstances(); i++) {
					if (i == 0) {
						classLimits[j].randomize(new Random(1));
					}
					if (classLimits[j].instance(i) != null) {
						if (i <= numLimit) {
							newTrain.add(classLimits[j].instance(i));
						} else {
							newTest.add(classLimits[j].instance(i));
						}
					}
				}
			}
			wekaObj.setTrain(newTrain);
			wekaObj.setTest(newTest);
			break;
		}
		readtree = t.parseJsonTree(wekaObj, data.get("treestruct"),
				d, custom_classifiers, attr,
				cClassifierService, customSetRepo, d);
		LOGGER.debug(String.valueOf(readtree.getRequiredInst().numInstances()));
		return readtree.getRequiredInst();
	}
	
	public String scoreSaveManualTree(JsonNode data) throws Exception {
		Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt()));
		Weka wekaObj = weka.getWeka(d.getId());
		JsonTree t = new JsonTree();
		ManualTree readtree = new ManualTree();
		LinkedHashMap<String, Classifier> custom_classifiers = weka
				.getCustomClassifierObject();
		Evaluation eval = new Evaluation(wekaObj.getTest());
		Instances train = wekaObj.getOrigTrain();
		switch (data.get("testOptions").get("value").asInt()) {
		case 0:
			wekaObj.setTrain(train);
			wekaObj.setTest(train);
			break;
		case 1:
			wekaObj.setTrain(train);
			long testsetid = data.get("testOptions").get("testsetid").asLong();
			wekaObj.setTest(weka.getWeka(testsetid).getOrigTrain());
			break;
		case 2:
			float limitPercent = (data.get("testOptions").get("percentSplit")
					.asLong()) / (float) 100;
			Instances[] classLimits = wekaObj.getInstancesInClass();
			float numLimit = 0;
			numLimit = limitPercent * train.numInstances();
			numLimit = Math.round(numLimit);
			Instances newTrain = new Instances(train, Math.round(numLimit));
			Instances newTest = new Instances(train, train.numInstances()
					- Math.round(numLimit));
			for (int j = 0; j < classLimits.length; j++) {
				numLimit = limitPercent * classLimits[j].numInstances();
				for (int i = 0; i < classLimits[j].numInstances(); i++) {
					if (i == 0) {
						classLimits[j].randomize(new Random(1));
					}
					if (classLimits[j].instance(i) != null) {
						if (i <= numLimit) {
							newTrain.add(classLimits[j].instance(i));
						} else {
							newTest.add(classLimits[j].instance(i));
						}
					}
				}
			}
			wekaObj.setTrain(newTrain);
			wekaObj.setTest(newTest);
			break;
		}
		readtree = t.parseJsonTree(wekaObj, data.get("treestruct"),
				d, custom_classifiers, attr,
				cClassifierService, customSetRepo, d);
		eval.evaluateModel(readtree, wekaObj.getTest());
		JsonNode cfmatrix = mapper.valueToTree(eval.confusionMatrix());
		JsonNode treenode = readtree.getJsontree();
		HashMap distributionData = readtree.getDistributionData();
		//get Attribute Data from instances
		ArrayList instanceData = new ArrayList();
		Instances reqInstances = readtree.getRequiredInst();
		double[] values;
		List<Integer> attrIndexes = new ArrayList<Integer>();
		List<Attribute> attr;
		int attrIndex = 0;
		for (JsonNode el : data.path("pickedAttrs")) {
			attr = new ArrayList<Attribute>();
			attr = attrRepo.findByFeatureUniqueId(el.asText(), d);
			for(Attribute a : attr){	
				attrIndex = reqInstances.attribute(a.getName()).index();
			}
			attrIndexes.add(attrIndex);
		}
		for(int i=0;i<reqInstances.numInstances();i++){
			values = new double[3];
			for(int j=0;j<attrIndexes.size();j++){
				values[j] = reqInstances.instance(i).value(attrIndexes.get(j));
			}
			values[2] = reqInstances.instance(i).classValue();
			instanceData.add(values);
		}
		int numnodes = readtree.numNodes();
		HashMap mp = new HashMap();
		t.getFeatures(treenode, mp, featureRepo, cfeatureRepo, cClassifierRepo,
				treeRepo, customSetRepo, sccRepo);
		User user = userRepo.findById(data.get("player_id").asLong());
		Score newScore = new Score();
		double nov = 0;
		List<Feature> fList = (List<Feature>) mp.get("fList");
		List<CustomFeature> cfList = (List<CustomFeature>) mp.get("cfList");
		List<CustomClassifier> ccList = (List<CustomClassifier>) mp.get("ccList");
		List<Tree> tList = (List<Tree>) mp.get("tList");
		List<CustomSet> csList = (List<CustomSet>) mp.get("csList");
		nov = treeService
				.getUniqueIdNovelty(fList, cfList, ccList, tList, user);
		ObjectNode result = mapper.createObjectNode();
		result.put("pct_correct", eval.pctCorrect());
		result.put("size", numnodes);
		result.put("novelty", nov);
		result.put("confusion_matrix", cfmatrix);
		result.put("text_tree", readtree.toString());
		result.put("treestruct", treenode);
		result.put("distribution_data", mapper.valueToTree(distributionData));
		if(data.path("pickedAttrs").size()>0){
			result.put("instances_data", mapper.valueToTree(instanceData));
		}
		result.put("treestruct", treenode);
		String result_json = "";
		try {
			result_json = mapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			LOGGER.error("Couldn't write response from scoreSaveManualTree to String",e);
		}
		if(distributionData.size()==0 && data.path("pickedAttrs").size() == 0){
			newScore.setNovelty(nov);
			newScore.setDataset(data.get("dataset").asText());
			newScore.setPct_correct(eval.pctCorrect());
			newScore.setSize(numnodes);
			double score = ((750 * (1 / numnodes)) + (500 * nov) + (1000 * eval
					.pctCorrect()));
			newScore.setScore(score);
			newScore = scoreRepo.saveAndFlush(newScore);
			Tree newTree = new Tree();
			newTree.setComment(data.get("comment").asText());
			Date date = new Date();
			newTree.setCreated(new DateTime(date.getTime()));
			newTree.setFeatures(fList);
			newTree.setCustomFeatures(cfList);
			newTree.setCustomClassifiers(ccList);
			newTree.setCustomTreeClassifiers(tList);
			newTree.setJson_tree(result_json);
			newTree.setPrivate_tree(false);
			newTree.setUser(user);
			newTree.setUser_saved(false);
			newTree.setPrivate_tree(false);
			newTree.setScore(newScore);
			Tree prevTree = treeRepo
					.findById(data.get("previous_tree_id").asLong());
			newTree.setPrev_tree_id(prevTree);
			if (data.get("command").asText().equals("savetree")) {
				newTree.setUser_saved(true);
				int privateflag = data.get("privateflag").asInt();
				if (privateflag == 1) {
					newTree.setPrivate_tree(true);
				}
			}
			treeRepo.saveAndFlush(newTree);
		}
		return result_json;
	}
}
