package org.scripps.branch.globalentity;

import org.scripps.branch.entity.Weka;
import org.scripps.branch.repository.FeatureCustomRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

public class WekaObject implements ApplicationContextAware {

	private static ApplicationContext ctx;
	private static Weka weka;
	private static int checktemp;

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	public static int getTemp() {
		return checktemp;
	}

	public static Weka getWeka() {
		return weka;
	}

	@Autowired
	FeatureCustomRepository featurerepo;

	@Override
	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		ctx = appContext;
		checktemp = 5;
		Weka wekaObj = new Weka();
		if (wekaObj.getTrain() == null) {
			Resource train_file = ctx
					.getResource("/WEB-INF/data/Metabric_clinical_expression_DSS_sample_filtered.arff");
			try {
				wekaObj.buildWeka(train_file.getInputStream(), null,
						"metabric_with_clinical", featurerepo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		weka = wekaObj;
		// System.out.println("Instances: "+weka.getTrain().numInstances());
	}
}
