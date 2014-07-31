package org.scripps.branch.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

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
import org.scripps.branch.service.TreeService;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.scripps.branch.viz.JsonTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
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
	UserRepository userRepo;

	@Autowired
	private WekaObject weka;

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

	@RequestMapping(value = "/MetaServer", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	public @ResponseBody String scoreOrSaveTree(@RequestBody JsonNode data)
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
				System.out.println(tList.size());
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
			result_json = getClinicalFeatures(data);
		} else if (command.contains("custom_feature_")) {
			if (command.equals("custom_feature_create")) {
				HashMap mp = cfeatureService.findOrCreateCustomFeature(data
						.get("name").asText(), data.get("expression").asText(),
						data.get("description").asText(), data.get("user_id")
								.asLong(), data.get("dataset").asText(), weka
								.getWeka());
				result_json = mapper.writeValueAsString(mp);
			} else if (command.equals("custom_feature_search")) {
				List<CustomFeature> cfList = cfeatureRepo
						.searchCustomFeatures(data.get("query").asText());
				result_json = mapper.writeValueAsString(cfList);
			} else if (command.equals("custom_feature_testcase")) {
				HashMap mp = cfeatureService.getTestCase(data.get("id")
						.asText(), weka.getWeka());
				result_json = mapper.writeValueAsString(mp);
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
				HashMap mp = cClassifierService.getOrCreateClassifier(
						entrezIds, classifierType, name, description,
						player_id, weka.getWeka(), dataset,
						weka.getCustomClassifierObject());
				result_json = mapper.writeValueAsString(mp);
			} else if (command.equals("custom_classifier_search")) {
				List<CustomClassifier> cclist = cClassifierRepo
						.searchCustomClassifiers(data.get("query").asText());
				result_json = mapper.writeValueAsString(cclist);
			} else if (command.equals("custom_classifier_getById")) {
				HashMap mp = cClassifierService.getClassifierDetails(
						data.get("id").asLong(), data.get("dataset").asText(),
						weka.getCustomClassifierObject());
				result_json = mapper.writeValueAsString(mp);
			}
		} else if (command.contains("pathway")) {
			if (command.equals("search_pathways")) {
				List<Pathway> pList = pathwayRepo.searchPathways(data.get(
						"query").asText());
				result_json = mapper.writeValueAsString(pList);
			} else if (command.equals("get_genes_of_pathway")) {
				Pathway p = pathwayRepo.findByNameAndSourcedb(
						data.get("pathway_name").asText(), data
								.get("source_db").asText());
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
		LinkedHashMap<String, Classifier> custom_classifiers = weka
				.getCustomClassifierObject();
		Evaluation eval = new Evaluation(wekaObj.getTest());
		Instances train = wekaObj.getOrigTrain();
		Instances test = wekaObj.getOrigTest();
		switch (data.get("testOptions").get("value").asInt()) {
		case 0:
			wekaObj.setTrain(train);
			wekaObj.setTest(train);
			break;
		case 1:
			wekaObj.setTrain(train);
			wekaObj.setTest(test);
			break;
		case 2:
			float limitPercent = (data.get("testOptions").get("percentSplit")
					.asLong()) / (float) 100;
			System.out.println(limitPercent);
			Instances[] classLimits = wekaObj.getInstancesInClass();
			float numLimit = 0;
			System.out.println(limitPercent);
			numLimit = limitPercent * train.numInstances();
			System.out.println(numLimit);
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
			System.out.println(wekaObj.getTrain().numInstances());
			System.out.println(wekaObj.getTest().numInstances());
			// ArffSaver saver = new ArffSaver();
			// saver.setInstances(newTrain);
			// saver.setFile(new
			// File("/home/karthik/Documents/splits/train.arff"));
			// saver.writeBatch();
			// saver = new ArffSaver();
			// saver.setInstances(newTest);
			// saver.setFile(new
			// File("/home/karthik/Documents/splits/test.arff"));
			// saver.writeBatch();
			break;
		}
		readtree = t.parseJsonTree(wekaObj, data.get("treestruct"),
				data.get("dataset").asText(), custom_classifiers, attr,
				cClassifierService);
		eval.evaluateModel(readtree, wekaObj.getTest());
		JsonNode cfmatrix = mapper.valueToTree(eval.confusionMatrix());
		JsonNode treenode = readtree.getJsontree();
		HashMap distributionData = readtree.getDistributionData();
		int numnodes = readtree.numNodes();
		HashMap mp = new HashMap();
		t.getFeatures(treenode, mp, featureRepo, cfeatureRepo, cClassifierRepo,
				treeRepo);
		User user = userRepo.findById(data.get("player_id").asLong());
		Score newScore = new Score();
		double nov = 0;
		Boolean flag = true;
		List<Feature> fList = (List<Feature>) mp.get("fList");
		List<CustomFeature> cfList = (List<CustomFeature>) mp.get("cfList");
		List<CustomClassifier> ccList = (List<CustomClassifier>) mp
				.get("ccList");
		List<Tree> tList = (List<Tree>) mp.get("tList");
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
		result.put("treestruct", treenode);
		String result_json = "";
		try {
			result_json = mapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch blockAim: Build a decision tree that
			// predicts 10 year survival using gene expression values and
			// clinical variables.
			e.printStackTrace();
		}
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
		return result_json;
	}
}
