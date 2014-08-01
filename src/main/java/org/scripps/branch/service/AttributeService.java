package org.scripps.branch.service;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.scripps.branch.entity.Dataset;
import org.springframework.stereotype.Service;

import weka.core.Instances;

@Service
public interface AttributeService {
	public void generateAttributesFromDataset(Instances data, Dataset dataset, String inputPath) throws FileNotFoundException;

	public HashMap<String, String> getAttributeFeatureMapping(String inputPath);
}
