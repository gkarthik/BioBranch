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
import org.springframework.stereotype.Service;

import weka.core.Instances;

@Service
public class AttributeServiceImpl implements AttributeService {

	@Autowired
	AttributeRepository attrRepo;

	@Autowired
	FeatureRepository featureRepo;

	@Override
	public void generateAttributesFromDataset(Instances data, String dataset,
			InputStream inStream) throws FileNotFoundException {
		Attribute attr;
		Feature f;
		HashMap<String, String> mp = getAttributeFeatureMapping(inStream);
		for (int i = 0; i < data.numAttributes(); i++) {
			attr = new Attribute();
			f = new Feature();
			attr.setName(data.attribute(i).name());
			attr.setCol_index(data.attribute(i).index());
			attr.setDataset(dataset);
			f = featureRepo.findByUniqueId(mp.get(data.attribute(i).name()));
			attr.setFeature(f);
			attrRepo.save(attr);
		}
	}

	@Override
	public HashMap<String, String> getAttributeFeatureMapping(
			InputStream inStream) {
		HashMap<String, String> mp = new HashMap<String, String>();
		BufferedReader fileReader = null;
		final String DELIMITER = "\t";
		try {
			String line = "";
			fileReader = new BufferedReader(new InputStreamReader(inStream));
			while ((line = fileReader.readLine()) != null) {
				String[] tokens = line.split(DELIMITER);
				if (tokens.length == 2) {
					mp.put(tokens[0], tokens[1]);
					System.out.println(tokens[0] + ": " + tokens[1]);
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
