package org.scripps.branch.tests;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContext;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.TreeRepository;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationContext.class }, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration
public class TreeTest {

	@Autowired
	@Qualifier("treeRepository")
	private TreeRepository t;
	
	@Autowired
	private HibernateAwareObjectMapper mp;

	@Test
	public void testRepo() throws SQLException {
		Tree newTree = t.findById(21);
		String result = "";
		try {
			result = mp.writeValueAsString(newTree);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(result.equals(""),false);
	}

}
