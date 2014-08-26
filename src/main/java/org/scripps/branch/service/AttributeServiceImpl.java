package org.scripps.branch.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.FeatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import weka.core.Instances;

@Service
public class AttributeServiceImpl implements AttributeService {

	@Autowired
	AttributeRepository attrRepo;

	@Autowired
	FeatureRepository featureRepo;
	
	@Autowired
	ApplicationContext ctx;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AttributeServiceImpl.class);

	@Override
	public void generateAttributesFromDataset(Instances data, Dataset dataset,
			String inputPath) throws FileNotFoundException {
		List<Attribute> attrList = new ArrayList<Attribute>();
		Attribute attr;
		Feature f;
		HashMap<String, String> mp = getAttributeFeatureMapping(inputPath);
		for (int i = 0; i < data.numAttributes(); i++) {
			attr = new Attribute();
			f = new Feature();
			attr.setName(data.attribute(i).name());
			attr.setCol_index(data.attribute(i).index());
			attr.setDataset(dataset);
			f = featureRepo.findByUniqueId(mp.get(data.attribute(i).name()));
			LOGGER.debug(data.attribute(i).name()+": "+mp.get(data.attribute(i).name())+" - "+ i);
			attr.setFeature(f);
			attrList.add(attr);
		}
		attrRepo.save(attrList);
		attrRepo.flush();
	}

	@Override
	public HashMap<String, String> getAttributeFeatureMapping(String inputPath) {
		HashMap<String, String> mp = new HashMap<String, String>();
		BufferedReader fileReader = null;
		final String DELIMITER = ",";
		try {
			String line = "";
			Resource input = ctx.getResource("file:"+inputPath);
			fileReader = new BufferedReader(new InputStreamReader(input.getInputStream()));
			while ((line = fileReader.readLine()) != null) {
				String[] tokens = line.split(DELIMITER);
				if (tokens.length > 0) {
					if(tokens[1].contains("///")){
						tokens[1] = tokens[1].split("///")[0];
					}
					mp.put(tokens[0], tokens[1]);
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
		return mp;
	}
}
