package org.scripps.branch.service;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Score;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.PathwayRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import weka.core.Instances;

@Service
public interface InstanceService {
	
	public void setTrainandTest(Weka wekaObj, Instances train, int testOption, int testsetId, Float percentSplit, Score newScore);
}
