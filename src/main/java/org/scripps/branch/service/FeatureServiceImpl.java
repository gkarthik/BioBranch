package org.scripps.branch.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	
	public String hashAttrName(String name) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(name.getBytes());
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}
	
	public List<Feature> generateFeaturesFromDataset(Instances data, Dataset d){
		List<Feature> fList = new ArrayList<Feature>();
		Feature f;
		LOGGER.debug(String.valueOf(data.numAttributes()));
		for(int i = 0; i< data.numAttributes();i++){
			LOGGER.debug(data.attribute(i).name());
			f = new Feature();
			f.setLong_name(data.attribute(i).name());
			f.setShort_name(data.attribute(i).name());
			f.setUnique_id(hashAttrName(System.currentTimeMillis() + data.attribute(i).name()));
			f.setDescription(data.attribute(i).name());
			f.setIsGene(false);
			fList.add(f);
		}
		fList = fRepo.save(fList);
		fRepo.flush();
		return fList;
	}
	
	@Transactional
	public JsonNode[] rankFeatures(Instances data, List<String> entrezIds, Dataset d) {
		JsonNodeFactory factory = JsonNodeFactory.instance;
		JsonNode[] sortedList = null;
		//Must introduce a better check
		if(entrezIds == null){
//			AttributeSelection attsel = new AttributeSelection();
//			InfoGainAttributeEval eval = new InfoGainAttributeEval();
//			Ranker search = new Ranker();
//			attsel.setEvaluator(eval);
//			attsel.setSearch(search);
//			List<org.scripps.branch.entity.Attribute> aList = aRepo.findByDatasetOrderByRelieffDesc(d);
//			List<Feature> fList = new ArrayList<Feature>();
//			Feature f;
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
			List<org.scripps.branch.entity.Attribute> tempList;
			double infogain = 0;
			int i =0, k=0;
			Feature f;
			sortedList = new JsonNode[entrezIds.size()];
			Boolean toAdd;
			for(String id: entrezIds){
				toAdd = false;
				f = fRepo.findByUniqueId(id);
				tempList = aRepo.findByFeatureUniqueId(id, d);
				if(tempList.size()>0){
					toAdd = true;
				}
				infogain = 0;
				for(org.scripps.branch.entity.Attribute a : tempList){
					if(a.getRelieff() > infogain){
						infogain = a.getRelieff();
					}
				}
				if(toAdd){
					objNode = mapper.valueToTree(f);
					objNode.put("infogain", infogain);
					i=-1;
					while(i<entrezIds.size()-1){
						i++;
						if(sortedList[i]==null){
							sortedList[i] = objNode;
							break;
						}
						if(infogain>sortedList[i].get("infogain").asDouble()){
							k = entrezIds.size()-1;
							while(k>=i+1){
								sortedList[k] = sortedList[k-1];
								k--;
							}
							sortedList[i] = objNode;
							break;
						}
					}
				}
			}
			List<JsonNode> sorted = new ArrayList<JsonNode>();
			    for(JsonNode s : sortedList) {
			       if(s!=null) {
			    	   if(!s.isNull()){
					          sorted.add(s);
			    	   }
			       }
			    }
			    sortedList = sorted.toArray(new JsonNode[sorted.size()]);
//				for(org.scripps.branch.entity.Attribute attr : aList){
//					if(attr.getFeature()!=null){
//						if(entrezIds.contains(attr.getFeature().getUnique_id())){
//							objNode = mapper.valueToTree(attr.getFeature());
//							objNode.put("infogain", attr.getRelieff());
//							sortedList.add(objNode);
//							entrezIds.remove(attr.getFeature().getUnique_id());
//							LOGGER.debug(attr.getFeature().getShort_name());
//						}
//					}
//				}
			
		}
		return sortedList;
	}

	
}
