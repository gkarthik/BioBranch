package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Pathway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PathwayRepository extends JpaRepository<Pathway, Long> {

	Pathway findByName(String name);

	@Query("select p from Pathway p where p.name like concat('%',concat(?1,'%'))")
	List<Pathway> searchPathways(String name);
}
