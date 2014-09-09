package org.scripps.branch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import weka.core.AttributeExpression;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.AddExpression;

@Service
@Transactional
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

	@Override
	public void addInstanceValues(Weka weka, Dataset d) {
		List<CustomFeature> cfList = cfeatureRepo.findAll();
		for (CustomFeature cf : cfList) {
				evalAndAddNewFeatureValues("custom_feature_" + cf.getId(), cf.getExpression(), weka.getTrain(), cf.getComponents(), d);
		}
	}

	@Override
	public int evalAndAddNewFeatureValues(String name, String exp,
			Instances data, List<Component> cList, Dataset d) {
		int attIndex = -1;
		AddExpression newFeature = new AddExpression();
		newFeature.setExpression(exp);// Attribute is supplied with index
										// starting from 1
		weka.core.Attribute attr = new weka.core.Attribute(name);
		attIndex = data.numAttributes() - 1;
		data.insertAttributeAt(attr, attIndex);// Insert Attribute just before
												// class value
		try {
			newFeature.setInputFormat(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Instance tempInst;
		List<Attribute> aList;
		String attr_name = null;
		Long limit;
		for (int i = 0; i < data.numInstances(); i++) {// Index here starts from
														// 0.
			try {
				tempInst = new Instance(data.instance(i));
				for(Component c : cList){
					if(c.getFeature()!=null){
						aList = attrRepo.findByFeatureUniqueId(c.getFeature().getUnique_id(), d);
						for(Attribute a: aList){
							attr_name = a.getName();
						}
					}
					limit = c.getUpperLimit();
					if(limit != null){
						if(tempInst.value(data.attribute(attr_name))>c.getUpperLimit()){
							tempInst.setValue(data.attribute(attr_name), limit);
						}
					}
					limit = c.getLowerLimit();
					if(limit != null){
						if(tempInst.value(data.attribute(attr_name))<c.getLowerLimit()){
							tempInst.setValue(data.attribute(attr_name), limit);
						}
					}
							
						
				}
				newFeature.input(tempInst);
				int numAttr = newFeature.outputPeek().numAttributes();
				Instance out = newFeature.output();
				data.instance(i).setValue(data.attribute(attIndex),
						out.value(numAttr - 1));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return attIndex;
	}

	@Override
	public HashMap findOrCreateCustomFeature(String feature_name, String exp,
			String description, long user_id, Dataset dataset, List<Component> cList, Weka weka) {
		HashMap mp = new HashMap();
		Boolean success = true;
		String message = "";
		Instances data = weka.getTrain();
		List<Feature> allFeatures = new ArrayList<Feature>();
		Pattern p = Pattern.compile("@([A-Za-z0-9])+");
		Matcher m = p.matcher(exp);
		String entrezid = "";
		String att_name = "";
		int index = 0;
		while (m.find()) {
			entrezid = m.group().replace("@", "");
			List<Attribute> atts = attrRepo.findByFeatureUniqueId(entrezid,
					dataset);
			if (atts != null && atts.size() > 0) {
				for (Attribute att : atts) {
					att_name = att.getName();
				}
				System.out.println(att_name);
				index = data.attribute(att_name).index();
				allFeatures.add(featureRepo.findByUniqueId(entrezid));
				index++;// WEKA AddExpression() accepts index starting from 1.
				exp = exp.replace("@" + entrezid, "a" + index);
			} else {
				message = "Map Genes to dataset failed.";
				success = false;
			}
		}
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
			compRepo.save(cList);
			compRepo.flush();
			cf.setComponents(cList);
			cf = cfeatureRepo.saveAndFlush(cf);
		} else {
			message = "Feature with this expression already exists";
			exists = true;
		}
		int cFeatureId = (int) cf.getId();
		int attIndex = evalAndAddNewFeatureValues("custom_feature_"
				+ cFeatureId, exp, weka.getTrain(), cList, dataset);
		if (attIndex == -1) {
			message = "Adding feature to dataset failed.";
			success = false;
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