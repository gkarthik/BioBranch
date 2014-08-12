package org.scripps.branch.service;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.SerializedCustomClassifier;
import org.scripps.branch.repository.SerializedCustomClassifierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import weka.core.Instances;

@Service
@Transactional
public class SerializedCustomClassifierService {

	@Autowired
	SerializedCustomClassifierRepository sccRepo;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SerializedCustomClassifierService.class);
	
	@Transactional
	public List<SerializedCustomClassifier> findAll() {
		return sccRepo.findAll();
	}
	
	@Transactional
	public SerializedCustomClassifier findById(long id) {
		return sccRepo.findById(id);
	}
	
	@Transactional
	public SerializedCustomClassifier saveAndFlush(SerializedCustomClassifier scc) {
		return sccRepo.saveAndFlush(scc);
	}
}
