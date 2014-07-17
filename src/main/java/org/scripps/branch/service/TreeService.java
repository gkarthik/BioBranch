package org.scripps.branch.service;

import java.util.List;

import org.scripps.branch.entity.CustomClassifier;
import org.scripps.branch.entity.CustomFeature;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.entity.Tree;
import org.scripps.branch.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface TreeService {
	public double getUniqueIdNovelty(List<Feature> fList,
			List<CustomFeature> cfList, List<CustomClassifier> ccList,
			List<Tree> tList, User user);
}
