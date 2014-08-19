package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContextConfig;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Pathway;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.PathwayRepository;
import org.scripps.branch.service.PathwayService;
import org.scripps.branch.service.PathwayServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationContextConfig.class }, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration
@Transactional
public class PathwayTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PathwayTest.class);
	
	@Autowired
	PathwayRepository path;
	
	@Autowired
	PathwayService pSer;
	
	@Autowired
	FeatureRepository fRepo;
	
	@Autowired
	org.springframework.context.ApplicationContext ctx;
	
	@Test
	@Ignore
	public void testgetGenesOfPathways() {
		Pathway p = path
				.findByNameAndSourcedb(
						"Alanine, aspartate and glutamate metabolism - Homo sapiens (human)",
						"KEGG");
		List<Feature> fList = p.getFeatures();
		System.out.println(fList.size());

		assertEquals(fList != null, true);

		Boolean exists = false;
		for (Feature f : fList) {
			if (f.getId() == 20887) {
				exists = true;
			}
		}

		assertEquals(exists, true);
	}
	
	@Test
	@Ignore
	public void testSearchPathway() {
		List<Pathway> pList = path.searchPathways("Alanine");
		System.out.println(pList.size());

		// assertEquals(pList!=null,true);

		for (Pathway p : pList) {
			if (p.getName()
					.equals(" Alanine, aspartate and glutamate metabolism - Homo sapiens (human)")) {
				assertEquals(p.getId(), 20887);
			}
		}
	}
	
	@Test
	@Transactional
	public void generatePathwayDb(){
		List<Pathway> pList = new ArrayList<Pathway>();
		String[] entrezIds;
		Pathway p;
		Feature f;
		List<Feature> fList;
		String inputPath = "/home/karthik/Documents/CPDB_pathways_genes.tab";
		BufferedReader fileReader = null;
		final String DELIMITER = "\t";
		try {
			String line = "";
			Resource input = ctx.getResource("file:"+inputPath);
			fileReader = new BufferedReader(new InputStreamReader(input.getInputStream()));
			while ((line = fileReader.readLine()) != null) {
				String[] tokens = line.split(DELIMITER);
				if (tokens.length == 4) {
					p= new Pathway();
					p.setName(tokens[0]);
					p.setExternal_id(tokens[1]);
					p.setSource_db(tokens[2]);
					entrezIds = new String[tokens[3].split(",").length];
					entrezIds = tokens[3].split(",");
					fList = new ArrayList<Feature>();
					for(String eId: entrezIds){
						f = fRepo.findByUniqueId(eId);
						if(f!=null){
							fList.add(f);
						}
					}
					p.setFeatures(fList);
					if(fList.size()>0){
						pList.add(p);
					}
				LOGGER.debug(p.getName()+" "+p.getExternal_id()+" "+p.getSource_db()+" "+p.getFeatures().size());
				break;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Couldn't read resource",e);
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				LOGGER.error("Couldn't close file reader",e);
			}
		}
//		pRepo.save(pList);
//		pRepo.flush();
	}

}
