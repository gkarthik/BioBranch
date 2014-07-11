package org.scripps.branch.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.scripps.branch.classifier.ManualTree;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.CustomClassifierRepository;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.TreeRepository;
import org.scripps.branch.repository.UserRepository;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.scripps.branch.viz.JsonTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

@Service
@Transactional
public class CustomClassifierServiceImpl implements CustomClassifierService {

	@Autowired
	CustomClassifierRepository ccRepo;
	
	@Autowired
	FeatureRepository fRepo;
	
	@Autowired
	AttributeRepository attrRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	TreeRepository treeRepo;
	
	@Autowired
	HibernateAwareObjectMapper mapper;
	
	@Override
	public HashMap getOrCreateClassifier(List entrezIds, int classifierType, String name, String description,  int player_id, Weka weka, String dataset, HashMap<String,Classifier> custom_classifiers){
		List<CustomClassifier> ccList = ccRepo.findAll();
		CustomClassifier returncf = new CustomClassifier();
		Boolean exists = false;
		long[] featureDbIds = new long[entrezIds.toArray().length];
		String message = "Classifier training completed.";
		Feature f;
		int ctr = 0;
		for(Object entrezId : entrezIds.toArray()){
			f = new Feature();
			System.out.println(entrezId.toString());
			f = fRepo.findByUniqueId(entrezId.toString());
			System.out.println(f.getId());
			featureDbIds[ctr] = f.getId();
			ctr++;
		}
		for(CustomClassifier cf:ccList){
			if(cf.getName().equals(name)){
				exists = true;
				returncf = cf;
				message = "Classifier with same name already exists.";
				break;
			}
			List<Feature> fList = cf.getFeature();
			if(cf.getType()==classifierType){
				int count = fList.size();
				int match = 0;
				HashSet hs;
				HashSet hs_orig;
				if(count==featureDbIds.length){
					hs = new HashSet();
					hs_orig = new HashSet(Arrays.asList(featureDbIds));
					for(Feature _f:fList){
						hs.add(_f.getUnique_id());
					}
					if(hs.containsAll(hs_orig)){
						exists = true;
						returncf = cf;
						message = "Classifier with same attributes already exists.";
						break;
					}
				}
			}
		}
		HashMap mp = new HashMap();
		if(!exists){
			try {
				returncf = insertandAddCustomClassifier(featureDbIds, classifierType, name, description, player_id, weka, dataset, custom_classifiers);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		HashMap results = new HashMap();
			results.put("name",returncf.getName());
			results.put("description",returncf.getDescription());
			results.put("type",returncf.getType());
			results.put("id",returncf.getId());
			results.put("exists",exists);
			results.put("message", message);
		return results;
	}
	
	@Override
	public CustomClassifier insertandAddCustomClassifier(long[] featureDbIds, int classifierType, String name, String description,  int player_id, Weka weka, String dataset, HashMap<String,Classifier> custom_classifiers){
		HashMap mp = new HashMap();
		int custom_classifier_id = 0;
		List<Feature> featureList = new ArrayList<Feature>();
		FilteredClassifier fc = buildCustomClasifier(weka, featureDbIds, classifierType);
		for(long id: featureDbIds){
			featureList.add(fRepo.getByDbId(id));
		}
			//Insert into Database
			CustomClassifier newCC = new CustomClassifier();
			User user = userRepo.findById(player_id);
			newCC.setName(name);
			newCC.setType(classifierType);
			newCC.setDescription(description);
			newCC.setFeature(featureList);
			newCC.setUser(user);
	        newCC = ccRepo.saveAndFlush(newCC);
			custom_classifiers.put("custom_classifier_"+newCC.getId(),fc);
		return newCC;
	}
	
	@Override
	public FilteredClassifier buildCustomClasifier(Weka weka, long[] featureDbIds, int classifierType){
		Instances data = weka.getTrain();
		String att_name = "";
		String indices = new String();
		for(long featureDbId : featureDbIds){
			List<Attribute> atts = attrRepo.findByFeatureDbId(featureDbId);
			if(atts!=null&&atts.size()>0){
				for(Attribute att : atts){
					att_name = att.getName();
				}
				indices += String.valueOf(data.attribute(att_name).index()+1)+",";
			}
		}
		System.out.println("Building Classifier");
		System.out.println(indices);
		Remove rm = new Remove();
		rm.setAttributeIndices(indices+"last");
		rm.setInvertSelection(true);		// build a classifier using only these attributes
		FilteredClassifier fc = new FilteredClassifier();
		fc.setFilter(rm);
		switch(classifierType){
		case 0:
			fc.setClassifier(new J48());
			System.out.println("J48");
			break;
		case 1:
			fc.setClassifier(new SMO());
			System.out.println("SMO");
			break;
		case 2:
			fc.setClassifier(new NaiveBayes());
			System.out.println("NaiveBayes");
			break;		
		}
		try {
			fc.buildClassifier(data);
			System.out.println("Built Classifier");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fc;
	}
	
	@Override
	public LinkedHashMap<String, Classifier> getClassifiersfromDb(Weka weka, String dataset){
		LinkedHashMap<String, Classifier> listOfClassifiers = new LinkedHashMap<String, Classifier>();
		List<CustomClassifier> ccList = new ArrayList<CustomClassifier>();
		ccList = ccRepo.findAll();
		for(CustomClassifier cc : ccList){
			listOfClassifiers.put("custom_classifier_"+cc.getId(), getandBuildClassifier(cc, weka, dataset));
		}
		return listOfClassifiers;
	}
	
	@Override
	public FilteredClassifier getandBuildClassifier(CustomClassifier cc, Weka weka, String dataset){
		long[] featuresDbId = new long[cc.getFeature().size()];
		int ctr = 0;
		for(Feature f: cc.getFeature()){
			featuresDbId[ctr] = f.getId();
			ctr++;
		}
		return buildCustomClasifier(weka, featuresDbId, cc.getType());
	}
	
	@Override
	public HashMap getClassifierDetails(long id, String dataset, LinkedHashMap<String, Classifier> custom_classifiers){
		HashMap mp = new HashMap();
		CustomClassifier cc = ccRepo.findById(id);
		String classifierString = custom_classifiers.get("custom_classifier_"+id).toString();
		HashMap featureAttributeMapping = new HashMap();
		String att_name = "";
		for(Feature f: cc.getFeature()){
			for(Attribute attr: f.getAttributes()){
				att_name = attr.getName();
			}
			featureAttributeMapping.put(f.getShort_name(), att_name);
		}
		mp.put("features", featureAttributeMapping);
		mp.put("classifierString",classifierString);
		return mp;
	}
	
	public void addCustomTree(String id, Weka weka,	LinkedHashMap<String, Classifier> custom_classifiers, String dataset) {
		System.out.println("ID befoire add: " + id);
		System.out.println("Contains: " + custom_classifiers.containsKey(id));
		if (!custom_classifiers.containsKey(id)) {
			Tree t = treeRepo.findById(Long.valueOf(id.replace("custom_tree_", "")));
						ManualTree tree = new ManualTree();
						JsonNode rootNode = null;
						try {
							rootNode = mapper.readTree(t.getJson_tree()).get("treestruct");
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						JsonTree jtree = new JsonTree();
						rootNode = jtree.mapEntrezIdsToAttNames(weka, rootNode, dataset, custom_classifiers, attrRepo, this);
						tree.setTreeStructure(rootNode);
						tree.setListOfFc(custom_classifiers);
						try {
							tree.buildClassifier(weka.getTrain());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						custom_classifiers.put(id, tree);
		}
	}
}
