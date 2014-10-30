package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContextConfig;
import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.DatasetMap;
import org.scripps.branch.repository.CustomClassifierRepository;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.service.CustomClassifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import weka.classifiers.Classifier;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles("production")
@ContextConfiguration(classes = { ApplicationContextConfig.class }, loader = AnnotationConfigWebContextLoader.class)
public class CustomClassifierTest {

	@Autowired
	private CustomClassifierRepository cClassifierRepo;

	@Autowired
	private CustomClassifierService ser;

	@Autowired
	private DatasetRepository dRepo;
	
	@Test
	public void searchCustomClassifier() {
		List<CustomClassifier> cclist = cClassifierRepo
				.searchCustomClassifiers("", dRepo.findById(334285));
		for(CustomClassifier c : cclist){
			System.out.println(c.getName());
		}
		System.out.println(cclist.size());
		assertEquals(cclist.size()==0, false);
	}
}