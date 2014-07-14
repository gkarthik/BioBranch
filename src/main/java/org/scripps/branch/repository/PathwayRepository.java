package org.scripps.branch.repository;


import java.util.List;





import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Pathway;

@Repository
public interface PathwayRepository extends JpaRepository<Pathway, Long> {

	@Query("select p from Pathway p where p.name like concat('%',concat(?1,'%'))")
	List<Pathway> searchPathways(String name);
	
	Pathway findByName(String name);
}
	
