package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContext;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.config.SecurityContext;
import org.scripps.branch.config.SocialContext;
import org.scripps.branch.config.WebApplicationContext;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.WekaObject;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.service.CustomFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@ContextConfiguration(classes = { ApplicationContext.class }, loader = AnnotationConfigWebContextLoader.class)
public class CustomFeatureTest {
	
	@Autowired
	private CustomFeatureService ser;
	
	@Autowired
	private WekaObject weka;
	
	@Test
	public void addCustomFeature() {
		Weka wekaobj = weka.getWeka();
		HashMap mp = ser.findOrCreateCustomFeature("Test", "@1960", "EGR3", -1, "metabric_with_clinical", wekaobj);
		assertEquals(mp==null, false);
	}

}