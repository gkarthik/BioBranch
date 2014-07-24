package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.repository.FeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public class FeatureTest {

	@Autowired
	@Qualifier("featureRepository")
	private FeatureRepository feat;

	@Test
	public void testFindByUniqueId() {
		Feature featList = feat.findByUniqueId("222663");
		System.out.println(featList.getShort_name());
		assertEquals(featList.getId(), Long.valueOf(5));

	}

//	@Test
//	public void testGetByDbId() {
//		Feature featList = feat.getByDbId(10);
//
//		assertEquals(featList.getUnique_id(), "15");
//
//	}
//
//	@Test
//	public void testGetMetaBricClinicalFeatures() {
//		List<Feature> fList = feat.getMetaBricClinicalFeatures();
//		for (Feature f : fList) {
//			if (f.getUnique_id().equals("metabric_with_clinical_8")) {
//				assertEquals(f.getId(), Long.valueOf(43211));
//				assertEquals(f.getShort_name(), "HER2_IHC_status");
//			}
//		}
//
//	}

}