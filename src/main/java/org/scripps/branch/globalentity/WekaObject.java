package org.scripps.branch.globalentity;

import java.util.LinkedHashMap;

import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.FeatureCustomRepository;
import org.scripps.branch.service.CustomClassifierService;
import org.scripps.branch.service.CustomFeatureService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import weka.classifiers.Classifier;

public class WekaObject implements ApplicationContextAware {

	private static ApplicationContext ctx;
	private Weka weka;
	private LinkedHashMap<String, Classifier> custom_classifiers;
	private static int checktemp;

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	@Autowired
	FeatureCustomRepository featurerepo;

	@Autowired
	CustomFeatureService cfService;

	@Autowired
	CustomClassifierService ccService;

	public LinkedHashMap<String, Classifier> getCustomClassifierObject() {
		return custom_classifiers;
	}

	public Weka getWeka() {
		return weka;
	}

	@Override
	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		ctx = appContext;
		Weka wekaObj = new Weka();
		if (wekaObj.getTrain() == null) {
			Resource train_file = ctx
					.getResource("/WEB-INF/data/Metabric_clinical_expression_DSS_sample_filtered.arff");
			try {
//				wekaObj.buildWeka(train_file.getInputStream(), null,
//						"metabric_with_clinical", featurerepo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		weka = wekaObj;
		cfService.addInstanceValues(wekaObj);
		// Set custom classifiers
		custom_classifiers = ccService.getClassifiersfromDb(wekaObj,
				"metabric_with_clinical");
	}
}
