package org.scripps.branch.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.scripps.branch.entity.Tutorial;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.evaluation.Evaluation;
import org.scripps.branch.evaluation.ThresholdCurve;
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
import org.scripps.branch.repository.TutorialRepository;
import org.scripps.branch.repository.UserRepository;
import org.scripps.branch.service.CustomClassifierService;
import org.scripps.branch.service.CustomFeatureService;
import org.scripps.branch.service.FeatureService;
import org.scripps.branch.service.InstanceService;
import org.scripps.branch.service.TreeService;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.scripps.branch.viz.JsonTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import weka.classifiers.Classifier;
import weka.core.Instances;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
	
	@Autowired
	private TutorialRepository tRepo;
	
	@Autowired 
	private InstanceService instSer;
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/update-trees", method = RequestMethod.POST)
	public String updateTreeList(String command, Model model)
			throws Exception {
		if(command.equals("update")){
			List<Tree> tList = treeRepo.findAll();
			String result_json = "";
			ObjectNode dataTable = mapper.createObjectNode();
			JsonNode pickedAttrs = dataTable.putArray("pcikedAttrs");
			model.addAttribute("success",true);
		    model.addAttribute("message","Tree Json refreshed.");
		    String message = "Tree Json refresh failed for ";
			for(Tree t : tList){
					Dataset d = t.getScore().getDataset();
					User u = t.getUser();
					int testOption = (int) t.getScore().getTestoption();
					int testsetId = -1;
					Float percentSplit = null;
					if(testOption == 1){
						testsetId = (int) t.getScore().getTestset().getId();
					} else if (testOption == 2) {
						percentSplit = t.getScore().getTestsplit();
					}
					String comment = "";
					comment = t.getComment();
					Long prevTreeId = null;
					if(t.getPrev_tree_id()!=null){
						prevTreeId = t.getPrev_tree_id().getId();
					}
					int privateflag = (t.getPrivate_tree()) ? 1 : 0;
					JsonNode treestruct = mapper.readTree(t.getJson_tree());
					result_json = null;
					if(t.isUser_saved()){
						LOGGER.debug("Tree ID: {}", t.getId());
						LOGGER.debug("Node: {}", treestruct.get("treestruct").get("name"));
						result_json = scoreSaveManualTree(command, treestruct.get("treestruct"), d, u, testOption, testsetId, percentSplit, pickedAttrs, comment, prevTreeId, privateflag, false);
						if(result_json == null){
							model.addAttribute("success", false);
						    model.addAttribute("message",message+=t.getId()+", ");
						} else {
							t.setJson_tree(result_json);
						}
					}
			}
			treeRepo.save(tList);
			treeRepo.flush();
		}
	    return "tutorial/tutorials";
	}
	
	@RequestMapping(value = "/MetaServer", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	public @ResponseBody String metaServerAPI(@RequestBody JsonNode data)
			throws Exception {
		String command = data.get("command").asText();
		String result_json = "";
		if (command.equals("scoretree") || command.equals("savetree")) {
			Dataset d = dataRepo.findById(data.get("dataset").asLong());
			User u = userRepo.findById(data.get("player_id").asInt());
			int testOption = data.get("testOptions").get("value").asInt();
			int testsetId = -1;
			Float percentSplit = null;
			if(testOption == 1){
				testsetId = data.get("testOptions").get("testsetid").asInt();
			} else if (testOption == 2) {
				percentSplit = (float) data.get("testOptions").get("percentSplit").asDouble();
			}
			String comment = "";
			int privateflag = 0;
			Long prevTreeId = data.get("previous_tree_id").asLong();
			if(command.equals("savetree")){
				comment = data.get("comment").asText();
				privateflag = data.get("privateflag").asInt();
			}
			result_json = scoreSaveManualTree(command, data.get("treestruct"), d, u, testOption, testsetId, percentSplit, data.path("pickedAttrs"), comment, prevTreeId, privateflag, true);
		} else if (command.contains("get_tree")) {
			if (command.equals("get_tree_by_id")) {
				Tree t = treeRepo.findById(data.get("treeid").asLong());
				result_json = mapper.writeValueAsString(t);
			} else if (command.equals("get_trees_by_search")) {
				Dataset d = dataRepo.findById(data.get("dataset").asLong());
				List<Tree> tList = treeRepo.getTreesBySearch(data.get("query")
						.asText(), d);
				result_json = mapper.writeValueAsString(tList);
			} else if (command.equals("get_trees_by_profile_search")) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				UserDetails userDetails = (UserDetails) auth.getPrincipal();
				User authUser = userRepo.findByEmail(userDetails.getUsername());
				List<Tree> tList = treeRepo.getTreesByProfileSearch(data.get("query")
						.asText(), authUser);
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
				List<Tree> tList = treeRepo.getAllTrees( new PageRequest( data.get("lowerLimit").asInt(), data.get("upperLimit").asInt()));
				int ctr = 1;
				for (Tree t : tList) {
					t.setRank(ctr);
					ctr++;
				}
				result_json = mapper.writeValueAsString(tList);
			}
		} else if (command.equals("search_clinical_features")) {
			Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt()));
			List<Feature> fList = featureRepo.searchNonGeneFeatures(d, data.get("query").asText());
			List<String> entrezIds = new ArrayList<String>();
			for(Feature f: fList){
				entrezIds.add(f.getUnique_id());
			}
			result_json = mapper.writeValueAsString(fSer.rankFeatures(getReqInstances(data), entrezIds, d));
		} else if (command.contains("custom_feature_")) {
			if (command.equals("custom_feature_create") || command.equals("custom_feature_preview")) {
				List<Component> cList = new ArrayList<Component>();
				Component c;Boolean toAdd = false;
				for (JsonNode el : data.path("components")) {
					toAdd = false;
					c = new Component();
					if(el.get("id").asText().contains("custom_feature")){
						c.setCfeature(cfeatureRepo.findById(Long.valueOf(el.get("id").asText().replace("custom_feature_",""))));
					} else {
						c.setFeature(featureRepo.findByUniqueId(el.get("id").asText()));
					}
					c.setUpperLimit(null);
					c.setLowerLimit(null);
					if(!el.get("uLimit").isNull()){
						c.setUpperLimit(el.get("uLimit").asDouble());
						toAdd = true;
					}
					if(!el.get("lLimit").isNull()){
						c.setLowerLimit(el.get("lLimit").asDouble());
						toAdd = true;
					}
					if(!data.get("ref_id").isNull()){
						toAdd = true;
					}
					if(toAdd && command.equals("custom_feature_preview")){
						cList.add(c);
					} else if(command.equals("custom_feature_create")) {
						cList.add(c);
					}
				}
				Component ref = null;
				if(!data.get("ref_id").isNull()){
					ref = new Component();
					ref.setUpperLimit(null);
					ref.setLowerLimit(null);
					if(data.get("ref_id").asText().contains("custom_feature")){
						ref.setCfeature(cfeatureRepo.findById(Long.valueOf(data.get("ref_id").asText().replace("custom_feature_",""))));
					} else {
						ref.setFeature(featureRepo.findByUniqueId(data.get("ref_id").asText()));
					}
				}
				Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt()));
				HashMap mp = new HashMap();
				if(command.equals("custom_feature_create")){
					mp = cfeatureService.findOrCreateCustomFeature(data
							.get("name").asText(), data.get("expression").asText(),
							data.get("description").asText(), data.get("user_id")
									.asLong(), d, cList, ref, weka
									.getWeka(d.getId()));
				} else if(command.equals("custom_feature_preview")) {
					ArrayList l = cfeatureService.previewCustomFeature(data.get("name").asText(), data.get("expression").asText(),
							 cList, ref, weka.getWeka(d.getId()).getOrigTrain(), d);
					mp.put("isNominal", false);
					mp.put("dataArray", l);
				}
				result_json = mapper.writeValueAsString(mp);
			} else if (command.equals("custom_feature_search")) {
				Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt())); 
				List<CustomFeature> cfList = cfeatureRepo
						.searchCustomFeatures(data.get("query").asText(), d.getCollection());
				result_json = mapper.writeValueAsString(cfList);
			} else if (command.equals("custom_feature_testcase")) {
				Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt()));
