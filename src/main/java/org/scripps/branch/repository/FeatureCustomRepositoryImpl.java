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

@Repository
@Transactional
public class FeatureCustomRepositoryImpl implements FeatureCustomRepository {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FeatureCustomRepositoryImpl.class);

	protected EntityManager em;

	@Override
	public Map<String, Feature> getByDataset(String dataset, boolean load_annotations_very_slowly) {
		Map<String, Feature> features = new HashMap<String, Feature>();
		String query = "select f from Feature f, Attribute a where a.dataset = '"	+ dataset + "'and f.id = a.feature_id";
		Query q = em.createQuery(query);
		List<Feature> fList = q.getResultList();
		for(Feature f:fList){
			features.put(f.getUnique_id(), f);
		}
		return features;

	}

	public void getEntityManager(EntityManager em) {
		this.em = em;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
		LOGGER.debug("entity manager set from Impl!");
	}

}
