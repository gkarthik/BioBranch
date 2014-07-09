package org.scripps.branch.repository;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceContext;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.postgresql.util.PGobject;
import org.scripps.branch.config.ApplicationContext;
import org.scripps.branch.config.PersistenceJPAConfig;
import org.scripps.branch.config.SecurityContext;
import org.scripps.branch.config.SocialContext;
import org.scripps.branch.config.WebApplicationContext;
import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class }, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
public class TreeTest {

	@Autowired 
	@Qualifier("treeRepository")
	private TreeRepository t;
	
	@Autowired 
	@Qualifier("featureRepository")
	private FeatureRepository f;
	
	@Test
    public void testFeatureUniqueId() throws SQLException {
//		User newUser= new User();
//		List<Tree> tree = t.findByUser(newUser);
//		System.out.println(tree.size());
//		String json = tree.getJson_tree();
//		System.out.println(tree.getComment().toString());
//        Tree newTree = new Tree();
//        newTree.setComment("First Tree!");
//        Date date= new Date();
//        newTree.setCreated(new DateTime(date.getTime()));
//        List<Feature> fList = new ArrayList();
//        fList.add(f.findByUniqueId("metabric_with_clinical_5"));
//        newTree.setFeatures(fList);
//    	String json = "{\"command\":\"scoretree\",\"dataset\":\"metabric_with_clinical\",\"treestruct\":{\"name\":\"Er_IHC_Status\",\"options\":{\"unique_id\":\"metabric_with_clinical_5\",\"kind\":\"split_node\",\"full_name\":\"tyrosylprotein sulfotransferase 2\",\"cid\":\"view226\",\"viewCSS\":{},\"accLimit\":null,\"viewWidth\":100},\"cid\":0,\"getSplitData\":false,\"edit\":0,\"highlight\":0,\"modifyAccLimit\":1,\"children\":[],\"manual_pct_correct\":0,\"gene_summary\":{\"summaryText\":\"\",\"goTerms\":{},\"generif\":{},\"name\":\"\"},\"accLimit\":0,\"showJSON\":0,\"x\":0,\"y\":0,\"x0\":0,\"y0\":0,\"collaborator\":{\"name\":\"gkarthik92\",\"id\":\"1684\",\"created\":\"2014-07-07T18:08:49.353Z\"}},\"comment\":\"\",\"player_id\":\"1684\"}";
//        newTree.setJson_tree(json);
//        newTree.setPrivate_tree(false);
//        newTree.setUser(null);
//        newTree.setUser_saved(false);
//        System.out.println(t.saveAndFlush(newTree).toString());
    }
    
}
