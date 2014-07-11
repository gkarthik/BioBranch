package org.scripps.branch.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;

public interface PathwayRepository {

	@Query("select f.short_name,f.long_name, c.entrez_id,c.source_db from Pathway p,Feature f where p.name=?1 and p.entrez_id =f.unique_id group by c.entrez_id")
	ArrayList<?> getGenesOfPathways(String pathwayName);

	@Query("select p from Pathway p where p.name LIKE ('%' || (:name) || '%')) group by name")
	ArrayList<?> searchPathways(String name);
}
