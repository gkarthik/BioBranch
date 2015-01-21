package org.scripps.branch.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Pathway;
import org.scripps.branch.entity.Score;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.DatasetMap;
import org.scripps.branch.repository.DatasetRepository;
import org.scripps.branch.repository.FeatureRepository;
import org.scripps.branch.repository.PathwayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import weka.core.Instances;

@Service
public class InstanceServiceImpl implements InstanceService {
	
	@Autowired 
	DatasetRepository dataRepo;
	
	@Autowired
	DatasetMap weka;
	
	@Override
	public void setTrainandTest(Weka wekaObj, Instances train, int testOption, int testsetId, Float percentSplit, Score newScore) {
		// TODO Auto-generated method stub
		switch (testOption) {
		case 0:
			wekaObj.setTrain(train);
			wekaObj.setTest(train);
			break;
		case 1:
			wekaObj.setTrain(train);
			long testsetid = testsetId;
			wekaObj.setTest(weka.getWeka(testsetid).getOrigTrain());
			newScore.setTestoption(1);
			newScore.setTestset(dataRepo.findById(testsetid));
			break;
		case 2:
			float limitPercent = (percentSplit) / (float) 100;
			Instances[] classLimits = wekaObj.getInstancesInClass();
			float numLimit = 0;
			numLimit = limitPercent * train.numInstances();
			numLimit = Math.round(numLimit);
			Instances newTrain = new Instances(train, Math.round(numLimit));
			Instances newTest = new Instances(train, train.numInstances()
					- Math.round(numLimit));
			for (int j = 0; j < classLimits.length; j++) {
				numLimit = limitPercent * classLimits[j].numInstances();
				for (int i = 0; i < classLimits[j].numInstances(); i++) {
					//Remove randomize to ensure reproducibility.
//					if (i == 0) {
//						classLimits[j].randomize(new Random(1));
//					}
					if (classLimits[j].instance(i) != null) {
						if (i <= numLimit) {
							newTrain.add(classLimits[j].instance(i));
						} else {
							newTest.add(classLimits[j].instance(i));
						}
					}
				}
			}
			wekaObj.setTrain(newTrain);
			wekaObj.setTest(newTest);
			newScore.setTestoption(2);
			newScore.setTestsplit(percentSplit);
			break;
		}
	}
	
	
}