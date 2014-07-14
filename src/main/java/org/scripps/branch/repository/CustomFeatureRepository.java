package org.scripps.branch.repository;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.scripps.branch.entity.CustomFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFeatureRepository extends
		JpaRepository<CustomFeature, Long> {
	@Query("select cf from CustomFeature cf where cf.name like concat('%',concat(?1,'%')) or cf.description like concat('%',concat(?1,'%'))")
	List<CustomFeature> searchCustomFeatures(String searchText);
	
	@Query("select cf from CustomFeature cf where cf.name=?1")
	CustomFeature findByName(String name);
	
	CustomFeature findById(long id);
}
