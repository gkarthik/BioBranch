package org.scripps.branch.repository;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.scripps.branch.entity.Weka;

import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;

public interface CustomClassifierCustomRepository {

	public FilteredClassifier buildCustomClassifier(Weka weka,
			Long[] featureDbIds, int classifierType);

	HashMap<?, ?> getClassifierDetailsByDbId(int id, String dataset,
			LinkedHashMap<String, Classifier> custom_classifiers);

	public LinkedHashMap<String, Classifier> getClassifiersfromDb();

}
