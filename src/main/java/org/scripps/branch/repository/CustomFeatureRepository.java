package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFeatureRepository extends
		JpaRepository<CustomFeature, Long> {
	CustomFeature findById(long id);

	@Query("select cf from CustomFeature cf where cf.name=?1")
	CustomFeature findByName(String name);

	@Query("select cf from CustomFeature cf where cf.name like concat('%',concat(?1,'%')) or cf.description like concat('%',concat(?1,'%'))")
	List<CustomFeature> searchCustomFeatures(String searchText);
	
	@Query("select count(*) from CustomFeature cf, Feature f, Attribute a, Dataset d where cf = ?1 and f IN cf.feature and a IN f.attributes and a.dataset.collection = ?2")
	int getAttrDatasets(CustomFeature c, Collection col);
}
