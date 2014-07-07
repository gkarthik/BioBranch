package org.scripps.branch.repository;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Feature;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

@Transactional
public interface FeatureRepository extends JpaRepository<Feature, Long>{
	
	@Query("select F from Feature F where F.id=?1")
	Feature getByDbId(long id);
	
	@Query("select F from Feature F where F.unique_id =?1")
	Feature findByUniqueId(String unique_id);
	
	//updatebyuniqueid need to check if parameters to be passes	
	@Query("update Feature set short_name=?1, long_name=?2, description=?3 where unique_id=?4")
	public int updateByUniqueId(String short_name,String  long_name,String  description, String unique_id); 
	
	
	//insert?

}
