package org.scripps.branch.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.CustomSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Transactional
public interface CustomSetRepository extends JpaRepository<CustomSet, Long> {

	CustomSet findById(long id);

}