package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFeatureRepository extends
		JpaRepository<CustomFeature, Long> {
	
	CustomFeature findById(long id);

	CustomFeature findByName(String name);
	
	@Query("select count(cf) from CustomFeature cf where cf.dataset.collection=?1")
	long getCountFromCollection(Collection c);
	
	@Query("select cf from CustomFeature cf where cf.name = ?1 and cf.dataset.collection=?2")
	CustomFeature findByNameAndCollection(String name, Collection c);

	@Query("select cf from CustomFeature cf where (cf.name like concat('%',concat(?1,'%')) or cf.description like concat('%',concat(?1,'%'))) and cf.dataset.collection = ?2")
	List<CustomFeature> searchCustomFeatures(String searchText, Collection c);
}
