package org.scripps.branch.service;

import java.io.IOException;
import java.util.List;

import org.scripps.branch.controller.MetaServerController;
import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Score;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.scripps.branch.entity.Weka;
import org.scripps.branch.globalentity.DatasetMap;
import org.scripps.branch.repository.TreeRepository;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import weka.core.Instances;

@Service
public class TreeServiceImpl implements TreeService {

	@Autowired
	TreeRepository treeRepo;
	
	@Autowired 
	DatasetMap weka;
	
	@Autowired 
	InstanceService instSer;
	
	@Autowired
	MetaServerController mc;
	
	@Autowired
	HibernateAwareObjectMapper mapper;

	@Override
	public double getUniqueIdNovelty(List<Feature> fList,
			List<CustomFeature> cfList, List<CustomClassifier> ccList,
			List<Tree> tList, User user) {
		long n = 0;
		if (fList.size() > 0) {
			n += treeRepo.getCountOfFeature(fList, user);
		}
		if (cfList.size() > 0) {
			n += treeRepo.getCountOfCustomFeature(cfList, user);
		}
		if (ccList.size() > 0) {
			n += treeRepo.getCountOfCustomClassifier(ccList, user);
		}
		if (tList.size() > 0) {
			n += treeRepo.getCountOfCustomTree(tList, user);
		}
		long base = treeRepo.getTotalCount();
		double nov = 0;
		if (base > 0 && n > 0) {
			nov = (1 - Math.log(n) / Math.log(base));
		} else if (base == 0 || n == 0) {// With this condition, novelty =
											// Infinity error resolved. 
			nov = 1; // First time gene used. 
		}
		return nov;
	}
}
