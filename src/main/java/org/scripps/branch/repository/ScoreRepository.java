package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Pathway;
import org.scripps.branch.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

	Score findById(long id);
}
