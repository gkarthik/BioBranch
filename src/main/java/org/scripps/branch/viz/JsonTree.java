package org.scripps.branch.viz;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.scripps.branch.classifier.ManualTree;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.AttributeRepository;

import weka.classifiers.Classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonTree {

	public static void main(String[] args) {
		// ObjectMapper mapper = new ObjectMapper();
		// JsonTree t = new JsonTree();
		// LinkedHashMap<String,Classifier> custom_classifiers = new
		// LinkedHashMap<String,Classifier>();
		// String dataset = "metabric_with_clinical";
		// Weka weka = new Weka();
		// JsonNode node = null;
		// String json =
		// "{\"options\":{\"unique_id\":\"metabric_with_clinical_5\"}}";
		// try {
		// node = mapper.readTree(json);
		// } catch (JsonProcessingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// node = t.mapEntrezIdsToAttNames(weka, node, dataset,
		// custom_classifiers);
		// System.out.println(node.toString());
	}

	public JsonNode mapEntrezIdsToAttNames(Weka weka, JsonNode node,
			String dataset,
			LinkedHashMap<String, Classifier> custom_classifiers,
			AttributeRepository attr) {
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
						ManualTree.addCustomTree(unique_id.asText(), weka,
								custom_classifiers, dataset, attr);
					}
					options.put("attribute_name", unique_id.asText());
				}
			}
		}
		ArrayNode children = (ArrayNode) node.get("children");
		if (children != null) {
			for (JsonNode child : children) {
				mapEntrezIdsToAttNames(weka, child, dataset,
						custom_classifiers, attr);
			}
		}
		return node;
	}

	public ManualTree parseJsonTree(Weka weka, JsonNode rootNode,
			String dataset,
			LinkedHashMap<String, Classifier> custom_classifiers,
			AttributeRepository attr) {
		ManualTree tree = new ManualTree();
		try {
			if (!dataset.equals("mammal")) {
				rootNode = mapEntrezIdsToAttNames(weka, rootNode, dataset,
						custom_classifiers, attr);
			}
			tree.setTreeStructure(rootNode);
			tree.setListOfFc(custom_classifiers);
			tree.buildClassifier(weka.getTrain());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tree;
	}

}
