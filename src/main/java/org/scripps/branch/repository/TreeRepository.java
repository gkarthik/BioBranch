package org.scripps.branch.repository;

import java.util.ArrayList;
import java.util.List;

import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TreeRepository extends JpaRepository<Tree, Long> {

	Tree findById(long id);
	
	@Query("select t from Tree t where t.json_tree like concat(concat('%',?1),'%') or t.comment like concat(concat('%',?1),'%')")
	List<?> getTreesBySearch(String query);

}