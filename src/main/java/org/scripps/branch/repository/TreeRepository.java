package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Tree;
import org.springframework.data.repository.Repository;

public interface TreeRepository extends Repository<Tree, Long> {
	List<Tree> findById(int Id);
}