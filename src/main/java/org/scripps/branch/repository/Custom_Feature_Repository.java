package org.scripps.branch.repository;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.scripps.branch.entity.Feature;
import org.springframework.data.jpa.repository.Query;



@Transactional
public interface Custom_Feature_Repository {
	
	
@SuppressWarnings("rawtypes")
@Query("select cf from custom_feature cf " +
		       "where (cf.name like ('%' || lower(:searchText) || '%')) " +
	         " or (cf.description like ('%' || lower(:searchText) || '%'))")
	
	ArrayList searchCustomFeatures(String searchText);	



public int insert(String name, String feature_exp, String description,
		int userid, List<Feature> features, String dataset);
}
