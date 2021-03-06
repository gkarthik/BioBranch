package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Pathway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PathwayRepository extends JpaRepository<Pathway, Long> {

	@Query("select p from Pathway p where p.name = ?1 and p.source_db=?2")
	Pathway findByNameAndSourcedb(String name, String source_db);

	@Query("select p from Pathway p where p.name like concat('%',concat(?1,'%'))")
	List<Pathway> searchPathways(String name);
	
	Pathway findById(long id);
}
