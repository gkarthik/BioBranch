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

	@Query("select cf from CustomFeature cf where cf.name=?1")
	CustomFeature findByName(String name);

	@Query("select cf from CustomFeature cf where cf.name like concat('%',concat(?1,'%')) or cf.description like concat('%',concat(?1,'%'))")
	List<CustomFeature> searchCustomFeatures(String searchText);
	
	@Query("select f from CustomFeature cf inner join cf.feature f inner join f.attributes a where cf=?1 and a.dataset=?2 group by f")
	List<Feature> getAttrDatasets(CustomFeature cf, Dataset d);
}
