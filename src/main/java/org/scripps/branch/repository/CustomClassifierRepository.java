package org.scripps.branch.repository;

import java.util.ArrayList;
import java.util.List;

import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomClassifierRepository extends
		JpaRepository<CustomClassifier, Long> {

	@Query("select C.feature from CustomClassifier C where C.id=?1")
	List<Feature> getClassifierByCustomClassifierId(long id);

	@Query("select CC from CustomClassifier CC where name LIKE concat('%',concat(:name,'%')) or description LIKE concat('%',concat(:name,'%'))")
	ArrayList<CustomClassifier> searchCustomClassifiers(String name);
	
	CustomClassifier findById(long id);
}
