package org.scripps.branch.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.AttributeRepository;
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
public class CustomFeatureServiceImpl implements CustomFeatureService{
	
	@Autowired
	@Qualifier("attributeRepository")
	AttributeRepository attrRepo;
	
	@Autowired
	@Qualifier("featureRepository")
	FeatureRepository featureRepo;
	
	@Autowired
	CustomFeatureCustomRepository cfeatureCusRepo;
	
	@Autowired
	CustomFeatureRepository cfeatureRepo;
	
	@Autowired
	UserRepository userRepo;
	
	public HashMap findOrCreateCustomFeature(String feature_name, String exp, String description, long user_id, String dataset, Weka weka){
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
		while(m.find()){
			entrezid = m.group().replace("@", "");
			List<Attribute> atts = attrRepo.findByFeatureUniqueId(entrezid,dataset);
			if(atts!=null&&atts.size()>0){
				for(Attribute att : atts){
					att_name = att.getName();
				}
				System.out.println(att_name);
				index = data.attribute(att_name).index();
				allFeatures.add((Feature) featureRepo.findByUniqueId(entrezid));
				index++;//WEKA AddExpression() accepts index starting from 1.
				exp = exp.replace("@"+entrezid, "a"+index);
			}else{
				message = "Map Genes to dataset failed.";
				success = false;
			}
		}
		AttributeExpression _attrExp = new AttributeExpression();
		CustomFeature cf = new CustomFeature();
		try {
			_attrExp.convertInfixToPostfix(exp);
		} catch (Exception e) {
			message = "Expression could not be converted to postfix.";
			success = false;
			e.printStackTrace();
		}
		Boolean exists = false;
		cf = cfeatureRepo.findByName(feature_name);
		if(cf==null){
			cf = cfeatureCusRepo.getByPostfixExpr(exp);
		} else {
			message = "Feauture with this name already exists";
			exists = true;
		}
		if(cf==null){
			cf = new CustomFeature();
			User newuser = userRepo.findById(user_id); 
			cf.setName(feature_name);
			cf.setDataset(dataset);
			cf.setExpression(exp);
			cf.setDescription(description);
			cf.setUser(newuser);
			cf.setFeatures(allFeatures);
			cf = cfeatureRepo.saveAndFlush(cf);
		} else {
			message = "Feauture with this expression already exists";
			exists = true;
		}
		int cFeatureId = (int) cf.getId();
		int attIndex = evalAndAddNewFeatureValues("custom_feature_" + cFeatureId, exp,	weka.getTrain());
		if(attIndex == -1){
			message = "Adding feature to dataset failed.";
			success = false;
		}
		System.out.println("cFeatureId: "+cFeatureId);
		mp.put("exists", exists);
		mp.put("message", message);
		mp.put("success", success);
		mp.put("id", "custom_feature_" + cFeatureId);
		mp.put("name", cf.getName());
		mp.put("description", cf.getDescription());
		return mp;
	}
	
	public int evalAndAddNewFeatureValues(String name, String exp, Instances data){
		int attIndex = -1;
		AddExpression newFeature = new AddExpression();
		newFeature.setExpression(exp);//Attribute is supplied with index starting from 1
		weka.core.Attribute attr = new weka.core.Attribute(name);
		attIndex = data.numAttributes()-1;
			data.insertAttributeAt(attr, attIndex);//Insert Attribute just before class value
			try {
				newFeature.setInputFormat(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0; i<data.numInstances(); i++){//Index here starts from 0.
					try {
						newFeature.input(data.instance(i));
						int numAttr = newFeature.outputPeek().numAttributes();
						Instance out = newFeature.output();
						data.instance(i).setValue(data.attribute(attIndex), out.value(numAttr-1));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		return attIndex;
	}
	
	public void addInstanceValues(Weka weka){
		List<CustomFeature> cfList = cfeatureRepo.findAll();
		for(CustomFeature cf : cfList){
			evalAndAddNewFeatureValues("custom_feature_"+cf.getId(), cf.getExpression(), weka.getTrain());
		}
	}
	
	@Transactional
	public HashMap getTestCase(String id, Weka weka){
		HashMap mp = new HashMap();
		id = id.replace("custom_feature_", "");
		CustomFeature cf = cfeatureRepo.findById(Long.valueOf(id));
		Instances data = weka.getTrain();
		String att_name = "";
		int attIndex = 0;
		Random rand = new Random(); 
		int instanceIndex = rand.nextInt(data.numInstances()); 
		HashMap feature_values = new HashMap();
		List<Feature> fList = cf.getFeatures();
		for(Feature _feature: fList){
			List<Attribute> attrs = _feature.getAttributes();
			att_name = "";
			for(Attribute attr: attrs){
				att_name = attr.getName();
			}
			attIndex = data.attribute(att_name).index();
			feature_values.put(_feature.getShort_name(), data.instance(instanceIndex).value(attIndex));
		}
		mp.put("features", feature_values);
		attIndex = data.attribute("custom_feature_"+id).index();
		mp.put("custom_feature", data.instance(instanceIndex).value(attIndex));
		mp.put("sample", instanceIndex);
		return mp;
	}
}