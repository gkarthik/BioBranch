package org.scripps.branch.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Attribute;
import weka.core.Instances;

@Service
@Transactional
public class FeatureServiceImpl implements FeatureService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureServiceImpl.class);
	
	@Autowired 
	FeatureRepository fRepo;
	
	@Autowired 
	AttributeRepository aRepo;
	
	@Autowired
	HibernateAwareObjectMapper mapper;
	
	@Transactional
	public ArrayNode rankFeatures(Instances data, List<String> entrezIds, Dataset d) {
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ArrayNode sortedList = new ArrayNode(factory);
		AttributeSelection attsel = new AttributeSelection();
		InfoGainAttributeEval eval = new InfoGainAttributeEval();
		Ranker search = new Ranker();
		attsel.setEvaluator(eval);
		attsel.setSearch(search);
		List<org.scripps.branch.entity.Attribute> aList = aRepo.findByDatasetOrderByRelieffDesc(d);
		List<Feature> fList = new ArrayList<Feature>();
		Feature f;
		//Must introduce a better check
		if(entrezIds == null){
//			double[][] attrRanks = new double[data.numAttributes()][2];
//			try {
//				attsel.SelectAttributes(data);
//				attrRanks = attsel.rankedAttributes();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			for(int i = 0; i < data.numAttributes()-1;i++){
//				Attribute a = data.attribute((int) attrRanks[i][0]);
//				if(!a.name().contains("custom_feature")){
//					for(org.scripps.branch.entity.Attribute attr : aList){
//						if(attr.getName().equals(a.name())){
//							attr.setRelieff((float)attrRanks[i][1]);
//							LOGGER.debug(attr.getName()+": "+attrRanks[i][1]);
//						}
//					}
//				}
//			}
//			aRepo.save(aList);
//			aRepo.flush();
		} else {
			ObjectNode objNode;
			String id;
				for(org.scripps.branch.entity.Attribute attr : aList){
					if(attr.getFeature()!=null){
						if(entrezIds.contains(attr.getFeature().getUnique_id())){
							objNode = mapper.valueToTree(attr.getFeature());
							objNode.put("infogain", attr.getRelieff());
							sortedList.add(objNode);
							entrezIds.remove(attr.getFeature().getUnique_id());
							LOGGER.debug(attr.getFeature().getShort_name());
						}
					}
				}
		}
		return sortedList;
	}

	
}
