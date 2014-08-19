package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContextConfig;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.DatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationContextConfig.class }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public class AttributeTest {

	@Autowired
	@Qualifier("attributeRepository")
	private AttributeRepository attr;
	
	@Autowired
	private DatasetRepository dRepo;

	@Test
	@Ignore
	public void testfindByAttNameDataset() {
		List<Attribute> attrList = attr.findByAttNameDataset("ILMN_1722781",
				"metabric_with_clinical");

		for (Attribute at : attrList) {
			if (at.getDataset().equals("metabric_with_clinical")) {
				assertEquals(at.getId(), 82099);
			}
		}

	}

	@Test
	@Ignore
	public void testfindByFeatureDbId() {
		List<Attribute> attrList = attr.findByFeatureDbId(1849);
		for (Attribute at : attrList) {
			if (at.getDataset().equals("metabric_with_clinical")) {
				assertEquals(at.getId(), 93806);
			}
		}

	}
	
	@Test
	public void getByNameAndDataset(){
		Dataset d = dRepo.findById(2);
		Attribute a  = attr.findByNameAndDataset("ILMN_1802380", d);
		assertEquals(a.getFeature().getShort_name(),"RERE");
	}

}