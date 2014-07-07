package Tests;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestService extends Feature {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TestService.class);

	public static void main(String args[]) {

		// loadFeatureTable();
		TestService a = new TestService();
		// FeatureService.getByUniqueId("17");
		// / FeatureService.getByDbId(1);
		// FeatureService.getByDataset("dream_breast_cancer", false);

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode features = a.getMetaBricClinicalFeatures(mapper);
		String json_features;
		try {
			json_features = mapper.writeValueAsString(features);
			System.out.println(json_features);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// @PersistenceContext(unitName = "DEFAULTJPA", type =
	// PersistenceContextType.EXTENDED)
	//
	// public static EntityManagerFactory emf = Persistence
	// .createEntityManagerFactory("DEFAULTJPA");
	// public static EntityManager em = emf.createEntityManager();
	//
	//
	@PersistenceUnit(unitName = "DEFAULTJPA")
	private EntityManager em;

	private List<Attribute> dataset_attributes;

	public TestService() {
		super();

	}

	public List<Attribute> getDataset_attributes() {
		return dataset_attributes;
	}

	public ObjectNode getMetaBricClinicalFeatures(ObjectMapper mapper) {

		ObjectNode featureObject = mapper.createObjectNode();
		String query = "select f.id,f.unique_id,f.short_name,"
				+ "f.long_name,f.description" + " from Feature f, Attribute a "
				+ "where a.dataset='metabric_with_clinical' and "
				+ "f.id = feature_id and " + "f.unique_id like 'metabric%'";

		try {

			em.getTransaction().begin();
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
			em.close();
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
}