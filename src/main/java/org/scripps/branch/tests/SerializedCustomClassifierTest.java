package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContext;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.entity.Collection;
import org.scripps.branch.entity.SerializedCustomClassifier;
import org.scripps.branch.entity.User;
import org.scripps.branch.repository.CollectionRepository;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.SerializedCustomClassifierRepository;
import org.scripps.branch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationContext.class }, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration
public class SerializedCustomClassifierTest {

	@Autowired
	private SerializedCustomClassifierRepository sccRepo;
	
	 @Test
	 public void testFindByDatasetId() {
		 SerializedCustomClassifier s = sccRepo.findById(236300);
		 assertEquals(s.getSerialized_object()!=null,true);
	 }

}
