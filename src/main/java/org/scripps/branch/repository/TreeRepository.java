package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TreeRepository extends JpaRepository<Tree, Long> {

	Tree findById(long id);

	// Includes private trees
	@Query("select t from Tree t where t.user=?1 and t.user_saved=true ")
	List<Tree> findByUser(User user);

	// Community Collection
	@Query("select t from Tree t where user_saved=true and t.private_tree=false")
	List<Tree> getAllTrees();

	// Does not include private trees
	@Query("select t from Tree t where t.user=?1 and t.private_tree=false and t.user_saved=true")
	List<Tree> getByOtherUser(User user);

	// COmmunity view
	@Query("select t from Tree t where t.user_saved = true and t.private_tree = false and (t.json_tree like concat(concat('%',?1),'%') or t.comment like concat(concat('%',?1),'%'))")
	List<Tree> getTreesBySearch(String query);

}