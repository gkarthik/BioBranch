package org.scripps.branch.tests;

import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContext;
import org.scripps.branch.service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationContext.class }, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration
public class AttributeServiceTest {

	@Autowired
	AttributeService attrSer;

	@Test
	public void getMapping() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(ApplicationContext.class);
		ctx.refresh();
		InputStream r = ctx.getClass().getResourceAsStream(
				"/WEB-INF/data/oslo_mapping.txt");
		// attrSer.getAttributeFeatureMapping(r);
	}

}
