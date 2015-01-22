package org.scripps.branch.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.scripps.branch.entity.Attribute;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.repository.AttributeRepository;
import org.scripps.branch.repository.DatasetRepository;
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
	
	@Autowired
	DatasetRepository dataRepo;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AttributeServiceImpl.class);
	
	public String hashAttrName(String name) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(name.getBytes());
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

	@Override
	public void generateAttributesFromDataset(Instances data, Dataset dataset,
			String inputPath) throws FileNotFoundException {
		List<Attribute> attrList = new ArrayList<Attribute>();
		Attribute attr;
		Feature f;
		
		
		Attribute classAttr=  new Attribute();
		int ids=0;

		HashMap<String, String> mp = getAttributeFeatureMapping(inputPath);
		for (int i = 0; i < data.numAttributes(); i++) {
			attr = new Attribute();
			f = new Feature();
			attr.setName(data.attribute(i).name());
			attr.setValue(generateValues(data.attribute(i), data));
			attr.setCol_index(data.attribute(i).index());
			attr.setDataset(dataset);
			attr.setNominal(data.attribute(i).isNominal());
			if(data.attribute(i).isNominal()){
				String[] s = data.attribute(i).toString().split(" ");
				attr.setLabels(s[2]);
			}
			
			f = featureRepo.findByUniqueId(mp.get(data.attribute(i).name()));
			//			try{
			//				if(mp.get(data.attribute(i).name()).contains("metabric")){
			//					f.setUnique_id(hashAttrName(System.currentTimeMillis()+mp.get(data.attribute(i).name())));
			//					f = featureRepo.saveAndFlush(f);
			//				}
			//			} catch(Exception e) {
			//				
			//			}
			
//			if(data.attribute(i).index()==data.classIndex()) attribute_id =  attr.getId();
			LOGGER.debug(data.attribute(i).name()+": "+mp.get(data.attribute(i).name())+" - "+ i);
			attr.setFeature(f);
			
			if(attr.getCol_index()==data.classIndex()) {
				ids = attrRepo.save(attr).getId();
				
			}
			else
			attrList.add(attr);
		}
		
		System.out.println("Attr name "+attrRepo.findByColIndex(ids,(int)data.classIndex()).getName() );
		dataset.setAttribute(attrRepo.findByColIndex(ids,(int)data.classIndex()));
		dataRepo.save(dataset);
		
		System.out.println("IDS: "+ ids);
		attrRepo.save(attrList);
		attrRepo.flush();
		dataRepo.flush();


	}

	@Override
	public String generateValues(weka.core.Attribute attr, Instances data) {
		String values = "[";
		for(int i=0;i<data.numInstances();i++){
			values= values +"{\""+ i + "\":\"" + data.instance(i).value(attr) + "\"}";
			if(i<data.numInstances()-1){
				values+=",";
			}
		}
		values+="]";
		return values;
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
				if (tokens.length > 0) {
					if(tokens[2].contains("///")){
						tokens[2] = tokens[2].split(" /// ")[0];
					}
					mp.put(tokens[0], tokens[2]);
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
	
	@Override
	public void generateAttributesFromDatasetWithoutMapping(Instances data, Dataset dataset, List<Feature> fList){
		List<Attribute> attrList = new ArrayList<Attribute>();
		Attribute attr;
		Feature f;
		for (int i = 0; i < data.numAttributes(); i++) {
			attr = new Attribute();
			f = null;
			for(Feature fTemp:fList){
				if(fTemp.getShort_name().equals(data.attribute(i).name())){
					f = fTemp;
				}
			}
			attr.setName(data.attribute(i).name());
			attr.setCol_index(data.attribute(i).index());
			attr.setDataset(dataset);
			if(f!=null){
				LOGGER.debug(data.attribute(i).name()+": "+f.getShort_name()+" - "+ i);
				attr.setFeature(f);
				attrList.add(attr);
			}
		}
		attrRepo.save(attrList);
		attrRepo.flush();
	}
}
