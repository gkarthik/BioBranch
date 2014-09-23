package org.scripps.branch.entity;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;

import org.scripps.branch.controller.CollectionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Weka {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Weka.class);

	String dataset;
	String eval_method; // {cross_validation, test_set, training_set}
	Map<String, Feature> features;
	Instances[] instancesInClass;
	Instances origTest = null;
	Instances origTrain = null;
	Random rand;
	Instances test = null;
	private Instances train = null;

	public void buildWeka(InputStream train_stream, InputStream test_stream,
			String method) throws Exception {
		setDataset(dataset);
		// get the data
		DataSource source = new DataSource(train_stream);
		setOrigTrain(source.getDataSet());
		if (getOrigTrain().classIndex() == -1) {
			getOrigTrain().setClassIndex(getOrigTrain().numAttributes() - 1);
		}
		train_stream.close();
		if (test_stream != null) {
			source = new DataSource(test_stream);
			test = source.getDataSet();
			if (test.classIndex() == -1) {
				test.setClassIndex(test.numAttributes() - 1);
			}
			setOrigTest(test);
			test_stream.close();
		}
		// specify how hands evaluated {cross_validation, test_set,
		// training_set}
		eval_method = method; // "cross_validation";//
		// assumes that feature table has already been loaded
		// get the features related to this weka dataset
		setTrain(getOrigTrain());
		setTest(getOrigTest());
		
	}

	public void generateLimits() {
		Instances data = getOrigTrain();
		instancesInClass = new Instances[data.numClasses()];
		for (int i = 0; i < data.numClasses(); i++) {
			for (int j = 0; j < data.numInstances(); j++) {
				if (data.instance(j).classValue() == i) {// Not
					if (instancesInClass[i] == null) {
						instancesInClass[i] = new Instances(data,
								Math.round(data.numInstances()));
					}
					instancesInClass[i].add(data.instance(j));
				}
			}
		}
//		for (int i = 0; i < instancesInClass.length; i++) {
//			System.out.println(instancesInClass[i].numInstances());
//		}
	}
	
	public boolean checkDataset(InputStream path1, InputStream path2){
		Weka wekaObj1;
		Weka wekaObj2;
		boolean value = false;
		try {
			wekaObj1 = new Weka();
			wekaObj1.buildWeka(path1, null, "");
			wekaObj2 = new Weka();
			wekaObj2.buildWeka(path2, null, "");
			value=wekaObj1.getTrain().equalHeaders(wekaObj2.getTrain());
			if(!value){
				LOGGER.debug(wekaObj1.equalHeadersMsg(wekaObj2.getTrain()));
			}
			LOGGER.debug("value="+value);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("Couldn't build Weka",e);
		}
		return value;
	}
	
	/*
	 * Function available in weka-dev 3.7.5
	 */
	  public String equalHeadersMsg(Instances d1) {

	    // Check class and all attributes
	    if (train.classIndex() != d1.classIndex()) {
	      return "Class Index doesn't match";
	    }
	    if (train.numAttributes() != d1.numAttributes()) {
	    	return "Number of Attributes doesn't match";
	    }
	    for (int i = 0; i < train.numAttributes(); i++) {
	      if (!(train.attribute(i).equals(d1.attribute(i)))) {
	        return train.attribute(i).name()+" doesn't match "+d1.attribute(i).name();
	      }
	    }
	    return "Matches";
	  }

	public String getDataset() {
		return dataset;
	}

	public String getEval_method() {
		return eval_method;
	}

	public Map<String, Feature> getFeatures() {
		return features;
	}

	public Instances[] getInstancesInClass() {
		return instancesInClass;
	}

	public Instances getOrigTest() {
		return origTest;
	}

	public Instances getOrigTrain() {
		return origTrain;
	}

	public Random getRand() {
		return rand;
	}

	public Instances getTest() {
		return test;
	}

	public Instances getTrain() {
		return train;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public void setEval_method(String eval_method) {
		this.eval_method = eval_method;
	}

	public void setFeatures(Map<String, Feature> features) {
		this.features = features;
	}

	public void setInstancesInClass(Instances[] numberOfInstancesInClass) {
		this.instancesInClass = numberOfInstancesInClass;
	}

	public void setOrigTest(Instances origTest) {
		this.origTest = origTest;
	}

	public void setOrigTrain(Instances orig_train) {
		this.origTrain = orig_train;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}

	public void setTest(Instances test) {
		this.test = test;
	}

	public void setTrain(Instances train) {
		this.train = train;
	}
}
