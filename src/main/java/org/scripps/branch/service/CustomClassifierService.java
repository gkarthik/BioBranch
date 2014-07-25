package org.scripps.branch.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.Weka;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;

@Service
@Transactional
public interface CustomClassifierService {
	public void addCustomTree(String id, Weka weka,
			LinkedHashMap<String, Classifier> custom_classifiers, String dataset);

	public FilteredClassifier buildCustomClasifier(Weka weka,
			long[] featureDbIds, int classifierType);

	public FilteredClassifier getandBuildClassifier(CustomClassifier cc,
			Weka weka, String dataset);

	public HashMap getClassifierDetails(long id, String dataset,
			LinkedHashMap<String, Classifier> custom_classifiers);

	public LinkedHashMap<String, Classifier> getClassifiersfromDb(Weka weka,
			String dataset);

	public HashMap getOrCreateClassifier(List entrezIds, int classifierType,
			String name, String description, int player_id, Weka weka,
			String dataset, HashMap<String, Classifier> custom_classifiers);

	public CustomClassifier insertandAddCustomClassifier(long[] featureDbIds,
			int classifierType, String name, String description, int player_id,
			Weka weka, String dataset,
			HashMap<String, Classifier> custom_classifiers);
}
