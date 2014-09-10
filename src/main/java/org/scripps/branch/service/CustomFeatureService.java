package org.scripps.branch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.scripps.branch.entity.Component;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Weka;
import org.springframework.stereotype.Service;

import weka.core.Instances;

@Service
public interface CustomFeatureService {
	
	public ArrayList previewCustomFeature(String name, String exp, List<Component> cList, Instances data, Dataset d);
	
	public void addInstanceValues(Weka weka, Dataset d);

	public int evalAndAddNewFeatureValues(String name, String exp,
			Instances data, List<Component> cList, Dataset d, Boolean saveInstance);

	public HashMap findOrCreateCustomFeature(String feature_name, String exp,
			String description, long user_id, Dataset dataset, List<Component> cList, Weka weka);
	
	//public HashMap getTestCase(String id, Weka weka);
}
