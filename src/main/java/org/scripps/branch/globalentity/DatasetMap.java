package org.scripps.branch.globalentity;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.scripps.branch.controller.FileUploadController;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.FeatureCustomRepository;
import org.scripps.branch.service.CustomClassifierService;
import org.scripps.branch.service.CustomFeatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import weka.classifiers.Classifier;

public class DatasetMap implements ApplicationContextAware {

	private static ApplicationContext ctx;
	private HashMap<String, Weka> name_dataset;
	private LinkedHashMap<String, Classifier> custom_classifiers;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DatasetMap.class);

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	@Autowired
	FeatureCustomRepository featurerepo;

	@Autowired
	CustomFeatureService cfService;

	@Autowired
	CustomClassifierService ccService;
	
	@Autowired
	String uploadPath;

	public LinkedHashMap<String, Classifier> getCustomClassifierObject() {
		return custom_classifiers;
	}

	public Weka getWeka(String key) {
		return name_dataset.get("dataset_"+key);
	}

	@Override
	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		ctx = appContext;

	}
	
	public Weka buildWekaAndClassifiers(String train, String test, String id){
		Weka wekaObj = new Weka();
		if (wekaObj.getTrain() == null) {
			Resource train_file = ctx.getResource("file:"+uploadPath+train);
			Resource test_file = ctx.getResource("file:"+uploadPath+test);
			try {
				wekaObj.buildWeka(train_file.getInputStream(), test_file.getInputStream(), "test_set",	"dataset_"+id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.error("Couldn't build Weka",e);
			}
		}
		cfService.addInstanceValues(wekaObj);
		// Set custom classifiers
		custom_classifiers = ccService.getClassifiersfromDb(wekaObj, "metabric_with_clinical");
		wekaObj.generateLimits();
		return wekaObj;
	}
}
