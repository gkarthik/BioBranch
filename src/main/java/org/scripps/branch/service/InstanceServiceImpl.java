package org.scripps.branch.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Pathway;
import org.scripps.branch.entity.Score;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.DatasetMap;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.PathwayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

@Service
public class InstanceServiceImpl implements InstanceService {
	
	@Autowired 
	DatasetRepository dataRepo;
	
	@Autowired
	DatasetMap weka;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(InstanceServiceImpl.class);
	
	@Override
	public void setTrainandTest(Weka wekaObj, Instances train, int testOption, int testsetId, Float percentSplit, Score newScore) {
		// TODO Auto-generated method stub
		switch (testOption) {
		case 0:
			wekaObj.setTrain(train);
			wekaObj.setTest(train);
			break;
		case 1:
			wekaObj.setTrain(train);
			long testsetid = testsetId;
			wekaObj.setTest(weka.getWeka(testsetid).getOrigTrain());
			newScore.setTestoption(1);
			newScore.setTestset(dataRepo.findById(testsetid));
			break;
		case 2:
			float limitPercent = (percentSplit) / (float) 100;
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
					//Remove randomize to ensure reproducibility.
//					if (i == 0) {
//						classLimits[j].randomize(new Random(1));
//					}
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
			newScore.setTestoption(2);
			newScore.setTestsplit(percentSplit);
			break;
		}
	}
	
	@Autowired
	@Qualifier("attributeRepository")
	private AttributeRepository attr;

//	public void checkChildren(JsonNode child, List<String> unique_id_list) {
//
//		if (child.get("options").get("kind").asText().equals("split_node")) {
//			unique_id_list.add(child.get("options").get("unique_id").asText());
//		}
//		if (child.get("children") != null) {
//			ArrayNode newChild = (ArrayNode) child.get("children");
//			for (JsonNode node : newChild)
//				checkChildren(node, unique_id_list);
//		}
//	}

//	@Override
//	public Instances createInstance(JsonNode jObj, Dataset dataset) {
//
//		LOGGER.debug("Create Instance: ");
//		
//		System.out.println("JSON Object"+jObj);
//		
//		List<Attribute> attrList = new ArrayList<Attribute>();
//		attrList = getAttributeList(jObj, dataset);
//
//		// Adding class attribute
//		attrList.add(dataset.getAttribute());
//
//		ObjectMapper mapper = new ObjectMapper();
//		ArrayList<JsonNode> jsonValsArr = new ArrayList<JsonNode>();
//
//		for (Attribute at : attrList) {
//			try {
//				
//				System.out.println("Valu"+at.getValue());
//				jsonValsArr.add(mapper.readTree(at.getValue()));
//			} catch (JsonProcessingException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//		List<weka.core.Attribute> wekaAttributeList = new ArrayList<weka.core.Attribute>();
//		weka.core.Attribute wekaAttribute;
//
//		// adding nominal/numeric attribute labels to fast vector and creating
//		// weka attribute list
//		for (Attribute at : attrList) {
//			if (at.isNominal()) {
//				FastVector fVector = new FastVector();
//				String[] labelVals = at.getLabels().replace("{", "")
//						.replace("}", "").split(",");
//
//				for (String str : labelVals) {
//					fVector.addElement(str);
//				}
//				wekaAttribute = new weka.core.Attribute(at.getName(), fVector);
//			} else {
//				wekaAttribute = new weka.core.Attribute(at.getName());
//			}
//			wekaAttributeList.add(wekaAttribute);
//		}
//
//		FastVector fvWekaAttributes = new FastVector(attrList.size());
//		for (weka.core.Attribute at : wekaAttributeList) {
//			fvWekaAttributes.addElement(at);
//		}
//
//		Instances insts = new Instances("Rel", fvWekaAttributes,
//				jsonValsArr.size());
//
//		for (int s = 0; s < jsonValsArr.get(0).size(); s++) {
//			insts.add(new Instance(jsonValsArr.size()));
//		}
//
//		Instance inst;
//		// creating instance and adding it to instances
//		for (int l = 0; l < attrList.size(); l++) {
//			JsonNode jNode = jsonValsArr.get(l);
//			for (int m = 0; m < jNode.size(); m++) {
//				inst = insts.instance(m);
//				if (attrList.get(l).getFeature() != null
//						&& !attrList.get(l).isNominal()) {
//					inst.setValue(wekaAttributeList.get(l),
//							jNode.findValue(String.valueOf(m)).asDouble());
//				}
//				else if (l == attrList.size() - 1
//						|| attrList.get(l).isNominal()) {
//					weka.core.Attribute a = wekaAttributeList.get(l);
//					boolean t = a.isNominal();
//					String tmp = String.valueOf(jNode.findValue(
//							String.valueOf(m)).asText());
//					inst.setValue(wekaAttributeList.get(l), String
//							.valueOf(jNode.findValue(String.valueOf(m))
//									.asText()));
//				}
//			}
//		}
//
//		insts.setClassIndex(insts.numAttributes() - 1);
//
//		LOGGER.debug("inst structure: " + insts.stringFreeStructure());
//		LOGGER.debug("NumInstances: " + insts.numInstances());
//		LOGGER.debug("NumAttributes: " + insts.numAttributes());
//
//		return insts;
//	}
//
//	@Override
//	public List<Attribute> getAttributeList(JsonNode jObj, Dataset dataset) {
//
//		LOGGER.debug("Creating Attribute List!!");
//		List<Attribute> attrList = new ArrayList<Attribute>();
//
////		if (!jObj.get("treestruct").has("options")) {
////			return new ArrayList<Attribute>();
////		}
//		
//		if (!jObj.has("options")) {
//			return new ArrayList<Attribute>();
//		}
//
////		JsonNode jOptions = jObj.get("treestruct").get("options");
//		
//		JsonNode jOptions = jObj.get("options");
//		
//		List<String> unique_id_List = new ArrayList<String>();
//
//		if (jOptions.has("unique_id")) {
//			unique_id_List.add(jOptions.get("unique_id").asText());
//		}
//
//		if (jObj.has("children")) {
//			ArrayNode children = (ArrayNode) jObj.get(
//					"children");
//			for (JsonNode child : children) {
//				checkChildren(child, unique_id_List);
//			}
//		}
//
//		for (String uniqueid : unique_id_List)
//			attrList.add(attr.findByUniqueId(uniqueid, dataset));
//		
//		System.out.println("attribute List Size"+attrList.size());	
//
//		LOGGER.debug("Attribute List created!!");
//		return attrList;
//	}
	public void checkChildren(JsonNode child, List<String> unique_id_list) {

		if (child.get("options").get("kind").asText().equals("split_node")) {
			unique_id_list.add(child.get("options").get("unique_id").asText());
		}
		if (child.get("children") != null) {
			ArrayNode newChild = (ArrayNode) child.get("children");
			for (JsonNode node : newChild)
				checkChildren(node, unique_id_list);
		}
	}

