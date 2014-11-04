package org.scripps.branch.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.scripps.branch.entity.CustomFeature;
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
import weka.attributeSelection.GainRatioAttributeEval;
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
		int limit = 50;
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
		} else if(entrezIds.size()==0){
			sortedList = new JsonNode[limit];
			GainRatioAttributeEval eval = new GainRatioAttributeEval();
			try{
				eval.buildEvaluator(data);
			} catch(Exception e) {
				LOGGER.error("Couldn't evaluate gain ratio", e);
			}
			Double infogain = Double.MIN_VALUE;
			ObjectNode objNode;
			int k, i;
			double[][] ig = new double[data.numAttributes()][2];
			for(int j=0;j<data.numAttributes();j++){
				try {
					ig[j][0] = j;
					ig[j][1] = eval.evaluateAttribute(j);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						LOGGER.error("Couldn't evaluate gain ratio for empty entrezIds.",e);
					}
			}
			Arrays.sort(ig, new Comparator<double[]>(){
				@Override
				public int compare(double[] o1, double[] o2) {
					// TODO Auto-generated method stub
					  if (o1[1] > o2[1]) {
					        return -1;
					    } else if (o1[1] < o2[1]) {
					        return 1;
					    }
					    return 0;
				}
			});
			for(int j=0;j<limit;j++){
				org.scripps.branch.entity.Attribute a = aRepo.findByNameAndDataset(data.attribute((int) ig[j][0]).name(), d);
				if(a==null){
					continue;
				}
				if(a.getFeature()==null){
					continue;
				}
				objNode = mapper.valueToTree(a.getFeature());
				objNode.put("infogain", ig[j][1]);
				i=-1;
				while(i<limit-1){
					i++;
					if(sortedList[i]==null){
						sortedList[i] = objNode;
						break;
					}
					if(infogain>sortedList[i].get("infogain").asDouble()){
						k = limit-1;
						while(k>=i+1){
							sortedList[k] = sortedList[k-1];
							k--;
						}
						sortedList[i] = objNode;
						break;
					}
				}
			}
				
			
		} else {
			ObjectNode objNode;
			List<org.scripps.branch.entity.Attribute> tempList;
			double infogain = 0;
			int i =0, k=0;
			Feature f;
			Boolean toAdd;
			InfoGainAttributeEval eval = new InfoGainAttributeEval();
			try{
				eval.buildEvaluator(data);
			} catch(Exception e) {
				LOGGER.error("Couldn't evaluate gain ratio", e);
			}
			sortedList = new JsonNode[entrezIds.size()];
			for(String id: entrezIds){
				toAdd = false;
				f = fRepo.findByUniqueId(id);
				tempList = aRepo.findByFeatureUniqueId(id, d);
				if(tempList.size()>0){
					toAdd = true;
				}
//				double[][] attrRanks = new double[data.numAttributes()][2];
//				AttributeSelection attsel = new AttributeSelection();
//				Ranker search = new Ranker();
//				try {
//					search.setStartSet(startSet);
//					attsel.setEvaluator(eval);
//					attsel.setSearch(search);
//					attsel.SelectAttributes(data);
//					attrRanks = attsel.rankedAttributes();
//					
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				infogain = 0;
				double tempIG = 0; 
				for(org.scripps.branch.entity.Attribute a : tempList){
					try {
						tempIG = eval.evaluateAttribute(data.attribute(a.getName()).index());
						if(tempIG > infogain){
							infogain = tempIG;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						LOGGER.error("Gain ratio Couldn't be calculated", e);
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
		if(sortedList!=null){
			List<JsonNode> sorted = new ArrayList<JsonNode>();
		    for(JsonNode s : sortedList) {
		       if(s!=null) {
		    	   if(!s.isNull()){
				          sorted.add(s);
		    	   }
		       }
		    }
		    sortedList = sorted.toArray(new JsonNode[sorted.size()]);
		}
		return sortedList;
	}

	
}