//				HashMap mp = cfeatureService.getTestCase(data.get("id")
//						.asText(), weka.getWeka(d.getId()));
//				result_json = mapper.writeValueAsString(mp);			
			} else if (command.equals("custom_feature_getById")){
				CustomFeature cf = cfeatureRepo.findById(data.get("id").asLong());
				result_json = mapper.writeValueAsString(cf);
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
				Dataset d = dataRepo.findById(data.get("dataset").asLong());
				List<CustomClassifier> cclist = cClassifierRepo
						.searchCustomClassifiers(data.get("query").asText(), d);
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
				Iterator<Feature> ite = fList.iterator();
				Feature f;
				while(ite.hasNext()){
					f = ite.next();
					for(Attribute a: f.getAttributes()){
						if(a.getDataset().getId() == d.getId()){
							exists = true;
							entrezIds.add(f.getUnique_id());
						}
					}
					if(!exists){
						ite.remove();
					}
				}
				result_json = mapper.writeValueAsString(fSer.rankFeatures(getReqInstances(data), entrezIds, d));
			}
		} else if (command.contains("rank_")){
			if(command.equals("rank_attributes")){
				Dataset d = dataRepo.findById(data.get("dataset").asLong());
				List<String> entrezIds = new ArrayList<String>();
				for(JsonNode el : data.path("unique_ids")){
					entrezIds.add(el.asText());
				}
				result_json = mapper.writeValueAsString(fSer.rankFeatures(getReqInstances(data), entrezIds, d));
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
			mp.put("cf", true);
			mp.put("cc", true);
			mp.put("t", true);
			long nonGeneCount = featureRepo.getCountOfNonGeneFeature(d);
			long total_count = featureRepo.getCountOfFeatures(d);
			if(nonGeneCount == 0){
				mp.put("non_genes", false);
			} else if(total_count==nonGeneCount){
				mp.put("genes", false);
			}
			if(cfeatureRepo.getCountFromCollection(d.getCollection())==0){
				mp.put("cf", false);
			}
			if(cClassifierRepo.getCount()==0){
				mp.put("cc", false);
			}
			if(treeRepo.getCount()==0){
				mp.put("t", false);
			}
			List<Double> aList = attrRepo.findByDatasetOrderByRelieffDesc(d);
			mp.put("infoGainMax", aList.get(0));
			mp.put("infoGainMin", aList.get(aList.size()-1));
//			mp.put("infoGainMax", 1);
//			mp.put("infoGainMin", 0);
			result_json = mapper.writeValueAsString(mp);
		} else if (command.equals("get_feature_limits")){
			Weka wekaObj = weka.getWeka(data.get("dataset").asLong());
			Dataset d = dataRepo.findById(Long.valueOf(data.get("dataset").asInt()));
			Instances train = wekaObj.getOrigTrain();
			String attr_name = "";
			String Uid = data.get("unique_id").asText();
			if(Uid!="" && !Uid.contains("custom_feature")){
				for(Attribute a: attrRepo.findByFeatureUniqueId(Uid, d)){
					attr_name = a.getName();
				}
			} else if (Uid!="") {
				attr_name = Uid;
			}
			train.sort(train.attribute(attr_name));
			HashMap<String, Double> mp = new HashMap<String, Double>();
			mp.put("uLimit", train.instance(train.numInstances()-1).value(train.attribute(attr_name)));
			mp.put("lLimit", train.instance(0).value(train.attribute(attr_name)));
			result_json = mapper.writeValueAsString(mp);
		} else if (command.contains("tutorial")){
			if(command.equals("tutorial_user_get")){
				List<Tutorial> tListComplete = tRepo.findAll();
				List<Tutorial> tList = userRepo.findById(data.get("user_id").asLong()).getTutorials();
				ArrayList<HashMap<String,String>> a = new ArrayList<HashMap<String,String>>();
				Boolean nComp;
				HashMap<String, String> mp;
				for(Tutorial t1: tListComplete){
					nComp = true;
					mp = new HashMap<String, String>();
					mp.put("id", String.valueOf(t1.getId()));
					mp.put("title", t1.getTitle());
					mp.put("description", t1.getDescription());
					mp.put("url", t1.getUrl());
					for(Tutorial t2 : tList){
						if(t2.getId()==t1.getId()){
							nComp = false;
						}
					}
					if(nComp){
						mp.put("completed", "false");
					} else {
						mp.put("completed", "true");
					}
					a.add(mp);
				}
				result_json = mapper.writeValueAsString(a);
			} else if (command.equals("tutorial_user_add")){
				User u = userRepo.findById(data.get("user_id").asLong());
				List<Tutorial> tList = u.getTutorials();
				tList.add(tRepo.findById(data.get("tutorial_id").asLong()));
				u.setTutorials(tList);
				u = userRepo.saveAndFlush(u);
				result_json = "{\"message\": \"success\"}";
			}
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
		Score newScore = new Score();
		int testOption = data.get("testOptions").get("value").asInt();
		int testsetId = -1;
		Float percentSplit = null;
		if(testOption == 1){
			testsetId = data.get("testOptions").get("testsetid").asInt();
		} else if (testOption == 2) {
			percentSplit = (float) data.get("testOptions").get("percentSplit").asDouble();
		}
		instSer.setTrainandTest(wekaObj, train, testOption, testsetId, percentSplit, newScore);
		readtree = t.parseJsonTree(wekaObj, data.get("treestruct"),
				d, custom_classifiers, attr,
				cClassifierService, customSetRepo, d);
		LOGGER.debug(String.valueOf(readtree.getRequiredInst().numInstances()));
		return readtree.getRequiredInst();
	}
	
	public String scoreSaveManualTree(String command, JsonNode treestruct, Dataset d, User u, Integer testOption, Integer testsetId, Float percentSplit, JsonNode pickedAttrs, String comment, Long prevTreeId, Integer privateTree, Boolean saveFlag){
		Weka wekaObj = weka.getWeka(d.getId());
		JsonTree t = new JsonTree();
		ManualTree readtree = new ManualTree();
		//readtree.setNumFolds(0);
		LinkedHashMap<String, Classifier> custom_classifiers = weka
				.getCustomClassifierObject();
		Instances train = wekaObj.getOrigTrain();
		Score newScore = new Score();
		//int testsetId = -1;
		//Float percentSplit = null;
//		if(testOption == 1){
//			//testsetId = data.get("testOptions").get("testsetid").asInt();
//		} else if (testOption == 2) {
//			//percentSplit = (float) data.get("testOptions").get("percentSplit").asDouble();
//		}
		instSer.setTrainandTest(wekaObj, train, testOption, testsetId, percentSplit, newScore);
		readtree = t.parseJsonTree(wekaObj, treestruct,
				d, custom_classifiers, attr,
				cClassifierService, customSetRepo, d);
		Evaluation eval = null;
		try {
			eval = new Evaluation(wekaObj.getTest());
			eval.evaluateModel(readtree, wekaObj.getTest());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			LOGGER.error("Evaluaion Exeption", e1);
		}
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
		if(pickedAttrs!=null){
			for (JsonNode el : pickedAttrs) {
				attr = new ArrayList<Attribute>();
				attr = attrRepo.findByFeatureUniqueId(el.asText(), d);
				for(Attribute a : attr){	
					attrIndex = reqInstances.attribute(a.getName()).index();
				}
				attrIndexes.add(attrIndex);
			}
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
		double nov = 0;
		List<Feature> fList = (List<Feature>) mp.get("fList");
		List<CustomFeature> cfList = (List<CustomFeature>) mp.get("cfList");
		List<CustomClassifier> ccList = (List<CustomClassifier>) mp.get("ccList");
		List<Tree> tList = (List<Tree>) mp.get("tList");
		List<CustomSet> csList = (List<CustomSet>) mp.get("csList");
		nov = treeService
				.getUniqueIdNovelty(fList, cfList, ccList, tList, u);
		ObjectNode result = mapper.createObjectNode();
		result.put("pct_correct", eval.pctCorrect());
		result.put("size", numnodes);
		result.put("novelty", nov);
		result.put("confusion_matrix", cfmatrix);
		result.put("auc", eval.areaUnderROC(0));
		result.put("auc_data_points", mapper.valueToTree(eval.getTc().getRocDataPoints().toArray()));
		result.put("auc_max_index", mapper.valueToTree(eval.getTc().getMaxIndex()));
		result.put("text_tree", readtree.toString());
		result.put("treestruct", treenode);
		result.put("distribution_data", mapper.valueToTree(distributionData));
		//data.path("pickedAttrs").size()
		if(attrIndexes.size()>0){
			result.put("instances_data", mapper.valueToTree(instanceData));
		}
		result.put("treestruct", treenode);
		String result_json = "";
		try {
			result_json = mapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			LOGGER.error("Couldn't write response from scoreSaveManualTree to String",e);
		}
		if(distributionData.size()==0 && attrIndexes.size() == 0 && saveFlag == true){
			newScore.setNovelty(nov);
			newScore.setDataset(d);
			newScore.setPct_correct(eval.pctCorrect());
			newScore.setSize(numnodes);
			double score = ((750 * (1 / numnodes)) + (500 * nov) + (1000 * eval
					.pctCorrect()));
			newScore.setScore(score);
			newScore = scoreRepo.saveAndFlush(newScore);
			Tree newTree = new Tree();
			newTree.setComment(comment);
			Date date = new Date();
			newTree.setCreated(new DateTime(date.getTime()));
			newTree.setFeatures(fList);
			newTree.setCustomFeatures(cfList);
			newTree.setCustomClassifiers(ccList);
			newTree.setCustomTreeClassifiers(tList);
			newTree.setCustomSets(csList);
			newTree.setJson_tree(result_json);
			newTree.setPrivate_tree(false);
			newTree.setUser(u);
			newTree.setUser_saved(false);
			newTree.setPrivate_tree(false);
			newTree.setScore(newScore);
			Tree prevTree = treeRepo
					.findById(prevTreeId);
			//data.get("previous_tree_id").asLong()
			//data.get("privateflag").asInt()
			newTree.setPrev_tree_id(prevTree);
			if (command.equals("savetree")) {
				newTree.setUser_saved(true);
				if (privateTree == 1) {
					newTree.setPrivate_tree(true);
				}
			}
			treeRepo.saveAndFlush(newTree);
		}
		return result_json;
	}
}
