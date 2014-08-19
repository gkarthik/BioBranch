package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContextConfig;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.DatasetMap;
import org.scripps.branch.repository.CustomFeatureRepository;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.service.CustomFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles("production")
@Transactional
@ContextConfiguration(classes = { ApplicationContextConfig.class }, loader = AnnotationConfigContextLoader.class)
public class CustomFeatureTest {

	@Autowired
	private CustomFeatureRepository cfRepo;

	@Autowired
	private DatasetRepository dRepo;

//	@Test
//	public void addCustomFeature() {
//		Weka wekaobj = weka.getWeka();
//		HashMap mp = ser.findOrCreateCustomFeature("Test", "@1960", "EGR3", -1,
//				"metabric_with_clinical", wekaobj);
//		assertEquals(mp == null, false);
//	}
//
//	@Test
//	public void getTestCase() {
//		Weka wekaobj = weka.getWeka();
//		HashMap mp = ser.getTestCase("custom_feature_74", wekaobj);
//		System.out.println(mp.size());
//		assertEquals(mp == null, false);
//	}
//
//	@Test
//	public void searchCustomFeature() {
//		List<CustomFeature> cfList = cfRepo.searchCustomFeatures("test");
//		System.out.println(cfList.size());
//		assertEquals(cfList == null, false);
//	}
	
	@Test
	public void getAttrDataset(){
		CustomFeature c  = cfRepo.findById(95218);
		Dataset d = dRepo.findById(1);
		List<Feature> aList = cfRepo.getAttrDatasets(c, d);
		for(Feature dt: aList){
			System.out.println(dt.getShort_name());
		}
		assertEquals(aList!=null,true);
	}
}