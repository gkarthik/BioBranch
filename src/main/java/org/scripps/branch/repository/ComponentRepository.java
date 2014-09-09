package org.scripps.branch.repository;

import javax.transaction.Transactional;

import org.scripps.branch.entity.Component;
import org.scripps.branch.entity.CustomSet;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface ComponentRepository extends JpaRepository<Component, Long> {

	Component findById(long id);

}