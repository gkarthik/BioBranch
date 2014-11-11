package org.scripps.branch.evaluation;

public class NominalPrediction extends weka.classifiers.evaluation.NominalPrediction{
	  /** The predicted probabilities */
	  private double [] m_Distribution;

	  /** The actual class value */
	  private double m_Actual = MISSING_VALUE;

	  /** The predicted class value */
	  private double m_Predicted = MISSING_VALUE;

	  /** The weight assigned to this prediction */
	  private double m_Weight = 1;
	  
	  private double m_pred = -1;
	  
	public NominalPrediction(double actual, double[] distribution) {
		super(actual, distribution);	
	}
	
	  public NominalPrediction(double actual, double [] distribution, 
              double weight) {
		  super(actual, distribution,weight);
	  }
	
	  public void updatePredicted() {
		  if(m_pred != -1){
			  m_Predicted = m_pred;
		  }
	  }

	public double getM_pred() {
		return m_pred;
	}
	
	public double getActual(){
		if(m_pred!=-1){
			return m_pred;
		}
		return m_Actual;
	}

	public void setM_pred(double m_pred) {
		this.m_pred = m_pred;
	}
}
