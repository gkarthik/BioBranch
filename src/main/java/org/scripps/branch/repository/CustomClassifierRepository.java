package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomClassifierRepository extends
		JpaRepository<CustomClassifier, Long> {

	CustomClassifier findById(long id);

	@Query("select C.features from CustomClassifier C where C.id=?1")
	List<Feature> getClassifierByCustomClassifierId(long id);

	@Query("select C from CustomClassifier C, Attribute A inner join C.features F where A.feature = F and (C.name like concat('%',concat(?1,'%')) or C.Description like concat('%',concat(?1,'%'))) and A.dataset=?2 group by C")
	List<CustomClassifier> searchCustomClassifiers(String name, Dataset d);
	
	@Query("select count(cc) from CustomClassifier cc")
	long getCount();
}
