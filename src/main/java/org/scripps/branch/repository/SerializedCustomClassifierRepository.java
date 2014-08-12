package org.scripps.branch.repository;

import java.util.List;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.SerializedCustomClassifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SerializedCustomClassifierRepository extends JpaRepository<SerializedCustomClassifier, Long> {
	
	SerializedCustomClassifier findById(long id);
	
	List<SerializedCustomClassifier> findAll();
	
}