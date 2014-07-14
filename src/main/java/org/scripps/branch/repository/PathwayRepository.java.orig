package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Pathway;
import org.springframework.data.jpa.repository.Query;

public interface PathwayRepository {

	@Query("select f from Pathway p,Feature f where p.name=?1 and p.entrez_id =f.unique_id group by c.entrez_id")
	List<Pathway> getGenesOfPathways(String pathwayName);

	@Query("select p from Pathway p where p.name LIKE concat('%',concat(?1,'%')) group by name")
	List<Pathway> searchPathways(String name);
}
