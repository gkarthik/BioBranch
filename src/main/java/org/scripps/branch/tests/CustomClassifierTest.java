package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContext;
import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.WekaObject;
import org.scripps.branch.repository.CustomClassifierRepository;
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
@ContextConfiguration(classes = { ApplicationContext.class }, loader = AnnotationConfigWebContextLoader.class)
public class CustomClassifierTest {

	@Autowired
	private WekaObject weka;

	@Autowired
	private CustomClassifierService ser;

	@Autowired
	private CustomClassifierRepository cClassifierRepo;

	@Test
	public void addCustomClassifier() {
		// 113130,699,11065
		LinkedHashMap<String, Classifier> custom_classifiers = new LinkedHashMap<String, Classifier>();
		Weka wekaobj = weka.getWeka();
		List<String> entrezIds = new ArrayList<String>();
		entrezIds.add("113130");
		entrezIds.add("699");
		HashMap mp = ser.getOrCreateClassifier(entrezIds, 0, "Test 2",
				"Test Classifier 2", -1, wekaobj, "metabric_with_clinical",
				custom_classifiers);
		assertEquals(mp == null, false);
	}

	@Test
	public void addCustomTree() {
		LinkedHashMap custom_classifiers = new LinkedHashMap();
		ser.addCustomTree("custom_tree_5", weka.getWeka(), custom_classifiers,
				"metabric_with_clinical");
		assertEquals(custom_classifiers.size(), 1);
	}

	@Test
	public void searchCustomClassifier() {
		List<CustomClassifier> cclist = cClassifierRepo
				.searchCustomClassifiers("test");
		assertEquals(cclist == null, false);
	}
}