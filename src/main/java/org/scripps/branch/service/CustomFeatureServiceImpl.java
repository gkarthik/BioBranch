package org.scripps.branch.service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	public HashMap findOrCreateCustomFeature(String feature_name, String exp, String description, int user_id, String dataset, Weka weka){
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
		CustomFeature cf;
		try {
			_attrExp.convertInfixToPostfix(exp);
		} catch (Exception e) {
			message = "Expression could not be converted to postfix.";
			success = false;
			e.printStackTrace();
		}
		Boolean exists = false;
		cf = cfeatureRepo.findByName(feature_name);
		if(cf!=null){
			cf = cfeatureCusRepo.getByPostfixExpr(exp);
		} else {
			message = "Feauture with this name already exists";
			exists = true;
		}
		if(cf!=null){
		User newuser = userRepo.findById(user_id); 
		cf = new CustomFeature(feature_name, exp, description, dataset, newuser, allFeatures);
		cfeatureRepo.saveAndFlush(cf);
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
}