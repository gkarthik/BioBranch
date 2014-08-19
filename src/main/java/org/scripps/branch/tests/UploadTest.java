package org.scripps.branch.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContextConfig;
import org.scripps.branch.controller.FileUploadController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationContextConfig.class }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public class UploadTest {

	@Autowired
	FileUploadController fu;

	@Test
	public void testUpload() {

	}

}
