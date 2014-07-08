package org.scripps.branch.repository;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;

@Transactional
public interface CustomFeatureRepository {

	// @Query("insert into custom_feature(name,expression, description, user, dataset) values(1?,2?,3?,4?,5?)")
	// public int insert(String name, String feature_exp, String description,
	// int user, String dataset);

	@SuppressWarnings("rawtypes")
	@Query("select cf from custom_feature cf "
			+ "where (cf.name like ('%' || (:searchText) || '%')) "
			+ " or (cf.description like ('%' || (:searchText) || '%'))")
	ArrayList searchCustomFeatures(String searchText);

	// custom_Feature_Feature

}
