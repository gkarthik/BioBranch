package org.scripps.branch.viz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.scripps.branch.classifier.ManualTree;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.CustomSet;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.CustomClassifierRepository;
import org.scripps.branch.repository.CustomFeatureRepository;
import org.scripps.branch.repository.CustomSetRepository;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.TreeRepository;
import org.scripps.branch.service.CustomClassifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.classifiers.Classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonTree {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonTree.class);
	
	public static void main(String[] args) {
		
	}

	public void getFeatures(JsonNode node, HashMap mp, FeatureRepository f,
			CustomFeatureRepository cf, CustomClassifierRepository cc,
			TreeRepository t, CustomSetRepository cs) {
		List<Feature> fList = (List<Feature>) mp.get("fList");
		List<CustomFeature> cfList = (List<CustomFeature>) mp.get("cfList");
		List<CustomClassifier> ccList = (List<CustomClassifier>) mp
				.get("ccList");
		List<Tree> tList = (List<Tree>) mp.get("tList");
		List<CustomSet> csList = (List<CustomSet>) mp.get("csList");
		if (fList == null) {
			fList = new ArrayList<Feature>();
		}
		if (cfList == null) {
			cfList = new ArrayList<CustomFeature>();
		}
		if (ccList == null) {
			ccList = new ArrayList<CustomClassifier>();
		}
		if (tList == null) {
			tList = new ArrayList<Tree>();
		}
		if (csList == null) {
			csList = new ArrayList<CustomSet>();
		}
		ObjectNode options = (ObjectNode) node.get("options");
		String uid = "";
		if (options != null) {
			JsonNode unique_id = options.get("unique_id");
			if (unique_id != null) {
				uid = unique_id.asText();
				if (uid.contains("custom_tree")) {
					Tree temp = t.findById(Long.valueOf(uid.replace(
							"custom_tree_", "")));
					if (temp != null) {
						tList.add(temp);
					}
				} else if (uid.contains("custom_feature")) {
					CustomFeature temp = cf.findById(Long.valueOf(uid.replace(
							"custom_feature_", "")));
					if (temp != null) {
						cfList.add(temp);
					}
				} else if (uid.contains("custom_classifier")) {
//					CustomClassifier temp = cc.findById(Long.valueOf(uid
//							.replace("custom_classifier_", "")));
					CustomClassifier temp = new CustomClassifier();
					if (temp != null) {
						ccList.add(temp);
					}
				} else if (uid.contains("custom_set")) {
					CustomSet temp = cs.findById(Long.valueOf(uid
							.replace("custom_set_", "")));
					if (temp != null) {
						csList.add(temp);
					}
				} else {
					Feature temp = f.findByUniqueId(unique_id.asText());
					if (temp != null) {
						fList.add(temp);
					}
				}
			}
		}
		mp.put("fList", fList);
		mp.put("cfList", cfList);
		mp.put("ccList", ccList);
		mp.put("tList", tList);
		ArrayNode children = (ArrayNode) node.get("children");
		if (children != null) {
			for (JsonNode child : children) {
				getFeatures(child, mp, f, cf, cc, t, cs);
			}
		}
	}

	public JsonNode mapEntrezIdsToAttNames(Weka weka, JsonNode node,
			Dataset dataset,
			LinkedHashMap<String, Classifier> custom_classifiers,
			AttributeRepository attr, CustomClassifierService ccService, CustomSetRepository cSetRepo) {
		ObjectNode options = (ObjectNode) node.get("options");
		if (options != null) {
			JsonNode unique_id = options.get("unique_id");
			if (unique_id != null && unique_id.asText() != "") {
				if (!unique_id.asText().contains("custom_")) {
					List<Attribute> atts = attr.findByFeatureUniqueId(
							unique_id.asText(), dataset);
					if (atts != null && atts.size() > 0) {
						for (Attribute att : atts) {
							String att_name = att.getName();
							options.put("attribute_name", att_name);
						}
					} else {
						options.put("error", "no attribute found for given id ");
					}
				} else {
					if (unique_id.asText().contains("custom_tree_")) {
						ccService.addCustomTree(unique_id.asText(), weka,
								custom_classifiers, dataset, cSetRepo);
					}
					options.put("attribute_name", unique_id.asText());
				}
			}
		}
		ArrayNode children = (ArrayNode) node.get("children");
		if (children != null) {
			for (JsonNode child : children) {
				mapEntrezIdsToAttNames(weka, child, dataset,
						custom_classifiers, attr, ccService, cSetRepo);
			}
		}
		return node;
	}

	public ManualTree parseJsonTree(Weka weka, JsonNode rootNode,
			Dataset dataset,
			LinkedHashMap<String, Classifier> custom_classifiers,
			AttributeRepository attr, CustomClassifierService ccService, CustomSetRepository cSetRepo) {
		ManualTree tree = new ManualTree();
		try {
			if (!dataset.equals("mammal")) {
				rootNode = mapEntrezIdsToAttNames(weka, rootNode, dataset,
						custom_classifiers, attr, ccService, cSetRepo);
			}
			tree.setTreeStructure(rootNode);
			tree.setListOfFc(custom_classifiers);
			tree.setCcSer(ccService);
			tree.setCustomRepo(cSetRepo.findAll());
			tree.buildClassifier(weka.getTrain());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			LOGGER.error("JsonProcessingException",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("IOException",e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Exception",e);
		}
		return tree;
	}
}
