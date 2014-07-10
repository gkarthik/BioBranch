package org.scripps.branch.repository;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.scripps.branch.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

	@Query("select F from Feature F where F.unique_id =?1")
	Feature findByUniqueId(String unique_id);

	@Query("select F from Feature F where F.id=?1")
	Feature getByDbId(long id);

	@Query("select F from Feature F, Attribute A where A.dataset='metabric_with_clinical' and F.id = A.feature and F.unique_id like '%metabric%'")
	ArrayList<Feature> getMetaBricClinicalFeatures();

}
