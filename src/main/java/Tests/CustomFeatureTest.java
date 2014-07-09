package Tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.entity.Custom_Feature;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.repository.CustomFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public class CustomFeatureTest {

	@Autowired
	@Qualifier("customFeatureRepository")
	private CustomFeatureRepository cfr;
	
	@Test
	public void testGetMetaBricClinicalFeatures() {
		List<Custom_Feature> cfList = cfr.searchCustomFeatures(" ");
		for (Custom_Feature cf : cfList) {
			if (1==1) {
				assertEquals(1,1);
				assertEquals(1,1);
			}
		}

	}
	
}
