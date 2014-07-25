package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CollectionRepository extends JpaRepository<Collection, Long> {

	@Query("select C from Collection C where C.id =?1")
	Collection findById(long i);

	@Query("select C from Collection C where C.user =?1")
	List<Collection> findByUserId(User user);

	// select * from Collection where collection id = select coolectioon_id from
	// dataset where dataset
	// @Query("select C from Collection C where C.datasets=?1")
	// List <Collection> findByDatasetId(Dataset dataset);
	//

}
