package org.scripps.branch.service;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Score;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.PathwayRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import weka.core.Instances;

@Service
public interface InstanceService {
	
	public void setTrainandTest(Weka wekaObj, Instances train, int testOption, int testsetId, Float percentSplit, Score newScore);
	public Instances createInstance(JsonNode jObj, Dataset dataset);
	public List<Attribute> getAttributeList(JsonNode jObj, Dataset dataset);
}
