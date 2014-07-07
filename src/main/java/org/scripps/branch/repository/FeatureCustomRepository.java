package org.scripps.branch.repository;

import java.util.Map;

import org.scripps.branch.entity.Feature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public interface FeatureCustomRepository {

	public  Map<String, Feature> getByDataset(String dataset,
			boolean load_annotations_very_slowly);
	public ObjectNode getMetaBricClinicalFeatures(ObjectMapper mapper);
	
}
