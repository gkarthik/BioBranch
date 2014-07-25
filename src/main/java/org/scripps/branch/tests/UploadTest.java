package org.scripps.branch.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContext;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.controller.FileUploadController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class,
		ApplicationContext.class }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public class UploadTest {

	@Autowired
	FileUploadController fu;

	@Test
	public void testUpload() {

	}

}
