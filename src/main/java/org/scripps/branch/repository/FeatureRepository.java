package org.scripps.branch.repository;

import java.util.ArrayList;

import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface FeatureRepository extends JpaRepository<Feature, Long> {
	
	@Transactional(readOnly = true)
	@Query("select F from Feature F where F.unique_id =?1")
	Feature findByUniqueId(String unique_id);

	@Query("select F from Feature F where F.id=?1")
	Feature getByDbId(long id);

	@Query("select F from Feature F, Attribute A where A.dataset=?1 and F.id = A.feature and F.isGene is not true")
	ArrayList<Feature> getNonGeneFeatures(Dataset d);
	
	@Query("select F from Feature F, Attribute A where A.dataset=?1 and F.id = A.feature and F.isGene is not true and (F.short_name like concat('%',concat(?2,'%')))")
	ArrayList<Feature> searchNonGeneFeatures(Dataset d, String query);
	
	@Query("select count(F) from Feature F, Attribute A where A.dataset=?1 and F.id = A.feature and F.isGene is not true")
	long getCountOfNonGeneFeature(Dataset d);
	
	@Query("select count(F) from Feature F, Attribute A where A.dataset=?1 and F.id = A.feature")
	long getCountOfFeatures(Dataset d);
}
