package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AttributeRepository extends JpaRepository<Attribute, Long> {

	@Query("select A from Attribute A where A.name=?1 and A.dataset =?2")
	List<Attribute> findByAttNameDataset(String att_name, String dataset);

	@Query("select A from Attribute A where feature_id= ?1 ")
	List<Attribute> findByFeatureDbId(long db_id);
	
	List<Attribute> findByDataset(Dataset d);
	
	Attribute findByNameAndDataset(String name, Dataset d);
 
	@Query("select A from Attribute A, Feature F where A.feature=F.id and F.unique_id=?1 and A.dataset=?2")
	List<Attribute> findByFeatureUniqueId(String unique_id, Dataset dataset);

}

// select * from Attribute A where A.name='ILMN_1722781' and A.dataset
// ='metabric_with_clinical'