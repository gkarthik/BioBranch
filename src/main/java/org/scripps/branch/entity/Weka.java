package org.scripps.branch.entity;

import java.io.InputStream;
import java.util.Map;
import java.util.Random;

import org.scripps.branch.repository.FeatureCustomRepository;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Weka {

	private Instances train = null;
	Instances test = null;
	Random rand;
	String eval_method; // {cross_validation, test_set, training_set}
	Map<String, Feature> features;
	String dataset;
	Instances origTrain = null;
	Instances origTest = null;
	Instances[] instancesInClass;

	public void buildWeka(InputStream train_stream, InputStream test_stream, String method, 
			String dataset) throws Exception {
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
	
	public void generateLimits(){
		Instances data = getOrigTrain();
		instancesInClass = new Instances[data.numClasses()];
		for(int i=0;i<data.numClasses();i++){
			for(int j=0;j<data.numInstances();j++){
				System.out.println(data.instance(j).classValue());
				if(data.instance(j).classValue()==i){//Not 
					if(instancesInClass[i]==null){
						instancesInClass[i] = new Instances(data, Math.round(data.numInstances()));
					}
					instancesInClass[i].add(data.instance(j));
				}
			}
		}
		for(int i=0;i<instancesInClass.length;i++){
			System.out.println(instancesInClass[i].numInstances());
		}
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

	public void setRand(Random rand) {
		this.rand = rand;
	}

	public void setTest(Instances test) {
		this.test = test;
	}

	public void setTrain(Instances train) {
		this.train = train;
	}
	
	public Instances getOrigTrain() {
		return origTrain;
	}

	public void setOrigTrain(Instances orig_train) {
		this.origTrain = orig_train;
	}
	
	public Instances getOrigTest() {
		return origTest;
	}

	public void setOrigTest(Instances origTest) {
		this.origTest = origTest;
	}
	
	public Instances[] getInstancesInClass() {
		return instancesInClass;
	}

	public void setInstancesInClass(Instances[] numberOfInstancesInClass) {
		this.instancesInClass = numberOfInstancesInClass;
	}
}