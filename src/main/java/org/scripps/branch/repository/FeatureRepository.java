package org.scripps.branch.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.scripps.branch.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Transactional
public interface FeatureRepository extends JpaRepository<Feature, Long> {

	@Query("select F from Feature F where F.unique_id =?1")
	Feature findByUniqueId(String unique_id);

	@Query("select F from Feature F where F.id=?1")
	Feature getByDbId(long id);

	// not tested -- s
	@Query("select F from Feature F, Attribute A where A.dataset='metabric_with_clinical' and F.id = A.feature and F.unique_id like '%metabric%'")
	List<Feature> getMetaBricClinicalFeatures();

}
