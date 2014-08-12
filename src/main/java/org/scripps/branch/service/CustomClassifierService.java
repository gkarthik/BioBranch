package org.scripps.branch.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.CustomSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;

@Service
public interface CustomClassifierService {
	public void addCustomTree(String id, Weka weka,
			LinkedHashMap<String, Classifier> custom_classifiers, Dataset dataset, CustomSetRepository cSetRepo);

	public HashMap buildCustomClasifier(Instances data, long id);

	public HashMap getClassifierDetails(long id, Dataset dataset,
			LinkedHashMap<String, Classifier> custom_classifiers);

	public LinkedHashMap<String, Classifier> getClassifiersfromDb(HashMap<String, Weka> name_dataset);

	public HashMap getOrCreateClassifier(List entrezIds, int classifierType,
			String name, String description, int player_id, HashMap<String, Weka> name_dataset,
			Dataset dataset, HashMap<String, Classifier> custom_classifiers);

	public CustomClassifier insertandAddCustomClassifier(long[] featureDbIds,
			int classifierType, String name, String description, int player_id,
			HashMap<String, Weka> name_dataset, Dataset dataset,
			HashMap<String, Classifier> custom_classifiers);
}
