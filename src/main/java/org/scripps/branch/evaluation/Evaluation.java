package org.scripps.branch.evaluation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.scripps.branch.classifier.ManualTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class Evaluation extends weka.classifiers.Evaluation{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Evaluation.class);

	public Evaluation(Instances data) throws Exception {
		super(data);
		m_Classes =data.classAttribute().numValues();
	}
		
	private FastVector m_Predictions;
	
	private int m_Classes;
	
	private int m_Predicted = -1;
	
	private int m_pred = -1;
	
	private ThresholdCurve tc = new ThresholdCurve();
	
	LinkedHashMap<String, Classifier> listOfFc = new LinkedHashMap<String, Classifier>();

	  /**
	   * Evaluates the classifier on a single instance and records the
	   * prediction (if the class is nominal).
	   *
	   * @param classifier machine learning classifier
	   * @param instance the test instance to be classified
	   * @return the prediction made by the classifier
	   * @throws Exception if model could not be evaluated 
	   * successfully or the data contains string attributes
	   */
		@Override
	  public double evaluateModelOnceAndRecordPrediction(Classifier classifier,
	      Instance instance) throws Exception {
			m_pred = -1;
			ManualTree t = ((ManualTree)classifier);
	    Instance classMissing = (Instance)instance.copy();
	    double pred = 0;
	    classMissing.setDataset(instance.dataset());
	    classMissing.setClassMissing();
	    if (m_ClassIsNominal) {
	      if (m_Predictions == null) {
		m_Predictions = new FastVector();
	      }
	      t.setParentNode(t);
	      double [] dist = classifier.distributionForInstance(classMissing);
	      pred = Utils.maxIndex(dist);
	      if(t.getM_pred()!=-1){
	    	  pred = t.getM_pred(); 
	      }
	      if (dist[(int)pred] <= 0) {
		pred = Instance.missingValue();
	      }
	      m_Predicted = (int) pred;
	      updateStatsForClassifier(dist, instance);
	      NominalPrediction n = new NominalPrediction(instance.classValue(), dist, 
	    		  instance.weight());
	      n.setM_pred(pred);
	      n.setTree(t);
	      n.updatePredicted();
	      m_Predictions.addElement(n);
	    } else {
	      pred = classifier.classifyInstance(classMissing);
	      updateStatsForPredictor(pred, instance);
	    }
	    return pred;
	  }
		
		 public FastVector getM_Predictions() {
		return m_Predictions;
	}

	public void setM_Predictions(FastVector m_Predictions) {
		this.m_Predictions = m_Predictions;
	}

		/**
		   * Updates all the statistics about a classifiers performance for 
		   * the current test instance.
		   *
		   * @param predictedDistribution the probabilities assigned to 
		   * each class
		   * @param instance the instance to be classified
		   * @throws Exception if the class of the instance is not
		   * set
		   */
		@Override
		  protected void updateStatsForClassifier(double [] predictedDistribution,
		      Instance instance)
		  throws Exception {

		    int actualClass = (int)instance.classValue();

		    if (!instance.classIsMissing()) {
		      updateMargins(predictedDistribution, actualClass, instance.weight());

		      // Determine the predicted class (doesn't detect multiple 
		      // classifications)
		      int predictedClass = -1;
		      double bestProb = 0.0;
		      bestProb = predictedDistribution[m_Predicted];
		      predictedClass = m_Predicted;
//		      for(int i = 0; i < m_NumClasses; i++) {
//			if (predictedDistribution[i] > bestProb) {
//			  predictedClass = i;
//			  bestProb = predictedDistribution[i];
//			}
//		      }

		      m_WithClass += instance.weight();

		      // Determine misclassification cost
		      if (m_CostMatrix != null) {
			if (predictedClass < 0) {
			  // For missing predictions, we assume the worst possible cost.
			  // This is pretty harsh.
			  // Perhaps we could take the negative of the cost of a correct
			  // prediction (-m_CostMatrix.getElement(actualClass,actualClass)),
			  // although often this will be zero
			  m_TotalCost += instance.weight()
			  * m_CostMatrix.getMaxCost(actualClass, instance);
			} else {
			  m_TotalCost += instance.weight() 
			  * m_CostMatrix.getElement(actualClass, predictedClass,
			      instance);
			}
		      }

		      // Update counts when no class was predicted
		      if (predictedClass < 0) {
			m_Unclassified += instance.weight();
			return;
		      }

		      double predictedProb = Math.max(MIN_SF_PROB,
			  predictedDistribution[actualClass]);
		      double priorProb = Math.max(MIN_SF_PROB,
			  m_ClassPriors[actualClass]
			                / m_ClassPriorsSum);
		      if (predictedProb >= priorProb) {
			m_SumKBInfo += (Utils.log2(predictedProb) - 
			    Utils.log2(priorProb))
			    * instance.weight();
		      } else {
			m_SumKBInfo -= (Utils.log2(1.0-predictedProb) - 
			    Utils.log2(1.0-priorProb))
			    * instance.weight();
		      }

		      m_SumSchemeEntropy -= Utils.log2(predictedProb) * instance.weight();
		      m_SumPriorEntropy -= Utils.log2(priorProb) * instance.weight();

		      updateNumericScores(predictedDistribution, 
			  makeDistribution(instance.classValue()), 
			  instance.weight());

		      // Update other stats
		      m_ConfusionMatrix[actualClass][predictedClass] += instance.weight();
		      if (predictedClass != actualClass) {
			m_Incorrect += instance.weight();
		      } else {
			m_Correct += instance.weight();
		      }
		    } else {
		      m_MissingClass += instance.weight();
		    }
		  }
		
		  /**
		   * Returns the area under ROC for those predictions that have been collected
		   * in the evaluateClassifier(Classifier, Instances) method. Returns
		   * Instance.missingValue() if the area is not available.
		   * 
		   * @param classIndex the index of the class to consider as "positive"
		   * @return the area under the ROC curve or not a number
		   */
		  public double areaUnderROC(int classIndex) {

		    // Check if any predictions have been collected
		    if (m_Predictions == null) {
		      return Instance.missingValue();
		    } else {
		      tc = new ThresholdCurve();
		      tc.setM_Classes(m_Classes);
		      Instances result = tc.getCurve(m_Predictions, classIndex);
		      tc.generateRocPoints(m_Predictions, classIndex);
		      return ThresholdCurve.getROCArea(result);
		    }
		  }

		public ThresholdCurve getTc() {
			return tc;
		}

		public void setTc(ThresholdCurve tc) {
			this.tc = tc;
		}
	
}
