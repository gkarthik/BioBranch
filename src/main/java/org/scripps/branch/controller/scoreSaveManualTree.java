package org.scripps.branch.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.scripps.branch.classifier.ManualTree;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.WekaObject;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.viz.JsonTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class scoreSaveManualTree {

	@Autowired
	private WekaObject weka;

	@Autowired
	@Qualifier("attributeRepository")
	private AttributeRepository attr;

	@RequestMapping(value = "/MetaServer", method = RequestMethod.POST, headers = { "Content-type=application/json" })
	public @ResponseBody String scoreOrSaveTree(@RequestBody JsonNode data,
			HttpServletRequest request) throws Exception {
		Weka wekaObj = WekaObject.getWeka();
		JsonTree t = new JsonTree();
		ManualTree readtree = new ManualTree();
		LinkedHashMap<String, Classifier> custom_classifiers = new LinkedHashMap<String, Classifier>();
		readtree = t.parseJsonTree(wekaObj, data.get("treestruct"),
				data.get("dataset").asText(), custom_classifiers, attr);
		int numnodes = readtree.numNodes();
		Evaluation eval = new Evaluation(wekaObj.getTrain());
		eval.evaluateModel(readtree, wekaObj.getTrain());
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode result = mapper.createObjectNode();
		JsonNode treenode = readtree.getJsontree();
		HashMap distributionData = readtree.getDistributionData();
		result.put("pct_correct", eval.pctCorrect());
		result.put("size", numnodes);
		result.put("text_tree", readtree.toString());
		result.put("treestruct", treenode);
		result.put("distribution_data", mapper.valueToTree(distributionData));
		// serialize and return the result
		List<Attribute> attrList = attr.findByFeatureUniqueId("1960",
				"metabric_with_clinical");
		result.put("treestruct", treenode);
		String result_json = "";
		try {
			result_json = mapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result_json;
	}
}