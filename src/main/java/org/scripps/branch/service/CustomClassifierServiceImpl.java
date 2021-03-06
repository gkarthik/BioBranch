package org.scripps.branch.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.scripps.branch.classifier.ManualTree;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.SerializedCustomClassifier;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.CustomClassifierRepository;
import org.scripps.branch.repository.CustomSetRepository;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.SerializedCustomClassifierRepository;
import org.scripps.branch.repository.TreeRepository;
import org.scripps.branch.repository.UserRepository;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.scripps.branch.viz.JsonTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

@Service
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
	
	@Autowired
	SerializedCustomClassifierRepository sccRepo;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomClassifierServiceImpl.class);

	@Override
	public void addCustomTree(String id, Weka weka,
			LinkedHashMap<String, Classifier> custom_classifiers, Dataset dataset, CustomSetRepository cSetRepo) {
		if (!custom_classifiers.containsKey(id)) {
			Tree t = treeRepo.findById(Long.valueOf(id.replace("custom_tree_",
					"")));
			ManualTree tree = new ManualTree();
			JsonNode rootNode = null;
			try {
				rootNode = mapper.readTree(t.getJson_tree()).get("treestruct");
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				LOGGER.error("Couldn't convert json to JsonNode",e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOGGER.error("IO Exception",e);
			}
			JsonTree jtree = new JsonTree();
			rootNode = jtree.mapEntrezIdsToAttNames(weka, rootNode, dataset,
					custom_classifiers, attrRepo, this, cSetRepo);
			tree.setTreeStructure(rootNode);
			tree.setListOfFc(custom_classifiers);
			tree.setCustomRepo(cSetRepo.findAll());
			try {
				tree.buildClassifier(weka.getTrain());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.error("Couldn't build classifier",e);
			}
			custom_classifiers.put(id, tree);
		}
	}

	@Override
	public HashMap buildCustomClasifier(Instances data, long id){
		HashMap mp = new HashMap();
		CustomClassifier c = ccRepo.findById(id);
		int classifierType = c.getType();
		List<Feature> fList = c.getFeatures();
		long[] featureDbIds = new long[fList.toArray().length];
		int ctr = 0;
		for (Feature f: fList) {
			featureDbIds[ctr] = f.getId();
			ctr++;
		}
		String att_name = "";
		String indices = new String();
		for (long featureDbId : featureDbIds) {
			List<Attribute> atts = attrRepo.findByFeatureDbId(featureDbId);
			if (atts != null && atts.size() > 0) {
				for (Attribute att : atts) {
					att_name = att.getName();
				}
				if(data.attribute(att_name)!=null){
					indices += String.valueOf(data.attribute(att_name).index() + 1)
							+ ",";
				}
			}
		}
		LOGGER.debug("Building Classifier");
		LOGGER.debug(indices);
		Remove rm = new Remove();
		rm.setAttributeIndices(indices + "last");
		rm.setInvertSelection(true); // build a classifier using only these
										// attributes
		FilteredClassifier fc = new FilteredClassifier();
		fc.setFilter(rm);
		switch (classifierType) {
		case 0:
			fc.setClassifier(new J48());
			break;
		case 1:
			fc.setClassifier(new SMO());
			break;
		case 2:
			fc.setClassifier(new NaiveBayes());
			break;
		}
		try {
			fc.buildClassifier(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error building classifier",e);
		}
		SerializedCustomClassifier scc = new SerializedCustomClassifier();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = null;
		User user;
		userDetails = (UserDetails) auth.getPrincipal();
		user = userRepo.findByEmail(userDetails.getUsername());
		scc.setUser(user);
		LOGGER.debug(c.getName());
		scc.setCustomClassifier(c);
		ByteArrayOutputStream baos;
		ObjectOutputStream out;
		baos = new ByteArrayOutputStream();
		try {
			out = new ObjectOutputStream(baos);
			out.writeObject(fc);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		scc.setSerialized_object(baos.toByteArray());
		scc = sccRepo.saveAndFlush(scc);
		mp.put("classifier", fc);
		mp.put("id", scc.getId());
		return mp;
	}

	@Override
	public HashMap getClassifierDetails(long id, Dataset dataset,
			LinkedHashMap<String, Classifier> custom_classifiers) {
		HashMap mp = new HashMap();
		CustomClassifier cc = sccRepo.findById(id).getCustomClassifier();
		String classifierString = custom_classifiers.get(
				"custom_classifier_" + id).toString();
		HashMap featureAttributeMapping = new HashMap();
		String att_name = "";
		for (Feature f : cc.getFeatures()) {
			for (Attribute attr : f.getAttributes()) {
				att_name = attr.getName();
			}
			featureAttributeMapping.put(f.getShort_name(), att_name);
		}
		mp.put("features", featureAttributeMapping);
		mp.put("classifierString", classifierString);
		return mp;
	}

	@Override
	@Transactional
	public LinkedHashMap<String, Classifier> getClassifiersfromDb(HashMap<String, Weka> name_dataset) {
		LinkedHashMap<String, Classifier> listOfClassifiers = new LinkedHashMap<String, Classifier>();
		List<SerializedCustomClassifier> ccList = new ArrayList<SerializedCustomClassifier>();
		ccList = sccRepo.findAll();
		ByteArrayInputStream bais;
		ObjectInputStream in;
		Classifier c;
		int length;
		for (SerializedCustomClassifier cc : ccList) {
				try {
					bais = new ByteArrayInputStream(cc.getSerialized_object());
					in = new ObjectInputStream(bais);
					c = (Classifier) in.readObject();
					listOfClassifiers.put("custom_classifier_" + cc.getId(), c);
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return listOfClassifiers;
	}

	@Override
	public HashMap getOrCreateClassifier(List entrezIds, int classifierType,
			String name, String description, int player_id, HashMap<String, Weka> name_dataset,
			Dataset dataset, HashMap<String, Classifier> custom_classifiers) {
		List<CustomClassifier> ccList = ccRepo.findAll();
		CustomClassifier returncf = new CustomClassifier();
		Boolean exists = false;
		long[] featureDbIds = new long[entrezIds.toArray().length];
		String message = "Classifier training completed.";
		Feature f;
		int ctr = 0;
		for (Object entrezId : entrezIds.toArray()) {
			f = new Feature();
			f = fRepo.findByUniqueId(entrezId.toString());
			featureDbIds[ctr] = f.getId();
			ctr++;
		}
		for (CustomClassifier cf : ccList) {
			if (cf.getName().equals(name)) {
				exists = true;
				returncf = cf;
				message = "Classifier with same name already exists.";
				break;
			}
			List<Feature> fList = cf.getFeatures();
			if (cf.getType() == classifierType) {
				int count = fList.size();
				int match = 0;
				HashSet hs;
				HashSet hs_orig;
				if (count == featureDbIds.length) {
					hs = new HashSet();
					hs_orig = new HashSet(Arrays.asList(featureDbIds));
					for (Feature _f : fList) {
						hs.add(_f.getUnique_id());
					}
					if (hs.containsAll(hs_orig)) {
						exists = true;
						returncf = cf;
						message = "Classifier with same attributes already exists.";
						break;
					}
				}
			}
		}
		HashMap mp = new HashMap();
		if (!exists) {
			try {
				returncf = insertandAddCustomClassifier(featureDbIds,
						classifierType, name, description, player_id, name_dataset,
						dataset, custom_classifiers);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		HashMap results = new HashMap();
		results.put("name", returncf.getName());
		results.put("description", returncf.getDescription());
		results.put("type", returncf.getType());
		results.put("id", returncf.getId());
		results.put("exists", exists);
		results.put("message", message);
		return results;
	}

	@Override
	public CustomClassifier insertandAddCustomClassifier(long[] featureDbIds,
			int classifierType, String name, String description, int player_id,
			HashMap<String, Weka> name_dataset, Dataset dataset,
			HashMap<String, Classifier> custom_classifiers) {
		HashMap mp = new HashMap();
		List<Feature> featureList = new ArrayList<Feature>();
		for (long id : featureDbIds) {
			featureList.add(fRepo.getByDbId(id));
		}
		// Insert into Database
		CustomClassifier newCC = new CustomClassifier();
		User user = userRepo.findById(player_id);
		newCC.setName(name);
		newCC.setType(classifierType);
		newCC.setDescription(description);
		newCC.setFeatures(featureList);
		newCC.setUser(user);
		newCC = ccRepo.saveAndFlush(newCC);
		return newCC;
	}
}
