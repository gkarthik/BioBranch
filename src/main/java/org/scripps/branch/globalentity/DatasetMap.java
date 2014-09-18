package org.scripps.branch.globalentity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.scripps.branch.controller.FileUploadController;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.FeatureCustomRepository;
import org.scripps.branch.service.CustomClassifierService;
import org.scripps.branch.service.CustomFeatureService;
import org.scripps.branch.service.FeatureService;
import org.scripps.branch.service.PathwayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import weka.classifiers.Classifier;

@Transactional
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
	FeatureService fSer;

	@Autowired
	CustomFeatureService cfService;

	@Autowired
	CustomClassifierService ccService;
	
	@Autowired
	DatasetRepository datasetRepo;
	
	@Autowired
	String uploadPath;
	
	@Autowired
	PathwayService pSer;

	public LinkedHashMap<String, Classifier> getCustomClassifierObject() {
		return custom_classifiers;
	}

	public Weka getWeka(long key) {
		return name_dataset.get("dataset_"+key);
	}
	
	public HashMap<String, Weka> getMap(){
		return name_dataset;
	}

	@Override
	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		ctx = appContext;
		buildDatasets();
	}
	
	public void buildDatasets(){
		List<Dataset> datasetList = datasetRepo.findAll();
		name_dataset = new HashMap<String, Weka>();
		for(Dataset d: datasetList){
			name_dataset.put("dataset_"+d.getId(), buildWekaAndClassifiers(d.getDatasetfile(), d.getDatasetfile(), d));
		}
//		// Set custom classifiers
		custom_classifiers = ccService.getClassifiersfromDb(name_dataset);
	}
	
	public Weka buildWekaAndClassifiers(String train, String test, Dataset d){
		Weka wekaObj = new Weka();
		if (wekaObj.getTrain() == null) {
			Resource train_file = ctx.getResource("file:"+uploadPath+train);
			LOGGER.debug("file:"+uploadPath+train);
			Resource test_file = ctx.getResource("file:"+uploadPath+test);
			try {
				wekaObj.buildWeka(train_file.getInputStream(), test_file.getInputStream(), "test_set");
				cfService.addInstanceValues(wekaObj, d); 
				wekaObj.generateLimits();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.error("Couldn't build Weka",e);
			}
			fSer.rankFeatures(wekaObj.getOrigTrain(), null, d);
		}
		return wekaObj;
	}
}
