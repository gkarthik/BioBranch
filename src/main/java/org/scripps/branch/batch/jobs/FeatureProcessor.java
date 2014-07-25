package org.scripps.branch.batch.jobs;

import org.scripps.branch.entity.Feature;
import org.springframework.batch.item.ItemProcessor;

public class FeatureProcessor implements ItemProcessor<Feature, Feature> {

	@Override
	public Feature process(Feature f) {
		System.out.println("job");
		System.out.println(f.getShort_name());
		return f;
	}
}
