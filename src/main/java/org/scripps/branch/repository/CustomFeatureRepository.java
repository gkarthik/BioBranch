package org.scripps.branch.repository;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.scripps.branch.entity.CustomFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Transactional
public interface CustomFeatureRepository extends
		JpaRepository<CustomFeature, Long> {
	@SuppressWarnings("rawtypes")
	@Query("select cf from CustomFeature cf " + "where (cf.name like ('%' || (:searchText) || '%')) " + " or (cf.description like ('%' || (:searchText) || '%'))")
	List<CustomFeature> searchCustomFeatures(String searchText);
	
	@Query("select cf from CustomFeature cf " + "where cf.name=?1")
	CustomFeature findByName(String name);
}
