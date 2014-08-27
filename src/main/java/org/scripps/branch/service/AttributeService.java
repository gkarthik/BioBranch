package org.scripps.branch.service;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.springframework.stereotype.Service;

import weka.core.Instances;

@Service
public interface AttributeService {
	public void generateAttributesFromDataset(Instances data, Dataset dataset, String inputPath) throws FileNotFoundException;
	
	public void generateAttributesFromDatasetWithoutMapping(Instances data, Dataset dataset, List<Feature> fList);

	public HashMap<String, String> getAttributeFeatureMapping(String inputPath);
}
