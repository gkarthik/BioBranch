package org.scripps.branch.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Repository
@Transactional
public class FeatureCustomRepositoryImpl implements FeatureCustomRepository {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FeatureCustomRepositoryImpl.class);

	protected EntityManager em;

	private List<Attribute> dataset_attributes;

	@Override
	public Map<String, Feature> getByDataset(String dataset,
			boolean load_annotations_very_slowly) {

		Map<String, Feature> features = new HashMap<String, Feature>();

		String query = "select f.id,f.unique_id,f.short_name,f.long_name,f.description,f.created,f.updated,"
				+ "a.id,a.col_index,a.name,a.dataset,a.relieff,a.created,a.updated,a.feature"
				+ " from Feature f, Attribute a "
				+ "where a.dataset = '"
				+ dataset + "'and f.id = feature_id";

		try {

			Query q = em.createQuery(query);

			List<?> list = q.getResultList();
			LOGGER.debug("Feature Counter =");
			Iterator<?> it = list.iterator();
			int featureCounter = 0, attributeCounter = 0;

			while (it.hasNext()) {

				Object[] result = (Object[]) it.next();

				Feature featureObject = features.get(result[1]);// check for
																// f.uniqueid

				if (featureObject == null) {

					featureObject = new Feature((Long) result[0],
							(String) result[1], (String) result[2],
							(String) result[3], (String) result[4],
							(DateTime) result[5], (DateTime) result[6]);
					featureCounter++;

				}

				LOGGER.debug("Feature Counter : " + featureCounter);

				List<Attribute> atts = getDataset_attributes();
				if (atts == null) {

					atts = new ArrayList<Attribute>();
				}
				Attribute attributeObject = new Attribute((int) result[7],
						(int) result[8], (String) result[9],
						(String) result[10], (float) result[11],
						(DateTime) result[12], (DateTime) result[13]);

				attributeObject.setFeature((Feature) result[14]);

				attributeCounter++;
				if (!atts.contains(attributeObject)) {
					atts.add(attributeObject);
					setDataset_attributes(atts);
				}
				if (load_annotations_very_slowly) {

					// f.getAnnotationsFromDb();
					// f.getTextAnnotationsFromDb();

				}

				features.put(featureObject.getUnique_id(), featureObject);

				LOGGER.debug("FeatureObj: " + featureObject.toString());
				LOGGER.debug("AttributeObj: " + attributeObject.toString());

			}
			LOGGER.debug("Feature Counter =" + featureCounter);
			LOGGER.debug("Attribute Counter =" + attributeCounter);

			em.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return features;

	}

	public List<Attribute> getDataset_attributes() {
		return dataset_attributes;
	}

	public void getEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public ObjectNode getMetaBricClinicalFeatures(ObjectMapper mapper) {

		ObjectNode featureObject = mapper.createObjectNode();
		String query = "select f.id,f.unique_id,f.short_name,"
				+ "f.long_name,f.description" + " from Feature f, Attribute a "
				+ "where a.dataset='metabric_with_clinical' and "
				+ "f.id = feature_id and " + "f.unique_id like 'metabric%'";

		try {

			Query q = em.createQuery(query);
			List<?> list = q.getResultList();
			Iterator<?> it = list.iterator();
			int featureCounter = 0;
			ArrayNode featureArrayNode = mapper.createArrayNode();

			while (it.hasNext()) {

				Object[] result = (Object[]) it.next();

				ObjectNode fObj = mapper.createObjectNode();

				fObj.put("id", (long) result[0]);
				fObj.put("unique_id", (String) result[1]);
				fObj.put("short_name", (String) result[2]);
				fObj.put("long_name", (String) result[3]);
				fObj.put("description", (String) result[4]);
				featureArrayNode.add(fObj);
				featureCounter++;
			}

			featureObject.put("features", featureArrayNode);
			em.getTransaction().commit();

			LOGGER.debug("FeatureCounter for getMetaBricClinicalFeatures :"
					+ featureCounter);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return featureObject;

	}

	public void setDataset_attributes(List<Attribute> dataset_attributes) {
		this.dataset_attributes = dataset_attributes;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
		LOGGER.debug("entity manager set from Impl!");
	}

}
