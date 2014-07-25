package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Pathway;
import org.scripps.branch.repository.PathwayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
@Transactional
public class PathwayTest {

	@Autowired
	PathwayRepository path;

	@Test
	public void testgetGenesOfPathways() {
		Pathway p = path
				.findByNameAndSourcedb(
						"Alanine, aspartate and glutamate metabolism - Homo sapiens (human)",
						"KEGG");
		List<Feature> fList = p.getFeatures();
		System.out.println(fList.size());

		assertEquals(fList != null, true);

		Boolean exists = false;
		for (Feature f : fList) {
			if (f.getId() == 20887) {
				exists = true;
			}
		}

		assertEquals(exists, true);
	}

	@Test
	public void testSearchPathway() {
		List<Pathway> pList = path.searchPathways("Alanine");
		System.out.println(pList.size());

		// assertEquals(pList!=null,true);

		for (Pathway p : pList) {
			if (p.getName()
					.equals(" Alanine, aspartate and glutamate metabolism - Homo sapiens (human)")) {
				assertEquals(p.getId(), 20887);
			}
		}
	}

}
