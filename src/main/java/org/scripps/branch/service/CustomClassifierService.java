package org.scripps.branch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NamedQuery;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Custom_Classifier;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.service.AttributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;



public class CustomClassifierService extends Custom_Classifier{


	public static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("DEFAULTJPA");
	public static EntityManager em = emf.createEntityManager();
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CustomClassifierService.class);

	
	@SuppressWarnings({ "rawtypes" })
	public int insertandAddCustomClassifier(String[] featureDbIds,
			int classifierType, String name, String description, int player_id,
			Weka weka, String dataset,
			HashMap<String, Classifier> custom_classifiers) throws Exception {


		HashMap hashMapObj =new HashMap();
		Custom_Classifier ccObj = new Custom_Classifier();
		int custom_classifier_id=0;

		FilteredClassifier fc = buildCustomClassifier(weka,featureDbIds,classifierType);


		try {
			em.getTransaction().begin();
			ccObj.setName(name);
			ccObj.setType(classifierType);
			ccObj.setDescription(description);
			ccObj.setPlayer_id(player_id);
			em.persist(ccObj);
			em.getTransaction().commit();


			while (!em.contains(ccObj)) {

				custom_classifier_id = ccObj.getId();
			}

			//check it 

			for(String id: featureDbIds){
				//custom_classifier_feature



			}







		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.debug("Creating custom classifier failed, no rows affected.");
		}

		em.close();

		return custom_classifier_id;
	}



	public FilteredClassifier buildCustomClasifier(Weka weka,
			String[] featureDbIds, int classifierType) {
		Instances data = weka.getTrain();
		String att_name = "";
		String indices = new String();
		for (String featureDbId : featureDbIds) {
			List<Attribute> atts = AttributeService.getByFeatureDbId(featureDbId);
			if (atts != null && atts.size() > 0) {
				for (Attribute att : atts) {
					att_name = att.getName();
				}
				indices += String.valueOf(data.attribute(att_name).index() + 1)
						+ ",";
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


	public FilteredClassifier buildCustomClassifier(Weka weka,
			String[] featureDbIds, int classifierType) {

		FilteredClassifier fc = new FilteredClassifier();
		return fc;
	}

	
	
	public FilteredClassifier getClassifierByDbId(int id, int classifierType,
			Weka weka, String dataset) throws Exception {
		
		
		String query = "select ccf from Custom_Classifier_Feature ccf where custom_classifier_id="+ id;
		String[] featuresDbId;
		
		
		try {
			em.getTransaction().begin();
			Query q = em.createQuery(query);
			List<?> list = q.getResultList();
			Iterator<?> it = list.iterator();
			int counter=0;
			while (it.hasNext()) {
				
				Custom_Classifier cc
			
				featuresDbId[counter]= 
				
			}
				
		
		
		
		
		
		
		
		
		
		
		return buildCustomClasifier(weka, featureDbId, classifierType)
	}
	
	
	
	


	public HashMap getClassifierDetailsByDbId(int id, String dataset,
			LinkedHashMap<String, Classifier> custom_classifiers)
					throws Exception {

		HashMap hashMapObj =  new HashMap();
		String query = "select cc from Custom_Classifier where id ="+id;
		int counter=0,counterf=0;
		Custom_Classifier ccObj =new Custom_Classifier();
		HashMap featuresHashMap =new HashMap();

		
			try {
				em.getTransaction().begin();
				Query q = em.createQuery(query);
				List<?> list = q.getResultList();
				Iterator<?> it = list.iterator();

				while (it.hasNext()) {

					ccObj = (Custom_Classifier) it.next();

					hashMapObj.put("id",ccObj.getId());
					hashMapObj.put("name", ccObj.getName());
					hashMapObj.put("type", ccObj.getType());
					hashMapObj.put("description", ccObj.getDescription());
					hashMapObj.put("player_id", ccObj.getPlayer_id());
					hashMapObj.put("created", ccObj.getCreated());

					counter++;
				}

				//custom_Classifier_feature to be done
				String query2 = "select f.short_name, a.name "
						+ "from Custom_Classifier_Feature ccf, Feature f, Attribute a"
						+ "where custom_classifier_id = "+id
						+ " and f.id = ccf.feature_id and "
						+ "a.feature_id = f.id and a.dataset='"
						+ dataset + "'";

				q = em.createQuery(query2);
				list = q.getResultList();
				it = list.iterator();

				while (it.hasNext()) {

					Object[] obj = (Object[]) it.next();
					featuresHashMap.put((String)obj[0], (String) obj[1]);
					counterf++;

				}

				String classifierString =custom_classifiers.get("custom_classifier_"+id).toString();

				hashMapObj.put("features", featuresHashMap);
				hashMapObj.put("classifierString",classifierString);


				LOGGER.debug("Counter feature =" + counterf);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			em.close();
		return hashMapObj;
	}








	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList searchCustomClassifier(String description){

		ArrayList results = new ArrayList();
		HashMap hashMapObj =new HashMap();

		String query =  "select cf from Custom_Classifier where name like '%"+description+
				"%' or description like '%"+description+"%'";
		Custom_Classifier ccObj = new Custom_Classifier();
		int counter =0;
		try {
			em.getTransaction().begin();
			Query q = em.createQuery(query);
			List<?> list = q.getResultList();
			Iterator<?> it = list.iterator();

			while (it.hasNext()) {

				ccObj = (Custom_Classifier) it.next();
				hashMapObj.put("name", ccObj.getName());
				hashMapObj.put("description", ccObj.getDescription());
				hashMapObj.put("id", "custom_classifier_"+ccObj.getId());

				counter++;
				LOGGER.debug("Custom_Classifier object" + ccObj.toString());
				results.add(hashMapObj);
			}
			LOGGER.debug("Counter =" + counter);
			em.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;
	}



}
