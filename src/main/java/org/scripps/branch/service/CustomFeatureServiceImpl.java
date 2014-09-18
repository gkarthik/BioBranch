package org.scripps.branch.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.scripps.branch.controller.MetaServerController;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Component;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.ComponentRepository;
import org.scripps.branch.repository.CustomFeatureCustomRepository;
import org.scripps.branch.repository.CustomFeatureRepository;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import weka.core.AttributeExpression;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.AddExpression;
import weka.filters.unsupervised.attribute.Remove;

@Service
public class CustomFeatureServiceImpl implements CustomFeatureService {

	@Autowired
	@Qualifier("attributeRepository")
	AttributeRepository attrRepo;

	@Autowired
	CustomFeatureCustomRepository cfeatureCusRepo;

	@Autowired
	CustomFeatureRepository cfeatureRepo;

	@Autowired
	@Qualifier("featureRepository")
	FeatureRepository featureRepo;

	@Autowired
	UserRepository userRepo;
	
	@Autowired 
	ComponentRepository compRepo;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CustomFeatureServiceImpl.class);
	
	ArrayList classDistribution = new ArrayList();

	@Override
	public void addInstanceValues(Weka weka, Dataset d) {
		List<CustomFeature> cfList = cfeatureRepo.findAll();
		Collections.sort(cfList, new Comparator<CustomFeature>(){
			@Override
			public int compare(CustomFeature o1, CustomFeature o2) {
			    if (o1.getId() > o2.getId()) {
			        return 1;
			    } else if (o1.getId() < o2.getId()) {
			        return -1;
			    }
			    return 0;
			}
		});
		for (CustomFeature cf : cfList) {
			if(cf.getDataset().getCollection().getId()==d.getCollection().getId()){
				LOGGER.debug("Custom Feature got");
				LOGGER.debug(String.valueOf(cf.getComponents().size()));
				evalAndAddNewFeatureValues("custom_feature_" + cf.getId(), cf.getExpression(), weka.getOrigTrain(), cf.getComponents(), cf.getReference(), d, true);
			}
		}
	}

	@Override
	public int evalAndAddNewFeatureValues(String name, String exp,
			Instances data, List<Component> cList, Component ref, Dataset d, Boolean saveInstance) {
		int attIndex = -1;
		AttributeExpression m_attributeExpression = new AttributeExpression();
		try {
			m_attributeExpression.convertInfixToPostfix(exp);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		double[] vals = new double[data.numAttributes()+1];
		weka.core.Attribute attr = new weka.core.Attribute(name);
		attIndex = data.numAttributes() - 1;
		if(saveInstance){
			data.insertAttributeAt(attr, attIndex);// Insert Attribute just before
			// class value
		} else {
			classDistribution = new ArrayList();
		}
		HashMap mp;
		List<Attribute> aList;
		String attr_name = null;
		String ref_name = null;
		Double limit;
		String[] attrNames = new String[cList.size()+1];
		int ctr = 0;
		if(cList.size()>0){
			for(Component c: cList){
				if(c.getFeature()!=null){
					aList = attrRepo.findByFeatureUniqueId(c.getFeature().getUnique_id(), d);
					for(Attribute a: aList){
						attrNames[ctr] = a.getName();
					}
				} else if(c.getCfeature()!=null){
					attrNames[ctr] = "custom_feature_"+c.getCfeature().getId();
				}
				ctr++;
			}
		}
		if(ref!=null){
			if(ref.getFeature()!=null){
				aList = attrRepo.findByFeatureUniqueId(ref.getFeature().getUnique_id(), d);
				for(Attribute a: aList){
					ref_name = a.getName();
				}
			} else if (ref.getCfeature()!=null) {
				ref_name = "custom_feature_"+ref.getCfeature().getId();
			}
		}
		for (int i = 0; i < data.numInstances(); i++) {// Index here starts from
														// 0.
			vals = new double[data.numAttributes()+1];
			vals = data.instance(i).toDoubleArray();
			try {
				ctr = 0;
				for(Component c : cList){
					attr_name = attrNames[ctr];
					if(data.instance(i).isMissing(data.attribute(attr_name).index())){
						continue;
					}
					if(c.getUpperLimit()!=null || c.getLowerLimit()!=null || ref!=null){
						limit = c.getUpperLimit();
						if(limit != null){
							if(vals[data.attribute(attr_name).index()]>c.getUpperLimit()){
								vals[data.attribute(attr_name).index()] = limit;
							}
						}
						limit = c.getLowerLimit();
						if(limit != null){
							if(vals[data.attribute(attr_name).index()]<c.getLowerLimit()){
								vals[data.attribute(attr_name).index()] = limit;
							}
						}
					}
					if(ref!=null){
						if(ref_name!=null){
							vals[data.attribute(attr_name).index()] = vals[data.attribute(attr_name).index()]+data.instance(i).value(data.attribute(ref_name));
						}
					}
					ctr++;
				}
				try{
					m_attributeExpression.evaluateExpression(vals);
				} catch(Exception e) {
					LOGGER.error(name,e);
					break;
				}
				mp = new HashMap();
				if(saveInstance){	
					data.instance(i).setValue(data.attribute(attIndex), vals[vals.length-1]);
				} else {
					mp.put("value", vals[vals.length-1]);
					mp.put("classprob", data.instance(i).classAttribute().value((int) data.instance(i).classValue()));
					classDistribution.add(mp);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Collections.sort(classDistribution, new Comparator<HashMap>(){
			public int compare(HashMap o1, HashMap o2) {
			    if (Double.valueOf(o1.get("value").toString()) > Double.valueOf(o2.get("value").toString())) {
			        return 1;
			    } else if (Double.valueOf(o1.get("value").toString()) < Double.valueOf(o2.get("value").toString())) {
			        return -1;
			    }
			    return 0;
			}});
		return attIndex;
	}

	@Override
	public HashMap findOrCreateCustomFeature(String feature_name, String exp,
			String description, long user_id, Dataset dataset, List<Component> cList, Component ref, Weka weka) {
		HashMap mp = new HashMap();
		Boolean success = true;
		String message = "Success.";
		Instances data = weka.getOrigTrain();
		HashMap exMap = parseExpression(exp, dataset, data);
		exp = (String) exMap.get("exp");
		message = (String) exMap.get("message");
		success = (Boolean) exMap.get("success");
		AttributeExpression _attrExp = new AttributeExpression();
		CustomFeature cf = new CustomFeature();
		try {
			_attrExp.convertInfixToPostfix(exp);
		} catch (Exception e) {
			message = "Expression not valid.";
			success = false;
			e.printStackTrace();
		}
		Boolean exists = false;
		cf = cfeatureRepo.findByName(feature_name);
		if (cf == null) {
			cf = cfeatureCusRepo.getByPostfixExpr(exp);
			if(cf!=null){
				Boolean equalTo = true;
				if(cf.getComponents().size()==cList.size()){
					for(Component c1: cf.getComponents()){
						for(Component c2 : cList){
							if(c1.getFeature()!=null && c2.getFeature()!=null){
								if(c1.getFeature().getId()!=c2.getFeature().getId()){
									equalTo = false;
								}
							}
							if(c1.getCfeature()!=null && c2.getCfeature()!=null){
								if(c1.getCfeature().getId()!=c2.getCfeature().getId()){
									equalTo = false;
								}
							}
						}
					}
				} else {
					equalTo = false;
				}
				if(!equalTo){
					cf = null;
				}
			}
		} else {
			message = "Feauture with this name already exists";
			exists = true;
		}
		if (cf == null) {
			cf = new CustomFeature();
			User newuser = userRepo.findById(user_id);
			cf.setName(feature_name);
			cf.setExpression(exp);
			cf.setDescription(description);
			cf.setUser(newuser);
			cf.setDataset(dataset);
			if(ref!=null){
				ref = compRepo.saveAndFlush(ref);
			}
			cf.setReference(ref);
			cf = cfeatureRepo.saveAndFlush(cf);
			for(Component c: cList){
				c.setParentCustomFeature(cf);
			}
			compRepo.save(cList);
			compRepo.flush();
		} else {
			message = "Feature with this expression and limits already exists";
			exists = true;
		}
		int cFeatureId = (int) cf.getId();
		if(!exists){
			int attIndex = evalAndAddNewFeatureValues("custom_feature_"
					+ cFeatureId, exp, weka.getTrain(), cList, ref, dataset, true);
			if (attIndex == -1) {
				message = "Adding feature to dataset failed.";
				success = false;
			}
		}
		System.out.println("cFeatureId: " + cFeatureId);
		mp.put("exists", exists);
		mp.put("message", message);
		mp.put("success", success);
		mp.put("id", "custom_feature_" + cFeatureId);
		mp.put("name", cf.getName());
		mp.put("description", cf.getDescription());
		return mp;
	}
	
	public HashMap parseExpression(String exp, Dataset dataset, Instances data){
		Pattern p = Pattern.compile("@([A-Za-z0-9_])+");
		Matcher m = p.matcher(exp);
		String entrezid = "";
		String att_name = "";
		int index = 0;
		String message = "";
		Boolean success = true;
		HashMap mp = new HashMap();
		while (m.find()) {
			entrezid = m.group().replace("@", "");
			if(entrezid.contains("custom_feature")){
				att_name = entrezid;
				index = data.attribute(att_name).index();
				index++;// WEKA AddExpression() accepts index starting from 1.
				exp = exp.replace("@" + entrezid, "a" + index);
			} else {
				List<Attribute> atts = attrRepo.findByFeatureUniqueId(entrezid,
						dataset);
				if (atts != null && atts.size() > 0) {
					for (Attribute att : atts) {
						att_name = att.getName();
					}
					System.out.println(att_name);
					index = data.attribute(att_name).index();
					index++;// WEKA AddExpression() accepts index starting from 1.
					exp = exp.replace("@" + entrezid, "a" + index);
				} else {
					message = "Map Genes to dataset failed.";
					success = false;
				}
			}
		}
		mp.put("exp",exp);
		mp.put("msg",message);
		mp.put("success",success);
		return mp;
	}
	
	@Override
	public ArrayList previewCustomFeature(String name, String exp, List<Component> cList, Component ref, Instances data, Dataset d) {
		exp = (String) parseExpression(exp, d, data).get("exp");
		evalAndAddNewFeatureValues(name, exp, data, cList, ref, d, false);
		return classDistribution;
	}

//	@Override
//	@Transactional
//	public HashMap getTestCase(String id, Weka weka) {
//		HashMap mp = new HashMap();
//		id = id.replace("custom_feature_", "");
//		CustomFeature cf = cfeatureRepo.findById(Long.valueOf(id));
//		Instances data = weka.getTrain();
//		String att_name = "";
//		int attIndex = 0;
//		Random rand = new Random();
//		int instanceIndex = rand.nextInt(data.numInstances());
//		HashMap feature_values = new HashMap();
//		List<Feature> fList = cf.getComponents();
//		for (Feature _feature : fList) {
//			List<Attribute> attrs = _feature.getAttributes();
//			att_name = "";
//			for (Attribute attr : attrs) {
//				att_name = attr.getName();
//			}
//			attIndex = data.attribute(att_name).index();
//			feature_values.put(_feature.getShort_name(),
//					data.instance(instanceIndex).value(attIndex));
//		}
//		mp.put("features", feature_values);
//		attIndex = data.attribute("custom_feature_" + id).index();
//		mp.put("custom_feature", data.instance(instanceIndex).value(attIndex));
//		mp.put("sample", instanceIndex);
//		return mp;
//	}
}