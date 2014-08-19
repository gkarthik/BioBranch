package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContextConfig;
import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.User;
import org.scripps.branch.repository.CollectionRepository;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationContextConfig.class }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public class CollectionTest {

	@Autowired
	@Qualifier("collectionRepository")
	private CollectionRepository coll;

	@Autowired
	private DatasetRepository ds;

	@Autowired
	private UserRepository u;

	@Test
	public void testFindByUserId() {

		User user = u.findById(1);
		List<Collection> collList = coll.findByUser(user);
		for (Collection co : collList) {
			assertEquals(co.getId(), 1);
		}
	}

	// @Test
	// public void testFindByDatasetId() {
	//
	// Dataset dataset = ds.findById(1);
	// List<Collection> collList = coll.findByDatasetId(dataset);
	// for (Collection co : collList) {
	// assertEquals(co.getId(), 1);
	// }
	// }

}
