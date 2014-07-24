package org.scripps.branch.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.FeatureRepository;
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

	@Override
	public void generateAttributesFromDataset(Instances data, String dataset,
			String inputPath) throws FileNotFoundException {
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
			System.out.println(data.attribute(i).name()+": "+mp.get(data.attribute(i).name()));
			attr.setFeature(f);
			attrRepo.saveAndFlush(attr);
		}
	}

	@Override
	public HashMap<String, String> getAttributeFeatureMapping(String inputPath) {
		HashMap<String, String> mp = new HashMap<String, String>();
		BufferedReader fileReader = null;
		final String DELIMITER = "\t";
		try {
			String line = "";
			Resource input = ctx.getResource("file:"+inputPath);
			fileReader = new BufferedReader(new InputStreamReader(input.getInputStream()));
			while ((line = fileReader.readLine()) != null) {
				String[] tokens = line.split(DELIMITER);
				if (tokens.length == 3) {
					mp.put(tokens[0], tokens[2]);
					System.out.println(tokens[0] + ": " + tokens[2]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mp;
	}
}
