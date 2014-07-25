package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.repository.CollectionRepository;
import org.scripps.branch.repository.DatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public class DatasetTest {

	@Autowired
	private CollectionRepository coll;

	@Autowired
	private DatasetRepository ds;

	@Test
	public void testFindByCollectionId() {

		Collection c = coll.findById(1);
		List<Dataset> dList = ds.findByCollectionId(c);
		for (Dataset d : dList) {
			if (d.getId() == 2) {
				assertEquals(d.getDatasetname().equals("dataset"), true);
			}
		}
	}
}
