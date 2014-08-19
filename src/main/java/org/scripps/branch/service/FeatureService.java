package org.scripps.branch.service;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.node.ArrayNode;

import weka.core.Instances;

@Service
public interface FeatureService {
	
	public ArrayNode rankFeatures(Instances data, List<String> entrezIds, Dataset d);
}