	@Override
	public Instances createInstance(JsonNode jObj, Dataset dataset) {

		LOGGER.debug("Create Instance: ");
		List<Attribute> attrList = new ArrayList<Attribute>();
		attrList = getAttributeList(jObj, dataset);

		// Adding class attribute
		attrList.add(dataset.getAttribute());
		
	

		ObjectMapper mapper = new ObjectMapper();
		ArrayList<JsonNode> jsonValsArr = new ArrayList<JsonNode>();

		for (Attribute at : attrList) {
			try {
				jsonValsArr.add(mapper.readTree(at.getValue()));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		List<weka.core.Attribute> wekaAttributeList = new ArrayList<weka.core.Attribute>();
		weka.core.Attribute wekaAttribute;

		// adding nominal/numeric attribute labels to fast vector and creating
		// weka attribute list
		for (Attribute at : attrList) {
			if (at.isNominal()) {
				FastVector fVector = new FastVector();
				String[] labelVals = at.getLabels().replace("{", "")
						.replace("}", "").split(",");

				for (String str : labelVals) {
					fVector.addElement(str);
				}
				wekaAttribute = new weka.core.Attribute(at.getName(), fVector);
			} else {
				wekaAttribute = new weka.core.Attribute(at.getName());
			}
			wekaAttributeList.add(wekaAttribute);
		}

		FastVector fvWekaAttributes = new FastVector(attrList.size());
		for (weka.core.Attribute at : wekaAttributeList) {
			fvWekaAttributes.addElement(at);
		}

		Instances insts = new Instances("Rel", fvWekaAttributes,
				jsonValsArr.size());

		for (int s = 0; s < jsonValsArr.get(0).size(); s++) {
			insts.add(new Instance(jsonValsArr.size()));
		}

		Instance inst;
		LOGGER.debug("size of attrlist 2"+attrList.size());
		// creating instance and adding it to instances
		for (int l = 0; l < attrList.size(); l++) {
			JsonNode jNode = jsonValsArr.get(l);
			for (int m = 0; m < jNode.size(); m++) {
				inst = insts.instance(m);
				if (attrList.get(l).getFeature() != null
						&& !attrList.get(l).isNominal()) {
					inst.setValue(wekaAttributeList.get(l),
							jNode.findValue(String.valueOf(m)).asDouble());
				}
				else if (l == attrList.size() - 1
						|| attrList.get(l).isNominal()) {
					weka.core.Attribute a = wekaAttributeList.get(l);
					boolean t = a.isNominal();
					String tmp = String.valueOf(jNode.findValue(
							String.valueOf(m)).asText());
					
					LOGGER.debug("value of M "+ String.valueOf(jNode.findValue(String.valueOf(m)).asText()));
					LOGGER.debug("Weka Attribute at l  "+ wekaAttributeList.get(l));

					inst.setValue(wekaAttributeList.get(l), jNode.findValue(String.valueOf(m)).textValue());
					
					LOGGER.debug("instance "+inst.toString());
				}
			}
		}

		insts.setClassIndex(insts.numAttributes() - 1);

		LOGGER.debug("inst structure: " + insts.stringFreeStructure());
		LOGGER.debug("NumInstances: " + insts.numInstances());
		LOGGER.debug("NumAttributes: " + insts.numAttributes());

		return insts;
	}
	
	
	@Override
	public List<Attribute> getAttributeList(JsonNode jObj, Dataset dataset) {

		
		LOGGER.debug("Creating Attribute List!!");
		List<Attribute> attrList = new ArrayList<Attribute>();

//		if (!jObj.get("treestruct").has("options")) {
//			return new ArrayList<Attribute>();
//		}
		
		if (!jObj.has("options")) {
			return new ArrayList<Attribute>();
		}

//		JsonNode jOptions = jObj.get("treestruct").get("options");
		
		JsonNode jOptions = jObj.get("options");
		
		List<String> unique_id_List = new ArrayList<String>();

		if (jOptions.has("unique_id")) {
			unique_id_List.add(jOptions.get("unique_id").asText());
		}

		if (jObj.has("children")) {
			ArrayNode children = (ArrayNode) jObj.get(
					"children");
			for (JsonNode child : children) {
				checkChildren(child, unique_id_List);
			}
		}

		for (String uniqueid : unique_id_List)
			attrList.add(attr.findByUniqueId(uniqueid, dataset));
		
		LOGGER.debug("attribute List Size"+attrList.size());	

		LOGGER.debug("Attribute List created!!");
		return attrList;
	}
	
}
