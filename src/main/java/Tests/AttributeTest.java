package Tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.repository.AttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public class AttributeTest {

	@Autowired
	@Qualifier("attributeRepository")
	private AttributeRepository attr;

	@Test
	public void testFeatureUniqueId() {
		List<Attribute> attrList = attr.findByFeatureUniqueId("1960",
				"metabric_with_clinical");
		for (Attribute at : attrList) {
			assertEquals(at.getName(), "ILMN_1722781");
		}
	}
	@Test
	public void testfindByFeatureDbId() {
		List<Attribute> attrList = attr.findByFeatureDbId(1849);
		for (Attribute at : attrList) {
			if(at.getDataset().equals("metabric_with_clinical")){
				assertEquals(at.getId(), 93806);
			}
		}

	}

	@Test
	public void testfindByAttNameDataset() {
		List<Attribute> attrList= attr.findByAttNameDataset("ILMN_1722781", "metabric_with_clinical");

		for (Attribute at : attrList) {
			if(at.getDataset().equals("metabric_with_clinical")){
				assertEquals(at.getId(), 82099);
			}
		}

	}

	

}