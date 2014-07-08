package org.scripps.branch.repository;

import java.util.Map;
import javax.transaction.Transactional;
import org.scripps.branch.entity.Feature;


@Transactional
public interface FeatureCustomRepository {

	public Map<String, Feature> getByDataset(String dataset,
			boolean load_annotations_very_slowly);

}
