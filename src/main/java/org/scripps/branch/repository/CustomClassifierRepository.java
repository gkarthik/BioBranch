package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomClassifierRepository extends
		JpaRepository<CustomClassifier, Long> {

	CustomClassifier findById(long id);

	@Query("select C.feature from CustomClassifier C where C.id=?1")
	List<Feature> getClassifierByCustomClassifierId(long id);

	@Query("select C from CustomClassifier C where C.name like concat('%',concat(?1,'%')) or C.Description like concat('%',concat(?1,'%'))")
	List<CustomClassifier> searchCustomClassifiers(String name);
}
