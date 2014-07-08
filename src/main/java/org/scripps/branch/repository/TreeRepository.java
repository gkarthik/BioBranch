package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Tree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface TreeRepository extends JpaRepository<Tree, Long> {
	Tree findById(int Id);
}