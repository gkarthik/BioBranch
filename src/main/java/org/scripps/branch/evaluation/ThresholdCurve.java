package org.scripps.branch.evaluation;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.TwoClassStats;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class ThresholdCurve extends weka.classifiers.evaluation.ThresholdCurve{
	
	private int m_Classes = 2;
	
	private double maxIndex = 0;

	private static ArrayList<ArrayList<HashMap<String, Double>>> rocDataPoints = new ArrayList<ArrayList<HashMap<String, Double>>>();

	/**
	   * Calculates the performance stats for the desired class and return 
	   * results as a set of Instances.
	   *
	   * @param predictions the predictions to base the curve on
	   * @param classIndex index of the class of interest.
	   * @return datapoints as a set of instances.
	   */
	  public Instances getCurve(FastVector predictions, int classIndex) {

	    if ((predictions.size() == 0) ||
	        (((NominalPrediction)predictions.elementAt(0))
	         .distribution().length <= classIndex)) {
	      return null;
	    }
	    double totPos = 0, totNeg = 0;
	    double [] probs = getProbabilities(predictions, classIndex);
	    // Get distribution of positive/negatives
	    for (int i = 0; i < probs.length; i++) {
	      NominalPrediction pred = (NominalPrediction)predictions.elementAt(i);
	      if (pred.actual() == Prediction.MISSING_VALUE) {
	        LOGGER.debug(getClass().getName() 
	                           + " Skipping prediction with missing class value");
	        continue;
	      }
	      if (pred.weight() < 0) {
	    	  LOGGER.debug(getClass().getName() 
	                           + " Skipping prediction with negative weight");
	        continue;
	      }
	      if (pred.actual() == classIndex) {
	        totPos += pred.weight();
	      } else {
	        totNeg += pred.weight();
	      }
	    }
	    
	    Instances insts = makeHeader();
	    int [] sorted = Utils.sort(probs);
	    TwoClassStats tc = new TwoClassStats(totPos, totNeg, 0, 0);
	    double threshold = 0;
	    double cumulativePos = 0;
	    double cumulativeNeg = 0;
	    maxIndex = ((NominalPrediction)predictions.elementAt(sorted[sorted.length-1])).predicted();
	    for (int i = 0; i < sorted.length; i++) {
	    	NominalPrediction pred = (NominalPrediction)predictions.elementAt(sorted[i]);

	      if ((i == 0) || (probs[sorted[i]] > threshold)) {
	    	  if(classIndex == maxIndex){
	    		  
	    	  }
		tc.setTruePositive(tc.getTruePositive() - cumulativePos);
		tc.setFalseNegative(tc.getFalseNegative() + cumulativePos);
		tc.setFalsePositive(tc.getFalsePositive() - cumulativeNeg);
		tc.setTrueNegative(tc.getTrueNegative() + cumulativeNeg);
		threshold = probs[sorted[i]];
		insts.add(makeInstance(tc, threshold));
		cumulativePos = 0;
		cumulativeNeg = 0;
		if (i == sorted.length - 1) {
		  break;
		}
	      }

	      if (pred.actual() == Prediction.MISSING_VALUE) {
		LOGGER.debug(getClass().getName()
				   + " Skipping prediction with missing class value");
		continue;
	      }
	      if (pred.weight() < 0) {
		LOGGER.debug(getClass().getName() 
				   + " Skipping prediction with negative weight");
		continue;
	      }
	      if (pred.actual() == classIndex) {
		cumulativePos += pred.weight();
	      } else {
		cumulativeNeg += pred.weight();
	      }
	      /*
	      System.out.println(tc + " " + probs[sorted[i]] 
	                         + " " + (pred.actual() == classIndex));
	      */
//	      if ((i != (sorted.length - 1)) &&
//	          ((i == 0) ||  
//	          (probs[sorted[i]] != probs[sorted[i - 1]]))) {
//	        insts.add(makeInstance(tc, probs[sorted[i]]));
//	      }
	    }
	    
	    // make sure a zero point gets into the curve
	    if (tc.getFalseNegative() != totPos || tc.getTrueNegative() != totNeg) {
	      tc = new TwoClassStats(0, 0, totNeg, totPos);
	      threshold = probs[sorted[sorted.length - 1]] + 10e-6;
	      insts.add(makeInstance(tc, threshold));
	    }
	        
	    return insts;
	  }
	  
	  public Instances getCurveByClass(FastVector predictions, int classIndex) {

		    if ((predictions.size() == 0) ||
		        (((NominalPrediction)predictions.elementAt(0))
		         .distribution().length <= classIndex)) {
		      return null;
		    }
		    double totPos = 0, totNeg = 0;
		    double [] probs = getProbabilities(predictions, classIndex);
		    // Get distribution of positive/negatives
		    for (int i = 0; i < probs.length; i++) {
		      NominalPrediction pred = (NominalPrediction)predictions.elementAt(i);
		      if (pred.actual() == Prediction.MISSING_VALUE) {
		        LOGGER.debug(getClass().getName() 
		                           + " Skipping prediction with missing class value");
		        continue;
		      }
		      if (pred.weight() < 0) {
		    	  LOGGER.debug(getClass().getName() 
		                           + " Skipping prediction with negative weight");
		        continue;
		      }
		      if (pred.actual() == classIndex) {
		        totPos += pred.weight();
		      } else {
		        totNeg += pred.weight();
		      }
		    }
		    
		    Instances insts = makeHeader();
		    int [] sorted = Utils.sort(probs);
		    TwoClassStats tc = new TwoClassStats(totPos, totNeg, 0, 0);
		    double threshold = 0;
		    double cumulativePos = 0;
		    double cumulativeNeg = 0;
		    for (int i = 0; i < sorted.length; i++) {
		    	NominalPrediction pred = (NominalPrediction)predictions.elementAt(sorted[i]);
		      if ((i == 0) || (probs[sorted[i]] > threshold)) {
		  			tc.setTruePositive(tc.getTruePositive() - cumulativePos);
					tc.setFalseNegative(tc.getFalseNegative() + cumulativePos);
					tc.setFalsePositive(tc.getFalsePositive() - cumulativeNeg);
					tc.setTrueNegative(tc.getTrueNegative() + cumulativeNeg);
			threshold = probs[sorted[i]];
			pred.getTree().getJsonnode()[classIndex].put("roc_uid_"+classIndex, i+1);
			insts.add(makeInstanceWithPred(tc, threshold, i+1));
			cumulativePos = 0;
			cumulativeNeg = 0;
			if (i == sorted.length - 1) {
			  break;
			}
		      }

		      

		      if (pred.actual() == Prediction.MISSING_VALUE) {
			LOGGER.debug(getClass().getName()
					   + " Skipping prediction with missing class value");
			continue;
		      }
		      if (pred.weight() < 0) {
			LOGGER.debug(getClass().getName() 
					   + " Skipping prediction with negative weight");
			continue;
		      }
		    	  if (pred.actual() == classIndex) {
		  			cumulativePos += pred.weight();
		  		      } else {
		  			cumulativeNeg += pred.weight();
		  		      }
		      /*
		      System.out.println(tc + " " + probs[sorted[i]] 
		                         + " " + (pred.actual() == classIndex));
		      */
//		      if ((i != (sorted.length - 1)) &&
//		          ((i == 0) ||  
//		          (probs[sorted[i]] != probs[sorted[i - 1]]))) {
//		        insts.add(makeInstance(tc, probs[sorted[i]]));
//		      }
		    }
		    
		    // make sure a zero point gets into the curve
		    if (tc.getFalseNegative() != totPos || tc.getTrueNegative() != totNeg) {
		      tc = new TwoClassStats(0, 0, totNeg, totPos);
		      threshold = probs[sorted[sorted.length - 1]] + 10e-6;
		      insts.add(makeInstance(tc, threshold));
		    }
		        
		    return insts;
		  }
	  
	  public void generateRocPoints(FastVector predictions, int classIndex){
		  rocDataPoints = new ArrayList<ArrayList<HashMap<String, Double>>>();
		  for(int i=0;i<m_Classes;i++){
			  Instances t = getCurveByClass(predictions, i);
		      rocDataPoints.add(convertToList(t));
		    }
	  }
	  
	  private ArrayList<HashMap<String, Double>> convertToList(Instances t){
		  ArrayList<HashMap<String, Double>> data = new ArrayList<HashMap<String, Double>>();
		  HashMap<String, Double> mp = new HashMap<String, Double>();
		  for(int i=0;i<t.numInstances();i++){
			  mp = new HashMap<String, Double>();
			  for(int j=0;j<t.numAttributes();j++){
				  if(t.attribute(j).name().equals("roc_uid")){
					  LOGGER.debug("Map Value ROC {}", t.instance(i).value(t.attribute(j)));
				  }
				  mp.put(t.attribute(j).name(),t.instance(i).value(t.attribute(j)));
			  }
			  data.add(mp);
		  }
		  return data;
	  }

//	public ArrayList<Double[]> generateROCPoints(FastVector predictions, int classIndex){
//		double totNeg = 0;
//		double totPos = 0;
//		 double [] probs = getProbabilities(predictions, classIndex);
//		 int [] sorted = Utils.sort(probs);
//		for (int i = 0; i < probs.length; i++) {
//		      NominalPrediction pred = (NominalPrediction)predictions.elementAt(i);
//		      if (pred.actual() == Prediction.MISSING_VALUE) {
//		        LOGGER.debug(getClass().getName() 
//		                           + " Skipping prediction with missing class value");
//		        continue;
//		      }
//		      if (pred.weight() < 0) {
//		    	  LOGGER.debug(getClass().getName() 
//		                           + " Skipping prediction with negative weight");
//		        continue;
//		      }
//		      if (pred.actual() == classIndex) {
//		        totPos += pred.weight();
//		      } else {
//		        totNeg += pred.weight();
//		      }
//		    }
//		  int tp = 0;
//		  int fp = 0;
//		  int i=0;
//		  NominalPrediction pred;
//		  ArrayList<Double[]> dataPoints = new ArrayList<Double[]>();
//		  Double[] tmp = new Double[2];
//		  double threshold = 0;
//		  LOGGER.debug("Probabilities {}", probs);
//		  while(i < sorted.length) {
//			  pred = (NominalPrediction)predictions.elementAt(sorted[i]);
//			  if(pred.actual() == classIndex){
//			    	 tp+=1;
//			     } else {
//			    	 fp+=1;
//			     }
//			  if(probs[sorted[i]]>threshold){
//				  tmp = new Double[2];
//				  tmp[0] = (double) (fp/(totNeg));  
//				  tmp[1] = (double) (tp/(totPos));
//				  dataPoints.add(tmp); 
//				  threshold = probs[sorted[i]];
//			  }
//			     i++;
//		  }
//		  LOGGER.debug("totNeg {}", totNeg);
//		  LOGGER.debug("totPos {}", totPos);
//		  LOGGER.debug("ROC {}", rocDataPoints.toArray());
//		  return dataPoints;
//	  }
	  /**
	   * generates an instance out of the given data
	   * 
	   * @param tc the statistics
	   * @param prob the probability
	   * @return the generated instance
	   */
	  private Instance makeInstanceWithPred(TwoClassStats tc, double prob, double rocId) {

	    int count = 0;
	    double [] vals = new double[14];
	    vals[count++] = tc.getTruePositive();
	    vals[count++] = tc.getFalseNegative();
	    vals[count++] = tc.getFalsePositive();
	    vals[count++] = tc.getTrueNegative();
	    vals[count++] = tc.getFalsePositiveRate();
	    vals[count++] = tc.getTruePositiveRate();
	    vals[count++] = tc.getPrecision();
	    vals[count++] = tc.getRecall();
	    vals[count++] = tc.getFallout();
	    vals[count++] = tc.getFMeasure();
	    vals[count++] = rocId;
	    LOGGER.debug("Roc UID vals array {}", vals[count]);
	      double ss = (tc.getTruePositive() + tc.getFalsePositive()) / 
	        (tc.getTruePositive() + tc.getFalsePositive() + tc.getTrueNegative() + tc.getFalseNegative());
	    vals[count++] = ss;
	    double expectedByChance = (ss * (tc.getTruePositive() + tc.getFalseNegative()));
	    if (expectedByChance < 1) {
	      vals[count++] = Instance.missingValue();
	    } else {
	    vals[count++] = tc.getTruePositive() / expectedByChance; 
	     
	    }
	    vals[count++] = prob;
	    return new Instance(1.0, vals);
	  }
	  
	  /**
	   * generates an instance out of the given data
	   * 
	   * @param tc the statistics
	   * @param prob the probability
	   * @return the generated instance
	   */
	  private Instance makeInstance(TwoClassStats tc, double prob) {

	    int count = 0;
	    double [] vals = new double[14];
	    vals[count++] = tc.getTruePositive();
	    vals[count++] = tc.getFalseNegative();
	    vals[count++] = tc.getFalsePositive();
	    vals[count++] = tc.getTrueNegative();
	    vals[count++] = tc.getFalsePositiveRate();
	    vals[count++] = tc.getTruePositiveRate();
	    vals[count++] = tc.getPrecision();
	    vals[count++] = tc.getRecall();
	    vals[count++] = tc.getFallout();
	    vals[count++] = tc.getFMeasure();
	    vals[count++] = 0.00;
	      double ss = (tc.getTruePositive() + tc.getFalsePositive()) / 
	        (tc.getTruePositive() + tc.getFalsePositive() + tc.getTrueNegative() + tc.getFalseNegative());
	    vals[count++] = ss;
	    double expectedByChance = (ss * (tc.getTruePositive() + tc.getFalseNegative()));
	    if (expectedByChance < 1) {
	      vals[count++] = Instance.missingValue();
	    } else {
	    vals[count++] = tc.getTruePositive() / expectedByChance; 
	     
	    }
	    vals[count++] = prob;
	    return new Instance(1.0, vals);
	  }
	  
	  /**
	   * generates the header
	   * 
	   * @return the header
	   */
	  private Instances makeHeader() {

	    FastVector fv = new FastVector();
	    fv.addElement(new Attribute(TRUE_POS_NAME));
	    fv.addElement(new Attribute(FALSE_NEG_NAME));
	    fv.addElement(new Attribute(FALSE_POS_NAME));
	    fv.addElement(new Attribute(TRUE_NEG_NAME));
	    fv.addElement(new Attribute(FP_RATE_NAME));
	    fv.addElement(new Attribute(TP_RATE_NAME));
	    fv.addElement(new Attribute(PRECISION_NAME));
	    fv.addElement(new Attribute(RECALL_NAME));
	    fv.addElement(new Attribute(FALLOUT_NAME));
	    fv.addElement(new Attribute(FMEASURE_NAME));
	    fv.addElement(new Attribute("roc_uid"));
	    fv.addElement(new Attribute(SAMPLE_SIZE_NAME));
	    fv.addElement(new Attribute(LIFT_NAME));
	    fv.addElement(new Attribute(THRESHOLD_NAME)); 
	    return new Instances(RELATION_NAME, fv, 100);
	  }
	  
	  /**
	   * 
	   * @param predictions the predictions to use
	   * @param classIndex the class index
	   * @return the probabilities
	   */
	  public double [] getProbabilities(FastVector predictions, int classIndex) {

	    // sort by predicted probability of the desired class.
	    double [] probs = new double [predictions.size()];
	    for (int i = 0; i < probs.length; i++) {
	      NominalPrediction pred = (NominalPrediction)predictions.elementAt(i);
	      probs[i] = pred.distribution()[classIndex];
	    }
	    return probs;
	  }
	
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

		public ArrayList<ArrayList<HashMap<String, Double>>> getRocDataPoints() {
			return rocDataPoints;
		}

		public void setRocDataPoints(ArrayList<ArrayList<HashMap<String, Double>>> rocDataPoints) {
			this.rocDataPoints = rocDataPoints;
		}
		
		public int getM_Classes() {
			return m_Classes;
		}

		public void setM_Classes(int m_Classes) {
			this.m_Classes = m_Classes;
		}
		
		public double getMaxIndex() {
			return maxIndex;
		}

		public void setMaxIndex(double maxIndex) {
			this.maxIndex = maxIndex;
		}
}
