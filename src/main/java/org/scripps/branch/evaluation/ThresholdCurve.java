package org.scripps.branch.evaluation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.core.Instances;

public class ThresholdCurve extends weka.classifiers.evaluation.ThresholdCurve{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ThresholdCurve.class);
	  /**
	   * Calculates the area under the ROC curve as the Wilcoxon-Mann-Whitney statistic.
	   *
	   * @param tcurve a previously extracted threshold curve Instances.
	   * @return the ROC area, or Double.NaN if you don't pass in 
	   * a ThresholdCurve generated Instances. 
	   */
		public static double getROCArea(Instances tcurve) {

	    final int n = tcurve.numInstances();
	    if (!RELATION_NAME.equals(tcurve.relationName()) 
	        || (n == 0)) {
	      return Double.NaN;
	    }
	    final int tpInd = tcurve.attribute(TRUE_POS_NAME).index();
	    final int fpInd = tcurve.attribute(FALSE_POS_NAME).index();
	    final double [] tpVals = tcurve.attributeToDoubleArray(tpInd);
	    final double [] fpVals = tcurve.attributeToDoubleArray(fpInd);
	    
	    LOGGER.debug(String.valueOf(tpVals.length));
	    LOGGER.debug(String.valueOf(TRUE_POS_NAME));
	    
	    double area = 0.0, cumNeg = 0.0;
	    final double totalPos = tpVals[0];
	    final double totalNeg = fpVals[0];
	    for (int i = 0; i < n; i++) {
		double cip, cin;
		if (i < n - 1) {
		    cip = tpVals[i] - tpVals[i + 1];
		    cin = fpVals[i] - fpVals[i + 1];
		} else {
		    cip = tpVals[n - 1];
		    cin = fpVals[n - 1];
		}
		area += cip * (cumNeg + (0.5 * cin));
		cumNeg += cin;
	    }
	    area /= (totalNeg * totalPos);

	    return area;
	  }

}
