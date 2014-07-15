package org.scripps.branch.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContext;
import org.scripps.branch.controller.MetaServerController;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles("production")
@ContextConfiguration(classes = { ApplicationContext.class }, loader = AnnotationConfigWebContextLoader.class)
public class MetaServiceControllerTest {
	
	@Autowired
	MetaServerController con;
	
	@Autowired
	HibernateAwareObjectMapper mp;
	
	@Test
	public void testScoreSave() throws Exception{
		String content = "{\"command\":\"scoretree\",\"dataset\":\"metabric_with_clinical\",\"treestruct\":{\"name\":\"TPST2\",\"options\":{\"unique_id\":\"8459\",\"kind\":\"split_node\",\"full_name\":\"tyrosylprotein sulfotransferase 2\",\"cid\":\"view226\",\"viewCSS\":{},\"accLimit\":null,\"viewWidth\":100},\"cid\":0,\"getSplitData\":false,\"edit\":0,\"highlight\":0,\"modifyAccLimit\":1,\"children\":[],\"manual_pct_correct\":0,\"gene_summary\":{\"summaryText\":\"\",\"goTerms\":{},\"generif\":{},\"name\":\"\"},\"accLimit\":0,\"showJSON\":0,\"x\":0,\"y\":0,\"x0\":0,\"y0\":0,\"collaborator\":{\"name\":\"gkarthik92\",\"id\":\"1684\",\"created\":\"2014-07-07T18:08:49.353Z\"}},\"comment\":\"\",\"player_id\":\"1684\"}";
		JsonNode data = null;
		try {
			data = mp.readTree(content);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String res = con.scoreSaveManualTree(data);
		assertEquals(res!=null,true);
	}
	
}
