package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreeRepository extends JpaRepository<Tree, Long> {
	Tree findById(int Id);

	List<Tree> findByUser(User newUser);
}