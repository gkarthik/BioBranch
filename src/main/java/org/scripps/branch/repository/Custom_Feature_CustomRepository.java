package org.scripps.branch.repository;

import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Weka;

import weka.core.Instances;







@Transactional
public interface Custom_Feature_CustomRepository {

	
	public HashMap<?, ?> getTestCase(String id, Weka weka);
	public void addInstances(Weka weka);
	public int evalAndAddNewFeatureValues(String feature_name,
			String featureExpression, Instances data);
	public HashMap<?, ?> findOrCreateCustomFeature(String feature_name, String exp,
			String description, int user_id, Weka weka, String dataset);
	public int insert(String name, String feature_exp, String description,
			int userid, List<Feature> features, String dataset);
	public HashMap<?, ?> findOrCreateCustomFeatureId(String name, String feature_exp,
			String description, int userid, List<Feature> features, Weka weka,
			String dataset);
}
