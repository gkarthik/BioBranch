package org.scripps.branch.repository;


import java.util.List;

import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
	
	@Query("select d from Dataset d where user=?1")
	List<Dataset> findByUser(User user);
}
