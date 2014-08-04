package org.scripps.branch.service;

import java.util.HashMap;

import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Weka;
import org.springframework.stereotype.Service;

import weka.core.Instances;

@Service
public interface CustomFeatureService {
	public void addInstanceValues(Weka weka, Dataset d);

	public int evalAndAddNewFeatureValues(String name, String exp,
			Instances data);

	public HashMap findOrCreateCustomFeature(String feature_name, String exp,
			String description, long user_id, String dataset, Weka weka);

	public HashMap getTestCase(String id, Weka weka);
}
