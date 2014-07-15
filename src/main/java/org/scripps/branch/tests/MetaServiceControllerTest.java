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
//		String content = "{\"command\":\"scoretree\",\"dataset\":\"metabric_with_clinical\",\"treestruct\":{\"name\":\"EGR3\",\"options\":{\"unique_id\":\"1960\",\"kind\":\"split_node\",\"full_name\":\"early growth response 3\",\"cid\":\"view86\",\"viewCSS\":{},\"accLimit\":57.01468189233279,\"viewWidth\":100,\"attribute_name\":\"ILMN_1722781\",\"bin_size\":1226,\"infogain\":0.013593144425844805,\"split_point\":6.048394999999999,\"orig_split_point\":6.048394999999999,\"pct_correct\":0.5701468189233279},\"cid\":0,\"getSplitData\":false,\"edit\":0,\"highlight\":0,\"modifyAccLimit\":1,\"manual_pct_correct\":0,\"gene_summary\":{\"summaryText\":\"\",\"goTerms\":{},\"generif\":{},\"name\":\"\"},\"accLimit\":0,\"showJSON\":0,\"x\":700,\"y\":0,\"x0\":0,\"y0\":0,\"collaborator\":{\"name\":\"karthik\",\"id\":3,\"created\":\"2014-07-15T20:28:20.775Z\"},\"children\":[{\"name\":\"low\",\"options\":{\"attribute_name\":\"low\",\"kind\":\"split_value\",\"cid\":\"view104\",\"viewCSS\":{},\"accLimit\":null,\"viewWidth\":100},\"cid\":0,\"getSplitData\":false,\"edit\":0,\"highlight\":0,\"modifyAccLimit\":1,\"manual_pct_correct\":0,\"gene_summary\":{\"summaryText\":\"\",\"goTerms\":{},\"generif\":{},\"name\":\"\"},\"accLimit\":0,\"showJSON\":0,\"x\":233.33333333333331,\"y\":200,\"x0\":0,\"y0\":0,\"collaborator\":{\"name\":\"karthik\",\"id\":3,\"created\":\"2014-07-15T20:28:20.775Z\"},\"children\":[{\"name\":\"n\",\"options\":{\"attribute_name\":\"n\",\"kind\":\"leaf_node\",\"bin_size\":\"164\",\"errors\":\"65\",\"pct_correct\":\"0.6\",\"cid\":\"view134\",\"viewCSS\":{},\"accLimit\":8.02610114192496,\"viewWidth\":100},\"cid\":0,\"getSplitData\":false,\"edit\":0,\"highlight\":0,\"modifyAccLimit\":1,\"manual_pct_correct\":0,\"gene_summary\":{\"summaryText\":\"\",\"goTerms\":{},\"generif\":{},\"name\":\"\"},\"accLimit\":0,\"showJSON\":0,\"x\":233.33333333333331,\"y\":300,\"x0\":0,\"y0\":0,\"collaborator\":{\"name\":\"karthik\",\"id\":3,\"created\":\"2014-07-15T20:28:20.775Z\"},\"children\":[]}]},{\"name\":\"high\",\"options\":{\"attribute_name\":\"high\",\"kind\":\"split_value\",\"cid\":\"view118\",\"viewCSS\":{},\"accLimit\":null,\"viewWidth\":100},\"cid\":0,\"getSplitData\":false,\"edit\":0,\"highlight\":0,\"modifyAccLimit\":1,\"manual_pct_correct\":0,\"gene_summary\":{\"summaryText\":\"\",\"goTerms\":{},\"generif\":{},\"name\":\"\"},\"accLimit\":0,\"showJSON\":0,\"x\":1166.6666666666667,\"y\":200,\"x0\":0,\"y0\":0,\"collaborator\":{\"name\":\"karthik\",\"id\":3,\"created\":\"2014-07-15T20:28:20.775Z\"},\"children\":[{\"name\":\"AURKA\",\"options\":{\"attribute_name\":\"y\",\"kind\":\"split_node\",\"bin_size\":\"1062\",\"errors\":\"428\",\"pct_correct\":\"0.6\",\"cid\":\"view156\",\"viewCSS\":{},\"accLimit\":51.97389885807504,\"viewWidth\":100,\"unique_id\":\"6790\",\"full_name\":\"aurora kinase A\"},\"cid\":0,\"getSplitData\":false,\"edit\":0,\"highlight\":0,\"modifyAccLimit\":1,\"manual_pct_correct\":0,\"gene_summary\":{\"summaryText\":\"\",\"goTerms\":{},\"generif\":{},\"name\":\"\"},\"accLimit\":0,\"showJSON\":0,\"x\":1166.6666666666667,\"y\":300,\"x0\":0,\"y0\":0,\"collaborator\":{\"name\":\"karthik\",\"id\":3,\"created\":\"2014-07-15T20:28:20.775Z\"},\"children\":[],\"previousAttributes\":{\"name\":\"y\",\"options\":{\"attribute_name\":\"y\",\"kind\":\"leaf_node\",\"bin_size\":\"1062\",\"errors\":\"428\",\"pct_correct\":\"0.6\",\"cid\":\"view156\",\"viewCSS\":{},\"accLimit\":51.97389885807504,\"viewWidth\":100,\"unique_id\":\"9212\",\"full_name\":\"aurora kinase B\"},\"cid\":0,\"getSplitData\":false,\"edit\":0,\"highlight\":0,\"modifyAccLimit\":1,\"manual_pct_correct\":0,\"gene_summary\":{\"summaryText\":\"\",\"goTerms\":{},\"generif\":{},\"name\":\"\"},\"accLimit\":0,\"showJSON\":0,\"x\":1166.6666666666667,\"y\":300,\"x0\":0,\"y0\":0,\"collaborator\":{\"name\":\"karthik\",\"id\":3,\"created\":\"2014-07-15T20:28:20.775Z\"},\"children\":[],\"previousAttributes\":[]}}]}]},\"comment\":\"\",\"player_id\":22}";
//		JsonNode data = null;
//		try {
//			data = mp.readTree(content);
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String res = con.scoreSaveManualTree(data);
//		assertEquals(res!=null,true);
	}
	
	public void testTree() throws Exception{
		String content = "{\"command\":\"get_trees_with_range\",\"lowerLimit\":0,\"upperLimit\":200,\"orderby\":\"score\"}";
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
		String res = con.scoreOrSaveTree(data);
		assertEquals(res!=null,true);
	}
	
}
