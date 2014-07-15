package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.scripps.branch.repository.TreeRepository;
import org.scripps.branch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public class TreeTest {

	@Autowired
	@Qualifier("treeRepository")
	private TreeRepository t;

	@Autowired
	private UserRepository u;

	@Test
	public void testRepo() throws SQLException {
		Tree newTree = t.findById(21);
		assertEquals(newTree == null, false);

		User user = u.findById(3);
		List<Tree> tList = t.findByUser(user);
		assertEquals(tList.size() == 0, false);

		tList = t.getByOtherUser(user);
		assertEquals(tList.size() == 0, false);

		tList = t.getAllTrees();
		assertEquals(tList.size() == 0, false);
	}

}
