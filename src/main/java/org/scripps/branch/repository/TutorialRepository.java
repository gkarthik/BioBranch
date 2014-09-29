package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.Tutorial;
import org.scripps.branch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Long> {

	Tutorial findById(long id);
}
