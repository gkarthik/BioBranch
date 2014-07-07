package org.scripps.branch.repository;

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
	@Query("select F,A from Feature F, Attribute A where A.dataset='metabric_with_clinical' and F.id = feature_id and F.unique_id like 'metabric%'")
	Feature getMetaBricClinicalFeatures();

	// updatebyuniqueid need to check if parameters to be passes
	@Query("update Feature set short_name=?1, long_name=?2, description=?3 where unique_id=?4")
	public int updateByUniqueId(String short_name, String long_name,
			String description, String unique_id);

}
