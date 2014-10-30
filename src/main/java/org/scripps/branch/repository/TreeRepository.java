package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TreeRepository extends JpaRepository<Tree, Long> {

	Tree findById(long id);

	// Includes private trees
	@Query("select t from Tree t where t.user=?1 and t.user_saved=true order by t.score.score")
	List<Tree> findByUser(User user);

	// Community Collection
	@Query("select t from Tree t where user_saved=true and t.private_tree=false order by t.score.score")
	List<Tree> getAllTrees();

	// Does not include private trees
	@Query("select t from Tree t where t.user=?1 and t.private_tree=false and t.user_saved=true order by t.score.score")
	List<Tree> getByOtherUser(User user);

	@Query("select count(f) from Tree t inner join t.customClassifiers f where f in (?1) and t.user != ?2")
	long getCountOfCustomClassifier(List<CustomClassifier> fList, User user);

	@Query("select count(f) from Tree t inner join t.customFeatures f where f in (?1) and t.user != ?2")
	long getCountOfCustomFeature(List<CustomFeature> fList, User user);

	@Query("select count(f) from Tree t inner join t.customTreeClassifiers f where f in (?1) and t.user != ?2")
	long getCountOfCustomTree(List<Tree> fList, User user);

	@Query("select count(f) from Tree t inner join t.features f where f in (?1) and t.user != ?2")
	long getCountOfFeature(List<Feature> fList, User user);

	@Query("select count(f) from Tree t inner join t.features f")
	long getTotalCount();

	// Community view
	@Query("select t from Tree t where t.user_saved = true and t.private_tree = false and t.score.dataset=?2 and (t.json_tree like concat(concat('%',?1),'%') or t.comment like concat(concat('%',?1),'%'))")
	List<Tree> getTreesBySearch(String query, Dataset d);
	
	@Query("select count(t) from Tree t")
	long getCount();

}