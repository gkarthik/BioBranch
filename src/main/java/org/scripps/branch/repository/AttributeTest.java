package org.scripps.branch.repository;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContext;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.config.SecurityContext;
import org.scripps.branch.config.SocialContext;
import org.scripps.branch.config.WebApplicationContext;
import org.scripps.branch.entity.Attribute;
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
        List<Attribute> attrList = attr.findByFeatureUniqueId("1960", "metabric_with_clinical");
        for(Attribute at : attrList){
        	assertEquals(at.getName(), "ILMN_1722781");
        }
    }
    
}