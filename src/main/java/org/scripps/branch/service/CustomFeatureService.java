package org.scripps.branch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Custom_Feature;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Weka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.core.AttributeExpression;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.AddExpression;

public class CustomFeatureService extends Custom_Feature{

	private String name;
	private String expression;

	public static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("DEFAULTJPA");
	public static EntityManager em = emf.createEntityManager();
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CustomFeatureService.class);

	public void addInstances(Weka weka) {

		Custom_Feature cfObj = new Custom_Feature();
		String query = "selecrt cf from Custom_Feature cf";
		try {
			em.getTransaction().begin();
			Query q = em.createQuery(query);
			List<?> list = q.getResultList();
			Iterator<?> it = list.iterator();
			int counter = 0;

			while (it.hasNext()) {

				cfObj = (Custom_Feature) it.next();
				evalAndAddNewFeatureValues("custom_feature_" + cfObj.getId(),
						cfObj.getExpression(), weka.getTrain());

				counter++;
				LOGGER.debug("Custom_Feat add Instance cfObj" + cfObj);
			}
			LOGGER.debug("Counter =" + counter);
			em.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int evalAndAddNewFeatureValues(String feature_name,
			String featureExpression, Instances data) {

		int attIndex = 0;

		AddExpression newFeature = new AddExpression();
		newFeature.setExpression(featureExpression);
		weka.core.Attribute attr = new weka.core.Attribute(feature_name);
		attIndex = data.numAttributes() - 1;
		data.insertAttributeAt(attr, attIndex);

		try {
			newFeature.setInputFormat(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < data.numInstances(); i++) {// Index here starts from
														// 0.
			try {
				newFeature.input(data.instance(i));
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap findOrCreateCustomFeature(String feature_name, String exp,
			String description, int user_id, Weka weka, String dataset) {

		HashMap hashMapObj = new HashMap();
		hashMapObj.put("success", true);
		Custom_Feature cfObj = new Custom_Feature();
		Instances data = weka.getTrain();

		List<Feature> allFeature = new ArrayList<Feature>();

		Pattern pattern = Pattern.compile("@([A-Za-z0-9])+");
		Matcher match = pattern.matcher(exp);

		String entrezid = "";
		String att_name = "";

		int index = 0;
		while (match.find()) {
			entrezid = match.group().replace("@", "");
			List<Attribute> atts = AttributeService.getByFeatureUniqueId(
					entrezid, dataset);
			if (atts != null && atts.size() > 0) {
				for (Attribute att : atts) {
					att_name = att.getName();

				}
				index = data.attribute(att_name).index();
				allFeature.add(FeatureService.getByUniqueId(entrezid));
				index++;
				exp = exp.replace("@" + entrezid, "a" + index);

			} else {
				hashMapObj.put("message",
						"NotCreated. Couldn't map Genes to Dataset. ");
				hashMapObj.put("success", false);
			}

		}
		try {
			HashMap hashMapObjTemp = findOrCreateCustomFeatureId(feature_name,
					exp, description, user_id, allFeature, weka, dataset);

			if (hashMapObjTemp.containsKey("feature_id")) {
				int cFeatureId = (int) hashMapObjTemp.get("feature_id");
				hashMapObj.put("exists", hashMapObjTemp.get("exists"));
				hashMapObj.put("message", hashMapObjTemp.get("message"));

				String query = "select cf from Custom_Feature cf where id="
						+ cFeatureId;

				//
				try {
					em.getTransaction().begin();
					Query q = em.createQuery(query);
					List<?> list = q.getResultList();
					Iterator<?> it = list.iterator();
					int counter = 0;

					while (it.hasNext()) {

						cfObj = (Custom_Feature) it.next();
						hashMapObj.put("id", "custom_feature_" + cfObj.getId());
						hashMapObj.put("description", cfObj.getDescription());
						hashMapObj.put("name", cfObj.getName());

						counter++;
						LOGGER.debug("HashMapObj" + hashMapObj.toString());
					}
					LOGGER.debug("Counter =" + counter);
					em.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				hashMapObj.put("success", false);
				hashMapObj.put("message", hashMapObjTemp.get("message"));

			}
		} catch (Exception e) {
			e.printStackTrace();
			hashMapObj.put("success", false);

		}
		return hashMapObj;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap findOrCreateCustomFeatureId(String name, String feature_exp,
			String description, int userid, List<Feature> features, Weka weka,
			String dataset) {

		HashMap hashMapObj = new HashMap();

		int cFeatureId = 0;

		String query = "select cf from Custom_Feature cf";
		AttributeExpression _attrExp = new AttributeExpression();
		Custom_Feature cfObj = new Custom_Feature();

		try {

			_attrExp.convertInfixToPostfix(feature_exp);

			em.getTransaction().begin();
			Query q = em.createQuery(query);
			List<?> list = q.getResultList();
			Iterator<?> it = list.iterator();
			int counter = 0;

			String exp = "";
			Boolean exists = false;
			String message = "";

			while (it.hasNext()) {

				cfObj = (Custom_Feature) it.next();
				exp = cfObj.getExpression();
				_attrExp.convertInfixToPostfix(exp);

				if (exp.equals(feature_exp)) {
					exists = true;
					cFeatureId = cfObj.getId();
					message = "Feature already Exists";
				} else if (cfObj.getName().equals(name)) {
					exists = true;
					cFeatureId = cfObj.getId();
					message = "Feature name has already been taken.";

				}
				counter++;
			}
			LOGGER.debug("Counter " + counter);
			if (!exists) {

				cFeatureId = insert(name, feature_exp, description, userid,
						features, dataset);
				evalAndAddNewFeatureValues("custom_feature_" + cFeatureId,
						feature_exp, weka.getTrain());
				message = "Feature has been successfully created.";

			}

			em.getTransaction().commit();
			em.close();

			hashMapObj.put("feature_id", cFeatureId);
			hashMapObj.put("exists", exists);
			hashMapObj.put("messgages", message);

		} catch (Exception e) {
			e.printStackTrace();
			hashMapObj
					.put("message",
							"Expression could not be parsed.<br>Please check if all the attributes have been tagged. Refer to Help Section.");

		}

		return hashMapObj;

	}

	public String getExpression() {
		return expression;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("rawtypes")
	public HashMap getTestCase(String id, Weka weka) {

		// to work on custom_feature_feature

		HashMap hashMapObj = new HashMap();

		return hashMapObj;
	}

	public int insert(String name, String feature_exp, String description,
			int userid, List<Feature> features, String dataset)
			throws Exception {

		int id = 0;
		em.getTransaction().begin();
		Custom_Feature cfObj = new Custom_Feature();
		cfObj.setName(name);
		cfObj.setExpression(feature_exp);
		cfObj.setDescription(description);
		cfObj.setPlayer_id(userid);
		cfObj.setDataset(dataset);

		em.persist(cfObj);
		em.getTransaction().commit();

		while (!em.contains(cfObj)) {

			id = cfObj.getId();
		}

		em.close();

		// work needed for custom_Featurwe_feature

		return id;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList searchCustomFeatures(String name) {

		ArrayList aListObj = new ArrayList();
		HashMap hashMapObj = new HashMap();
		Custom_Feature cfObj = new Custom_Feature();

		String query = " select cf from Custom_Feature cf"
				+ " where cf.name like '%" + name + "%' or"
				+ " cf.description like '%" + name + "%'";
		// name,description, feature_exp,custom_feature_id
		try {

			em.getTransaction().begin();
			Query q = em.createQuery(query);
			List<?> list = q.getResultList();
			Iterator<?> it = list.iterator();
			int counter = 0;

			while (it.hasNext()) {

				cfObj = (Custom_Feature) it.next();
				hashMapObj.put("name", cfObj.getName());
				hashMapObj.put("description", cfObj.getDescription());
				hashMapObj.put("feature_exp", cfObj.getExpression());
				hashMapObj.put("custom_feature_id",
						"custom_feature_" + cfObj.getId());

				aListObj.add(hashMapObj);

				counter++;
				LOGGER.debug("HashMapObj" + hashMapObj.toString());
			}
			LOGGER.debug("Counter =" + counter);
			em.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return aListObj;

	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setName(String name) {
		this.name = name;
	}

}
