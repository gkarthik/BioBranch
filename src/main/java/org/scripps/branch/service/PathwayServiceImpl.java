package org.scripps.branch.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Pathway;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.PathwayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PathwayServiceImpl implements PathwayService {
	
	@Autowired
	ApplicationContext ctx;
	
	@Autowired 
	PathwayRepository pRepo;
	
	@Autowired 
	FeatureRepository fRepo;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PathwayServiceImpl.class);
	
	@Override
	public int importFromFile() {
		List<Pathway> pList = new ArrayList<Pathway>();
		String[] entrezIds;
		Pathway p;
		Feature f;
		List<Feature> fList;
		String inputPath = "http://cpdb.molgen.mpg.de/CPDB/getPathwayGenes?idtype=entrez-gene";
		BufferedReader fileReader = null;
		final String DELIMITER = "\t";
		try {
			String line = "";
			LOGGER.debug("Starting Download... ");
			Resource input = ctx.getResource("url:"+inputPath);
			fileReader = new BufferedReader(new InputStreamReader(input.getInputStream()));
			LOGGER.debug("Finished Download... ");
			while ((line = fileReader.readLine()) != null) {
				String[] tokens = line.split(DELIMITER);
				if (tokens.length == 4 && tokens[0]!="pathway") {
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
						if(pList.size() % 1000 == 0){
							pRepo.save(pList);
							pRepo.flush();
							pList = new ArrayList<Pathway>();
						}
					}
				LOGGER.debug(p.getName()+" "+p.getExternal_id()+" "+p.getSource_db()+" "+p.getFeatures().size());
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
		pRepo.save(pList);
		pRepo.flush();
		return 1;
	}	
}
