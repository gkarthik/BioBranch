package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {

	@Query("select D from Dataset D where collection=?1")
	List<Dataset> findByCollectionId(Collection coll);

	@Query("select D from Dataset D where id=?1")
	Dataset findById(long id);
}
