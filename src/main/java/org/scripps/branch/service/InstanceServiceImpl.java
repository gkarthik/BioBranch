package org.scripps.branch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Score;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.DatasetMap;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.DatasetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class InstanceServiceImpl implements InstanceService {

	@Autowired 
	DatasetRepository dataRepo;

	@Autowired
	DatasetMap weka;

	@Autowired
	@Qualifier("attributeRepository")
	private AttributeRepository attr;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InstanceServiceImpl.class);

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
		Instance inst;
		List<weka.core.Attribute> wekaAttributeList = new ArrayList<weka.core.Attribute>();
		weka.core.Attribute wekaAttribute;

		ObjectMapper mapper = new ObjectMapper();
		ArrayList<JsonNode> jsonValsArr = new ArrayList<JsonNode>();

		LOGGER.debug("Create Instance: ");
		List<Attribute> attrList = new ArrayList<Attribute>();
		attrList = getAttributeList(jObj, dataset);
		// Adding class attribute
		attrList.add(dataset.getAttribute());

		//move out try
		try {
			for (Attribute at : attrList) {
				jsonValsArr.add(mapper.readTree(at.getValue()));
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
					inst.setValue(wekaAttributeList.get(l), jNode.findValue(String.valueOf(m)).textValue());
				}
			}
		}

		insts.setClassIndex(insts.numAttributes() - 1);

		LOGGER.debug("inst structure: " + insts.stringFreeStructure());
		LOGGER.debug("NumInstances: " + insts.numInstances());
		LOGGER.debug("NumAttributes: " + insts.numAttributes());
		LOGGER.debug("Instance created!!!");

		return insts;
	}

	@Override
	public List<Attribute> getAttributeList(JsonNode jObj, Dataset dataset) {

		LOGGER.debug("Creating Attribute List!!");
		List<Attribute> attrList = new ArrayList<Attribute>();

		try{
			if (!jObj.has("options")) {
				return new ArrayList<Attribute>();
			}

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
		}
		catch(Exception e){
			LOGGER.debug("Exception in generating attribute list!!! :" + e);
		}

		LOGGER.debug("Attribute List Size"+attrList.size());	
		LOGGER.debug("Attribute List created!!");
		return attrList;
	}


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
			float limitPercent = (percentSplit) / 100;
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

}
