package org.scripps.branch.batch.jobs;

import org.scripps.branch.classifier.ManualTree;
import org.scripps.branch.entity.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class FeatureProcessor implements ItemProcessor<Feature, Feature> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureProcessor.class);
	
	@Override
	public Feature process(Feature f) {
		LOGGER.debug(f.getShort_name());	
		return f;
	}
}
