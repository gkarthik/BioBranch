package org.scripps.branch.repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.Weka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

@Repository
@Transactional
public class CustomClassifierCustomRepositoryImpl implements
		CustomClassifierCustomRepository {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FeatureCustomRepositoryImpl.class);

	protected EntityManager em;

	@Autowired
	AttributeRepository attr;

	CustomClassifierRepository ccr;

	@Override
	public FilteredClassifier buildCustomClassifier(Weka weka,
			Long[] featureDbIds, int classifierType) {
		Instances data = weka.getTrain();
		String att_name = "";
		String indices = new String();
		for (Long featureDbId : featureDbIds) {
			List<Attribute> atts = attr.findByFeatureDbId(featureDbId);
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

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap getClassifierDetailsByDbId(int id, String dataset,
			LinkedHashMap<String, Classifier> custom_classifiers) {

		HashMap hashMapObj = new HashMap();
		String query = "select cc from Custom_Classifier where id =" + id;
		int counter = 0, counterf = 0;
		CustomClassifier ccObj = new CustomClassifier();
		HashMap featuresHashMap = new HashMap();

		try {

			Query q = em.createQuery(query);
			List<?> list = q.getResultList();
			Iterator<?> it = list.iterator();

			while (it.hasNext()) {

				ccObj = (CustomClassifier) it.next();

				hashMapObj.put("id", ccObj.getId());
				hashMapObj.put("name", ccObj.getName());
				hashMapObj.put("type", ccObj.getType());
				hashMapObj.put("description", ccObj.getDescription());
				hashMapObj.put("player_id", ccObj.getUser());
				hashMapObj.put("created", ccObj.getCreated());

				counter++;
			}

			// custom_Classifier_feature to be done
			String query2 = "select f.short_name, a.name "
					+ "from Custom_Classifier_Feature ccf, Feature f, Attribute a"
					+ "where custom_classifier_id = " + id
					+ " and f.id = ccf.feature_id and "
					+ "a.feature_id = f.id and a.dataset='" + dataset + "'";

			q = em.createQuery(query2);
			list = q.getResultList();
			it = list.iterator();

			while (it.hasNext()) {

				Object[] obj = (Object[]) it.next();
				featuresHashMap.put(obj[0], obj[1]);
				counterf++;

			}

			String classifierString = custom_classifiers.get(
					"custom_classifier_" + id).toString();

			hashMapObj.put("features", featuresHashMap);
			hashMapObj.put("classifierString", classifierString);

			LOGGER.debug("Counter feature =" + counterf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		em.close();
		return hashMapObj;
	}

	@Override
	public LinkedHashMap<String, Classifier> getClassifiersfromDb() {

		String query = "select CC from Custom_Classifier CC";
		Query q = em.createQuery(query);

		List<?> list = q.getResultList();
		Iterator<?> it = list.iterator();
		LinkedHashMap<String, Classifier> listOfClassifiers = new LinkedHashMap<String, Classifier>();
		long id = -1;
		int classifierType = 0;
		list = q.getResultList();
		it = list.iterator();

		while (it.hasNext()) {

			CustomClassifier obj = (CustomClassifier) it.next();
			id = obj.getId();
			classifierType = obj.getType();
			try {
				listOfClassifiers.put("custom_classifier_" + id,
						ccr.getClassifierByCustomClassifierId(id));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return listOfClassifiers;
	}

	// to be done
	// public HashMap getOrCreateClassifierId(List entrezIds, int
	// classifierType,
	// String name, String description, int player_id, Weka weka,
	// String dataset, HashMap<String, Classifier> custom_classifiers)
	//

	// check if necessary
	// public int insertandAddCustomClassifier(String[] featureDbIds,
	// int classifierType, String name, String description, int player_id,
	// Weka weka, String dataset,
	// HashMap<String, Classifier> custom_classifiers)
}
