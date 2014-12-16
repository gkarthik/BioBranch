/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    RandomTree.java
 *    Copyright (C) 2001 University of Waikato, Hamilton, New Zealand
 *
 */

package org.scripps.branch.classifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.scripps.branch.entity.CustomSet;
import org.scripps.branch.entity.Dataset;
import org.scripps.branch.entity.Feature;
import org.scripps.branch.service.CustomClassifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.ContingencyTables;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Randomizable;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * <!-- globalinfo-start --> Class for constructing a tree when the structure of
 * the tree has been completely determined elsewhere. <!-- globalinfo-end -->
 * 
 * <!-- options-start --> Valid options are:
 * <p/>
 * 
 * <pre>
 * -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console
 * </pre>
 * 
 * <!-- options-end -->
 * 
 * @author Benjamin Good (bgood@scripps.edu) based on RandomTree
 */
public class ManualTree extends Classifier implements OptionHandler,
		WeightedInstancesHandler, Randomizable, Drawable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ManualTree.class);
	/** for serialization */
	static final long serialVersionUID = 8934314652175299374L;

	/**
	 * Adds Tree Classifiers to classifier hashmap.
	 * 
	 * @param id
	 * @param weka
	 * @param global
	 *            object
	 * 
	 */

	/**
	 * Main method for this class.
	 * 
	 * @param argv
	 *            the commandline parameters
	 */
	public static void main(String argv[]) throws Exception {
		/*
		 * Weka weka = new Weka(); String train_file =
		 * "/home/karthik/workspace/cure/WebContent/WEB-INF/data/Metabric_clinical_expression_DSS_sample_filtered.arff"
		 * ; String dataset = "metabric_with_clinical"; weka.buildWeka(new
		 * FileInputStream(train_file), null, dataset);
		 * evalAndAddNewFeatureValues("a17^2",weka.getTrain()); Instances data =
		 * m_wekaObject.getTrain(); int numAttr = data.numAttributes()-2;
		 * LOGGER.debug(data.attribute(numAttr));
		 * LOGGER.debug(data.instance(13).value(16));
		 * LOGGER.debug(data.instance(13).value(numAttr));
		 */
		ManualTree t =new ManualTree();
		List<double[]> vertices = new ArrayList<double[]>();
		double[] testPoint = new double[2];
		double[] temp = new double[2];
		temp[0] = 0;
		temp[1] = 0;
		vertices.add(temp);
		temp = new double[2];
		temp[0] = 0;
		temp[1] = 2;
		vertices.add(temp);
		temp = new double[2];
		temp[0] = 2;
		temp[1] = 2;
		vertices.add(temp);
		temp = new double[2];
		temp[0] = 2;
		temp[1] = 0;
		vertices.add(temp);
		testPoint[0] =1;
		testPoint[1] = 1;
		int check = t.checkPointInPolygon(vertices,testPoint);
		Boolean c = true;
		c=!c;
	}

	/** The subtrees appended to this tree. */
	protected ManualTree[] m_Successors;

	/** The attribute to split on. */
	protected int m_Attribute = -1;

	/** The split point. */
	protected double m_SplitPoint = Double.NaN;

	/** The header information. */
	protected Instances m_Info = null;

	/** The proportions of training instances going down each branch. */
	protected double[] m_Prop = null;

	/** Class probabilities from the training data. */
	protected double[] m_ClassDistribution = null;

	/** Minimum number of instances for leaf. */
	protected double m_MinNum = 1.0;

	/** The number of attributes considered for a split. */
	protected int m_KValue = 0;

	/** The random seed to use. */
	protected int m_randomSeed = 1;

	/** The maximum depth of the tree (0 = unlimited) */
	protected int m_MaxDepth = 0;

	/** Determines how much data is used for backfitting */
	protected int m_NumFolds = 0;

	/** Whether unclassified instances are allowed */
	protected boolean m_AllowUnclassifiedInstances = false;

	/** a ZeroR model in case no model can be built from the data */
	protected Classifier m_ZeroR;

	/** A tree captured from json **/
	protected JsonNode jsontree;

	protected ObjectNode jsonnode;

	/** distribution array **/
	protected HashMap m_distributionData = new HashMap();

	/** for building up the json tree **/
	ObjectMapper mapper;
	
	/** Custom Classifier Object **/
	protected LinkedHashMap<String, Classifier> listOfFc = new LinkedHashMap<String, Classifier>();
	
	protected List<CustomSet> cSetList;

	protected Instances requiredInst;
	
	protected CustomClassifierService ccSer;
	
	protected Dataset d;
	
	protected int m_pred = -1;
	
	protected ManualTree parentNode;
	
	protected HashMap<String, Integer> m_ClassAssignment = new HashMap<String,Integer>(); 

	/**
	 * Trying to get generate distribution of classes
	 * 
	 * @param Instances
	 * @Param Attribute index to get distribution of
	 * @Param HashMap to put data into
	 * 
	 * @return HashMap of class distribution data
	 */
	protected HashMap addDistributionData(Instances instances, int attIndex,
			HashMap distMap) throws Exception {
		Map<String, Comparable> temp = new HashMap<String, Comparable>();
		ArrayList<Object> distData = new ArrayList();
		// GenerateCSV csv = new GenerateCSV();
		// String data = "";
		boolean isNominal = false;
		instances.sort(attIndex);
		for (int i = 0; i < instances.numInstances(); i++) {
			Instance inst = instances.instance(i);
			if (!Double.isNaN(inst.value(attIndex))) {
				temp = new HashMap<String, Comparable>();
				if (inst.attribute(attIndex).isNominal()) {
					temp.put(
							"value",
							inst.attribute(attIndex).value(
									(int) inst.value(attIndex)));
					isNominal = true;
					// data+=inst.attribute(m_Attribute).value((int)inst.value(m_Attribute))+",";
				} else {
					temp.put("value", inst.value(attIndex));
					// data+=inst.value(att)+",";
				}
				temp.put("classprob",
						inst.classAttribute().value((int) inst.classValue()));
				// data+=inst.classAttribute().value((int)
				// inst.classValue())+"\n";
				distData.add(temp);
			}
		}
		if (!distData.isEmpty()) {
			distMap.put("dataArray", distData);
			distMap.put("isNominal", isNominal);
			setDistributionData(distMap);
		}
		return distMap;
		// To check if data is being generated right.
		// csv.generateCsvFile("/home/karthik/Documents/distribution.csv",
		// data);
	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String allowUnclassifiedInstancesTipText() {
		return "Whether to allow unclassified instances.";
	}

	/**
	 * Backfits the given data into the tree.
	 */
	public void backfitData(Instances data) throws Exception {

		// Compute initial class counts
		double[] classProbs = new double[data.numClasses()];
		for (int i = 0; i < data.numInstances(); i++) {
			Instance inst = data.instance(i);
			classProbs[(int) inst.classValue()] += inst.weight();
		}

		// Fit data into tree
		backfitData(data, classProbs);
	}

	/**
	 * Recursively backfits data into the tree.
	 * 
	 * @param data
	 *            the data to work with
	 * @param classProbs
	 *            the class distribution
	 * @throws Exception
	 *             if generation fails
	 */
	protected void backfitData(Instances data, double[] classProbs)
			throws Exception {

		// Make leaf if there are no training instances
		if (data.numInstances() == 0) {
			m_Attribute = -1;
			m_ClassDistribution = null;
			m_Prop = null;
			return;
		}

		// Check if node doesn't contain enough instances or is pure
		// or maximum depth reached
		m_ClassDistribution = classProbs.clone();

		/*
		 * if (Utils.sum(m_ClassDistribution) < 2 * m_MinNum ||
		 * Utils.eq(m_ClassDistribution[Utils.maxIndex(m_ClassDistribution)],
		 * Utils .sum(m_ClassDistribution))) {
		 * 
		 * // Make leaf m_Attribute = -1; m_Prop = null; return; }
		 */

		// Are we at an inner node
		if (m_Attribute > -1) {

			// Compute new weights for subsets based on backfit data
			m_Prop = new double[m_Successors.length];
			for (int i = 0; i < data.numInstances(); i++) {
				Instance inst = data.instance(i);
				if (!inst.isMissing(m_Attribute)) {
					if (data.attribute(m_Attribute).isNominal()) {
						m_Prop[(int) inst.value(m_Attribute)] += inst.weight();
					} else {
						m_Prop[(inst.value(m_Attribute) < m_SplitPoint) ? 0 : 1] += inst
								.weight();
					}
				}
			}

			// If we only have missing values we can make this node into a leaf
			if (Utils.sum(m_Prop) <= 0) {
				m_Attribute = -1;
				m_Prop = null;
				return;
			}

			// Otherwise normalize the proportions
			Utils.normalize(m_Prop);

			// Split data
			Instances[] subsets = splitData(data);

			// Go through subsets
			for (int i = 0; i < subsets.length; i++) {

				// Compute distribution for current subset
				double[] dist = new double[data.numClasses()];
				for (int j = 0; j < subsets[i].numInstances(); j++) {
					dist[(int) subsets[i].instance(j).classValue()] += subsets[i]
							.instance(j).weight();
				}

				// Backfit subset
				m_Successors[i].backfitData(subsets[i], dist);
			}

			// If unclassified instances are allowed, we don't need to store the
			// class distribution
			if (getAllowUnclassifiedInstances()) {
				m_ClassDistribution = null;
				return;
			}

			// Otherwise, if all successors are non-empty, we don't need to
			// store the class distribution
			boolean emptySuccessor = false;
			for (int i = 0; i < subsets.length; i++) {
				if (m_Successors[i].m_ClassDistribution == null) {
					emptySuccessor = true;
					return;
				}
			}
			m_ClassDistribution = null;

			// If we have a least two non-empty successors, we should keep this
			// tree
			/*
			 * int nonEmptySuccessors = 0; for (int i = 0; i < subsets.length;
			 * i++) { if (m_Successors[i].m_ClassDistribution != null) {
			 * nonEmptySuccessors++; if (nonEmptySuccessors > 1) { return; } } }
			 * 
			 * // Otherwise, this node is a leaf or should become a leaf
			 * m_Successors = null; m_Attribute = -1; m_Prop = null; return;
			 */
		}
	}

	/**
	 * Builds classifier.
	 * 
	 * @param data
	 *            the data to train with
	 * @throws Exception
	 *             if something goes wrong or the data doesn't fit
	 */
	@Override
	public void buildClassifier(Instances data) throws Exception {
		// Make sure K value is in range
		if (m_KValue > data.numAttributes() - 1)
			m_KValue = data.numAttributes() - 1;
		if (m_KValue < 1)
			m_KValue = (int) Utils.log2(data.numAttributes()) + 1;

		// can classifier handle the data?
		getCapabilities().testWithFail(data);

		// remove instances with missing class
		data = new Instances(data);
		data.deleteWithMissingClass();

		// only class? -> build ZeroR model
		if (data.numAttributes() == 1) {
			System.err
					.println("Cannot build model (only class attribute present in data!), "
							+ "using ZeroR model instead!");
			m_ZeroR = new weka.classifiers.rules.ZeroR();
			m_ZeroR.buildClassifier(data);
			return;
		} else {
			m_ZeroR = null;
		}

		// Figure out appropriate datasets
		Instances train = null;
		Instances backfit = null;
		Random rand = data.getRandomNumberGenerator(m_randomSeed);
		if (m_NumFolds <= 0) {
			train = data;
		} else {
			data.randomize(rand);
			data.stratify(m_NumFolds);
			train = data.trainCV(m_NumFolds, 1, rand);
			backfit = data.testCV(m_NumFolds, 1);
		}
		
		//Set Default Instances for selection.
		setRequiredInst(data);

		// Create the attribute indices window
		int[] attIndicesWindow = new int[data.numAttributes() - 1];
		int j = 0;
		for (int i = 0; i < attIndicesWindow.length; i++) {
			if (j == data.classIndex())
				j++; // do not include the class
			attIndicesWindow[i] = j++;
		}

		// Compute initial class counts
		double[] classProbs = new double[train.numClasses()];
		for (int i = 0; i < train.numInstances(); i++) {
			Instance inst = train.instance(i);
			classProbs[(int) inst.classValue()] += inst.weight();
		}
		
		Instances requiredInstances = getRequiredInst();
		// Build tree
		if (jsontree != null) {
			buildTree(train, classProbs, new Instances(data, 0), m_Debug, 0,
					jsontree, 0, m_distributionData, requiredInstances, listOfFc, cSetList, ccSer, d);
		} else {
			System.out
					.println("No json tree specified, failing to process tree");
		}
		setRequiredInst(requiredInstances);
		// Backfit if required
		if (backfit != null) {
			backfitData(backfit);
		}
	}

	/**
	 * Recursively generates a tree.
	 * 
	 * @param data
	 *            the data to work with
	 * @param classProbs
	 *            the class distribution
	 * @param header
	 *            the header of the data
	 * @param minNum
	 *            the minimum number of instances per leaf
	 * @param debug
	 *            whether debugging is on
	 * @param attIndicesWindow
	 *            the attribute window to choose attributes from
	 * @param random
	 *            random number generator for choosing random attributes
	 * @param depth
	 *            the current depth
	 * @param determineStructure
	 *            whether to determine structure
	 * @param m_distributionData
	 *            HashMap to put distribution data if getSplitData is true in
	 *            any node
	 * @throws Exception
	 *             if generation fails
	 */
	protected void buildTree(Instances data, double[] classProbs,
			Instances header, boolean debug, int depth, JsonNode node,
			int parent_index, HashMap m_distributionData, Instances requiredInstances,
			LinkedHashMap<String, Classifier> custom_classifiers, List<CustomSet> cSList, CustomClassifierService ccService, Dataset ds)
			throws Exception {

		if (mapper == null) {
			mapper = new ObjectMapper();
		}
		// Store structure of dataset, set minimum number of instances
		m_Info = header;
		m_Debug = debug;
		
		// if in dead json return
		if (node == null) {
			m_Attribute = -1;
			m_ClassDistribution = null;
			m_Prop = null;
			return;
		}

		// Make leaf if there are no training instances
		if (data.numInstances() == 0) {
			m_Attribute = -1;
			m_ClassDistribution = null;
			m_Prop = null;
			return;
		}

		// Check if node doesn't contain enough instances or is pure
		// or maximum depth reached
		m_ClassDistribution = classProbs.clone();
		cSetList = cSList;
		ccSer = ccService;
		d = ds;

		// if (Utils.sum(m_ClassDistribution) < 2 * m_MinNum
		// || Utils.eq(m_ClassDistribution[Utils.maxIndex(m_ClassDistribution)],
		// Utils
		// .sum(m_ClassDistribution))
		// || ((getMaxDepth() > 0) && (depth >= getMaxDepth()))) {
		// // Make leaf
		// m_Attribute = -1;
		// m_Prop = null;
		// return;
		// }


		// Investigate the selected attribute
		int attIndex = parent_index;

		// options child added by web client developer
		// TODO work with him to make a more meaningful structure...
		JsonNode options = node.get("options");
		if (options == null) {
			return;
		}
		String kind = options.get("kind").asText();
		JsonNode att_name = options.get("attribute_name");
		Boolean getSplitData = false;
		Boolean getInstanceData = false;
		// this allows me to modify the json tree structure to add data about
		// the evaluation
		ObjectNode evalresults = (ObjectNode) options;
		ObjectNode _node = (ObjectNode) node;
		//For Roc - Node Match
		_node.set("roc_uid_0", null);
		_node.set("roc_uid_1", null);

		Map<String, JsonNode> sons = new HashMap<String, JsonNode>();
		// String name = node_name.asText();
		if (kind != null && kind.equals("split_node") && att_name != null) { //
			// attIndex = data.attribute(node_id.asText()).index();
			if (!att_name.asText().equals("")
					&& !att_name.asText().contains("custom_classifier")
					&& !att_name.asText().contains("custom_tree")
					&& !att_name.asText().contains("custom_set")) {
				attIndex = data.attribute(att_name.asText()).index();
			} else {
				if(att_name.asText().contains("custom_set")){
					int ctr = 0;
					for (CustomSet c: cSList) {
						if (c.getId() == Long.valueOf(att_name.asText().replace("custom_set_",""))) {
							break;
						}
						ctr++;
					}
					attIndex = (data.numAttributes() - 1) +custom_classifiers.size()+ ctr;
				} else {
					if(att_name.asText().contains("custom_classifier_new")){
						HashMap mp = ccSer.buildCustomClasifier(data, Long.valueOf(att_name.asText().replace("custom_classifier_new_", "")));
						Classifier fc = (Classifier) mp.get("classifier");
						custom_classifiers.put("custom_classifier_"+mp.get("id"), fc);
						evalresults.put("unique_id", "custom_classifier_"+mp.get("id"));
						evalresults.put("attribute_name", "custom_classifier_"+mp.get("id"));
						att_name = evalresults.get("attribute_name");
					}
					int ctr = 0;
					for (String key : custom_classifiers.keySet()) {
						if (key.equals(att_name.asText())) {
							break;
						}
						ctr++;
					}
					attIndex = (data.numAttributes() - 1) + ctr;
				}
			}
			if (node.get("getSplitData") != null) {
				getSplitData = node.get("getSplitData").asBoolean();
			}
			JsonNode split_values = node.get("children");
			int c = 0;
			if (split_values != null && split_values.size() > 0) {
				for (JsonNode svalue : split_values) {
					String key = svalue.get("name").asText();
					JsonNode son = svalue.get("children").get(0);
					if (key.contains("<")) {
						key = "low";
					} else if (key.contains(">")) {
						key = "high";
					}
					sons.put(key, son);
					c++;
				}
			}
			// LOGGER.debug("Id name "+att_name+" index "+attIndex+" type "+kind+" sons "+c);
		} else {
			// LOGGER.debug("non split node, name "+att_name+" type "+kind);
		}

		double[] vals = new double[data.numAttributes()
				+ custom_classifiers.size() + cSetList.size()];
		double[][][] dists = new double[data.numAttributes()
				+ custom_classifiers.size() + cSetList.size()][0][0];
		double[][] props = new double[data.numAttributes()
				+ custom_classifiers.size() + cSetList.size()][0];
		double[] splits = new double[data.numAttributes()
				+ custom_classifiers.size() + cSetList.size()];
		listOfFc = custom_classifiers;
		// Compute class distributions and value of splitting
		// criterion for each attribute
		HashMap<String, Double> mp = new HashMap<String, Double>();
		if (attIndex >= data.numAttributes() && attIndex < data.numAttributes()+custom_classifiers.size()) {
			mp = distribution(props, dists, attIndex, data, Double.NaN,
					custom_classifiers);
		} else if(attIndex >= data.numAttributes()+custom_classifiers.size()-1){
			mp = distribution(props, dists, attIndex, data, Double.NaN,
					custom_classifiers);
		} else {
			if (options.get("split_point") != null) {
				mp = distribution(props, dists, attIndex, data,
						options.get("split_point").asDouble(),
						custom_classifiers);
			} else {
				mp = distribution(props, dists, attIndex, data, Double.NaN,
						custom_classifiers);
			}
		}

		splits[attIndex] = mp.get("split_point");
		vals[attIndex] = gain(dists[attIndex], priorVal(dists[attIndex]));

		m_Attribute = attIndex;
		double[][] distribution = dists[m_Attribute];

		// stop if input json tree does not contain any more children
		// replacing Utils.gr(vals[m_Attribute], 0)&&
		if (kind != null && kind.equals("split_node") && att_name != null) {
			//Assign Classes for custom sets(visual splits).
			m_ClassAssignment.put("Inside", Utils.maxIndex(dists[m_Attribute][1]));
			m_ClassAssignment.put("Outside", (Utils.maxIndex(dists[m_Attribute][1]) == 1) ? 0 : 1);
			// Build subtrees
			m_SplitPoint = splits[m_Attribute];
			m_Prop = props[m_Attribute];
			Instances[] subsets = splitData(data);
			m_Successors = new ManualTree[distribution.length];

			// record quantity and quality measures for node
			int quantity = 0;
			for (int i = 0; i < distribution.length; i++) {
				quantity += subsets[i].numInstances();
			}
			evalresults.put("bin_size", quantity);
			evalresults.put("infogain", vals[m_Attribute]);
			evalresults.put("majClass", m_Info.classAttribute().value(Utils.maxIndex(m_ClassDistribution)));
			evalresults.put("split_point", m_SplitPoint);
			evalresults.put("orig_split_point", mp.get("orig_split_point"));
			
			if (Boolean.TRUE.equals(getSplitData)) {
				addDistributionData(data, m_Attribute, m_distributionData);
			}

			int maxIndex = 0;
			double maxCount = 0;
			double errors = 0;
			double pct_correct = 0;
			double bin_size = 0;

			for (int i = 0; i < distribution.length; i++) {
				m_Successors[i] = new ManualTree();
				m_Successors[i].setKValue(m_KValue);
				m_Successors[i].setMaxDepth(getMaxDepth());

				// Compute pct_correct from distributions and send to split_node
				bin_size = Utils.sum(distribution[i]);
				maxIndex = Utils.maxIndex(distribution[i]);
				maxCount = distribution[i][maxIndex];
				if (m_Info.classAttribute().value(maxIndex).equals(data.classAttribute().value(1))) {
					errors += bin_size - maxCount;
				} else {
					errors += maxCount;
				}
				// test an instance to see which child node to send its subset
				// down.
				// after split, should hold for all in set
				String child_name = "";
				Instances subset = subsets[i];
				if (subset == null || subset.numInstances() == 0) {
					continue;
				}
				Instance inst = subset.instance(0);
				if (m_Attribute >= data.numAttributes() && m_Attribute<data.numAttributes()+custom_classifiers.size()) {
					double predictedClass = custom_classifiers.get(
							att_name.asText()).classifyInstance(inst);
					child_name = m_Info.classAttribute().value(
							(int) predictedClass);

				} else if(m_Attribute >= data.numAttributes()+custom_classifiers.size() -1 ){
					CustomSet cSet = getReqCustomSet(m_Attribute-(data.numAttributes()-1+custom_classifiers.size()), cSetList);
						JsonNode vertices = mapper.readTree(cSet.getConstraints());
					ArrayList<double[]> attrVertices = generateVerticesList(vertices);
					List<Attribute> aList = generateAttributeList(cSet, data, ds);
					double[] testPoint = new double[2];
					testPoint[0] = inst.value(aList.get(0));
					testPoint[1] = inst.value(aList.get(1));
					int check = checkPointInPolygon(attrVertices, testPoint);
					if(check == 0){
						child_name="Outside";
					} else {
						child_name = "Inside";
					}
				} else {
					// which nominal attribute is this split linked to?
					if (subset.attribute(m_Attribute).isNominal()) {
						child_name = inst.attribute(m_Attribute).value(
								(int) inst.value(m_Attribute));
					}
					// otherwise, if we have a numeric attribute, are we going
					// high or low?
					else if (data.attribute(m_Attribute).isNumeric()) {
						if (inst.value(m_Attribute) < m_SplitPoint) {
							child_name = "low";
						} else {
							child_name = "high";
						}
					}
				}
				m_Successors[i].setM_ClassAssignment((HashMap<String, Integer>) m_ClassAssignment.clone());
				JsonNode son = sons.get(child_name);
				if (son != null) {
					m_Successors[i].buildTree(subsets[i], distribution[i],
							header, m_Debug, depth + 1, son, attIndex,
							m_distributionData, requiredInstances, custom_classifiers, cSList, ccService, ds);
				} else {
					// if we are a split node with no input children, we need to
					// add them into the tree
					// JsonNode split_values = node.get("children");
					if (kind != null && kind.equals("split_node")) {
						ArrayNode children = (ArrayNode) node.get("children");
						if (children == null) {
							children = mapper.createArrayNode();
						}
						ObjectNode child = mapper.createObjectNode();
						child.put("name", child_name);
						ObjectNode c_options = mapper.createObjectNode();
						c_options.put("attribute_name", child_name);
						c_options.put("kind", "split_value");
						child.put("options", c_options);
						children.add(child);
						_node.put("children", children);
						m_Successors[i].buildTree(subsets[i], distribution[i],
								header, m_Debug, depth + 1, child, attIndex,
								m_distributionData, requiredInstances, custom_classifiers, cSList, ccService, ds);

					} else {
						// for leaf nodes, calling again ends the cycle and
						// fills up the bins appropriately
						m_Successors[i].buildTree(subsets[i], distribution[i],
								header, m_Debug, depth + 1, node, attIndex,
								m_distributionData, requiredInstances, custom_classifiers, cSList, ccService, ds);
					}
				}
			}

			pct_correct = (quantity - errors) / quantity;
			evalresults.put("pct_correct", pct_correct);
			// If all successors are non-empty, we don't need to store the class
			// distribution
			boolean emptySuccessor = false;
			for (int i = 0; i < subsets.length; i++) {
				if (m_Successors[i].m_ClassDistribution == null) {
					emptySuccessor = true;
					break;
				}
			}
			if (!emptySuccessor) {
				m_ClassDistribution = null;
			}
		} else {
			m_Attribute = -1;
			if (kind != null && kind.equals("leaf_node")) {
				double bin_size = 0, maxCount = 0;
				int maxIndex = 0;
				double errors = 0;
				double pct_correct = 0;
				if (m_ClassDistribution != null) {
					bin_size = Utils.sum(m_ClassDistribution);
					maxIndex = Utils.maxIndex(m_ClassDistribution); // this is
																	// where it
																	// decides
																	// what
																	// class the
																	// leaf is..
																	// takes the
																	// majority.
					maxCount = m_ClassDistribution[maxIndex];
					errors = bin_size - maxCount;
					pct_correct = (bin_size - errors) / bin_size;
					this.setJsonnode(_node);
				}
				if(node.get("pickInst") != null){
					getInstanceData = node.get("pickInst").asBoolean();
				}
				if (Boolean.TRUE.equals(getInstanceData)) {
					requiredInstances.delete();
					for(int k=0;k<data.numInstances();k++){
						requiredInstances.add(data.instance(k));
					}
				}
				String class_name = m_Info.classAttribute().value(maxIndex);
				_node.put("majClass", class_name);
				if(node.get("setClass")!=null){
					String setClass = node.get("setClass").asText();
					class_name = m_Info.classAttribute().value(m_ClassAssignment.get(setClass));
					this.setJsonnode(_node);
				}
				_node.put("name", class_name);
				evalresults.put("attribute_name", class_name);
				evalresults.put("kind", "leaf_node");
				evalresults.put("bin_size", Utils.doubleToString(bin_size, 2));
				evalresults.put("errors", Utils.doubleToString(errors, 2));
				evalresults.put("pct_correct",
						Utils.doubleToString(pct_correct, 2));
			} else {
				// Make leaf

				// add the data to the json object
				double bin_size = 0, maxCount = 0;
				int maxIndex = 0;
				double errors = 0;
				double pct_correct = 0;
				if (m_ClassDistribution != null) {
					bin_size = Utils.sum(m_ClassDistribution);
					maxIndex = Utils.maxIndex(m_ClassDistribution); // this is
																	// where it
																	// decides
																	// what
																	// class the
																	// leaf is..
																	// takes the
																	// majority.
					maxCount = m_ClassDistribution[maxIndex];
					errors = bin_size - maxCount;
					pct_correct = (bin_size - errors) / bin_size;
				}
				ArrayNode children = (ArrayNode) node.get("children");
				if (children == null) {
					children = mapper.createArrayNode();
				}
				ObjectNode child = mapper.createObjectNode();
				String class_name = m_Info.classAttribute().value(maxIndex);
				child.put("majClass", class_name);
				String nodeName = node.get("name").asText();
				if(nodeName.equals("Inside") || nodeName.equals("Outside")){
					child.put("setClass", nodeName);
					class_name = m_Info.classAttribute().value(m_ClassAssignment.get(nodeName));
				} 
				child.put("name", class_name);
				ObjectNode c_options = mapper.createObjectNode();
				c_options.put("attribute_name", class_name);
				c_options.put("kind", "leaf_node");
				c_options.put("bin_size", Utils.doubleToString(bin_size, 2));
				c_options.put("errors", Utils.doubleToString(errors, 2));
				c_options.put("pct_correct",
						Utils.doubleToString(pct_correct, 2));
				child.put("options", c_options);
				children.add(child);
				_node.put("children", children);
				this.setJsonnode(child);
			}
		}
	}
	
	public int checkPointInPolygon(List<double[]> vertices,double[] testPoint){
		int i, j; 
		Boolean c = false;
		  for (i = 0, j = vertices.size()-1; i < vertices.size(); j = i++) {
		    if ( ((vertices.get(i)[1]>testPoint[1]) != (vertices.get(j)[1]>testPoint[1])) &&
		     (testPoint[0] < (vertices.get(j)[0]-vertices.get(i)[0]) * (testPoint[1]-vertices.get(i)[1]) / (vertices.get(j)[1]-vertices.get(i)[1]) + vertices.get(i)[0]) )
		       c = !c;
		  }
		  if(c){
			  return 1;
		  }
		return 0;
	}
	
	/**
	 * Computes class distribution for an attribute.
	 * 
	 * @param props
	 * @param dists
	 * @param att
	 *            the attribute index
	 * @param data
	 *            the data to work with
	 * @throws Exception
	 *             if something goes wrong
	 */
	protected HashMap<String, Double> distribution(double[][] props,
			double[][][] dists, int att, Instances data,
			double givenSplitPoint,
			HashMap<String, Classifier> custom_classifiers) throws Exception {

		HashMap<String, Double> mp = new HashMap<String, Double>();
		double splitPoint = givenSplitPoint;
		double origSplitPoint = 0;
		Attribute attribute = null;
		double[][] dist = null;
		int indexOfFirstMissingValue = -1;
		String CustomClassifierId = null;
		CustomSet cSet = null;
		if (att >= data.numAttributes() && att < data.numAttributes()+custom_classifiers.size()) {
			CustomClassifierId = getKeyinMap(custom_classifiers, att, data);
		} else if(att>=data.numAttributes()+custom_classifiers.size()) {
			cSet = getReqCustomSet(att-(data.numAttributes()-1+custom_classifiers.size()), cSetList);
		} else {
			attribute = data.attribute(att);
		}
		if (CustomClassifierId == null && cSet == null) {
			if (attribute.isNominal()) {
				// For nominal attributes
				dist = new double[attribute.numValues()][data.numClasses()];
				for (int i = 0; i < data.numInstances(); i++) {
					Instance inst = data.instance(i);
					if (inst.isMissing(att)) {

						// Skip missing values at this stage
						if (indexOfFirstMissingValue < 0) {
							indexOfFirstMissingValue = i;
						}
						continue;
					}
					dist[(int) inst.value(att)][(int) inst.classValue()] += inst
							.weight();
				}
			} else {

				// For numeric attributes
				double[][] currDist = new double[2][data.numClasses()];
				dist = new double[2][data.numClasses()];

				// Sort data
				data.sort(att);

				// Move all instances into second subset
				for (int j = 0; j < data.numInstances(); j++) {
					Instance inst = data.instance(j);
					if (inst.isMissing(att)) {

						// Can stop as soon as we hit a missing value
						indexOfFirstMissingValue = j;
						break;
					}
					currDist[1][(int) inst.classValue()] += inst.weight();
				}

				// Value before splitting
				double priorVal = priorVal(currDist);

				// Save initial distribution
				for (int j = 0; j < currDist.length; j++) {
					System.arraycopy(currDist[j], 0, dist[j], 0, dist[j].length);
				}

				if (Double.isNaN(splitPoint)) {
					// Try all possible split points
					double currSplit = data.instance(0).value(att);
					double currVal, bestVal = -Double.MAX_VALUE;
					for (int i = 0; i < data.numInstances(); i++) {
						Instance inst = data.instance(i);
						if (inst.isMissing(att)) {

							// Can stop as soon as we hit a missing value
							break;
						}

						// Can we place a sensible split point here?
						if (inst.value(att) > currSplit) {

							// Compute gain for split point
							currVal = gain(currDist, priorVal);

							// Is the current split point the best point so far?
							if (currVal > bestVal) {

								// Store value of current point
								bestVal = currVal;

								// Save split point
								splitPoint = (inst.value(att) + currSplit) / 2.0;
								origSplitPoint = splitPoint;

								// Save distribution
								for (int j = 0; j < currDist.length; j++) {
									System.arraycopy(currDist[j], 0, dist[j],
											0, dist[j].length);
								}
							}
						}
						currSplit = inst.value(att);

						// Shift over the weight
						currDist[0][(int) inst.classValue()] += inst.weight();
						currDist[1][(int) inst.classValue()] -= inst.weight();
					}
				} else {
					double currSplit = data.instance(0).value(att);
					double currVal, bestVal = -Double.MAX_VALUE;
					// Split data set using given split point.
					for (int i = 0; i < data.numInstances(); i++) {
						Instance inst = data.instance(i);
						if (inst.isMissing(att)) {
							// Can stop as soon as we hit a missing value
							break;
						}
						if (inst.value(att) > currSplit) {
							// Compute gain for split point
							currVal = gain(currDist, priorVal);
							// Is the current split point the best point so far?
							if (currVal > bestVal) {
								// Store value of current point
								bestVal = currVal;
								// Save computed split point
								origSplitPoint = (inst.value(att) + currSplit) / 2.0;
							}
						}
						currSplit = inst.value(att);
						// Shift over the weight
						currDist[0][(int) inst.classValue()] += inst.weight();
						currDist[1][(int) inst.classValue()] -= inst.weight();
						if (inst.value(att) <= splitPoint) {
							// Save distribution since split point is specified
							for (int j = 0; j < currDist.length; j++) {
								System.arraycopy(currDist[j], 0, dist[j], 0,
										dist[j].length);
							}
						}
					}
				}
			}
		} else if(CustomClassifierId != null) {
			Classifier fc = custom_classifiers.get(CustomClassifierId);
			dist = new double[data.numClasses()][data.numClasses()];
			Instance inst;
			for (int i = 0; i < data.numInstances(); i++) {
				inst = data.instance(i);
				double predictedClass = fc.classifyInstance(inst);
				if (predictedClass != Instance.missingValue()) {
					dist[(int) predictedClass][(int) inst.classValue()] += inst
							.weight();
				}
			}
		} else if(cSet != null) {
			dist = new double[data.numClasses()][data.numClasses()];
			JsonNode vertices = mapper.readTree(cSet.getConstraints());
			ArrayList<double[]> attrVertices = generateVerticesList(vertices);
			List<Attribute> aList = generateAttributeList(cSet, data, d);
			double[] testPoint = new double[2];
			int ctr = 0;
			for(int k=0;k<data.numInstances();k++){
				testPoint = new double[2];
				ctr = 0;
				for(Attribute a:aList){
					if(!data.instance(k).isMissing(a)){
						testPoint[ctr] = data.instance(k).value(a);
						ctr++;
					}
				}
				int check = checkPointInPolygon(attrVertices, testPoint);
				dist[check][(int) data.instance(k).classValue()] += data.instance(k).weight();
			}
		}

		// Compute weights for subsetsCustomClassifierIndex
		props[att] = new double[dist.length];
		for (int k = 0; k < props[att].length; k++) {
			props[att][k] = Utils.sum(dist[k]);
		}
		if (Utils.eq(Utils.sum(props[att]), 0)) {
			for (int k = 0; k < props[att].length; k++) {
				props[att][k] = 1.0 / props[att].length;
			}
		} else {
			Utils.normalize(props[att]);
		}

		// Any instances with missing values ?
		if (indexOfFirstMissingValue > -1) {

			// Distribute weights for instances with missing values
			for (int i = indexOfFirstMissingValue; i < data.numInstances(); i++) {
				Instance inst = data.instance(i);
				if (attribute.isNominal()) {

					// Need to check if attribute value is missing
					if (inst.isMissing(att)) {
						for (int j = 0; j < dist.length; j++) {
							dist[j][(int) inst.classValue()] += props[att][j]
									* inst.weight();
						}
					}
				} else {

					// Can be sure that value is missing, so no test required
					for (int j = 0; j < dist.length; j++) {
						dist[j][(int) inst.classValue()] += props[att][j]
								* inst.weight();
					}
				}
			}
		}

		// Return distribution and split point
		dists[att] = dist;
		mp.put("split_point", splitPoint);
		mp.put("orig_split_point", origSplitPoint);
		return mp;
	}
	
	/**
	 * Computes class distribution of an instance using the decision tree.
	 * 
	 * @param instance
	 *            the instance to compute the distribution for
	 * @return the computed class distribution
	 * @throws Exception
	 *             if computation fails
	 */
	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {

		// default model?
		if (m_ZeroR != null) {
			return m_ZeroR.distributionForInstance(instance);
		}
		
		double[] returnedDist = null;
		
		//Set Parent Node to set m_pred in case custom set occurs.
		if(m_Successors!=null){
			for(int i=0;i<m_Successors.length;i++){
				m_Successors[i].setParentNode(parentNode);
			}
		}
		
		if(m_Info != null){
			if (m_Attribute > -1 && m_Attribute < m_Info.numAttributes()) {

				// Node is not a leaf
				if (instance.isMissing(m_Attribute)) {
						LOGGER.debug("Missing attribute");
					// Value is missing
					returnedDist = new double[m_Info.numClasses()];

					// Split instance up
					for (int i = 0; i < m_Successors.length; i++) {
						double[] help = m_Successors[i]
								.distributionForInstance(instance);
						if (help != null) {
							for (int j = 0; j < help.length; j++) {
								returnedDist[j] += m_Prop[i] * help[j];
							}
						}
					}
				} else if (m_Info.attribute(m_Attribute).isNominal()) {

					// For nominal attributes
					returnedDist = m_Successors[(int) instance.value(m_Attribute)]
							.distributionForInstance(instance);
					parentNode.setJsonnode(m_Successors[(int) instance.value(m_Attribute)].getJsonnode());
				} else {

					// For numeric attributes
					if (instance.value(m_Attribute) < m_SplitPoint) {
						returnedDist = m_Successors[0]
								.distributionForInstance(instance);
						parentNode.setJsonnode(m_Successors[0].getJsonnode());
					} else {
						returnedDist = m_Successors[1]
								.distributionForInstance(instance);
						parentNode.setJsonnode(m_Successors[1].getJsonnode());
					}
				}
			} else if (m_Attribute >= m_Info.numAttributes()-1) {
				if(m_Attribute>=(listOfFc.size()+m_Info.numAttributes())-1){
					CustomSet cSet = getReqCustomSet(m_Attribute-(listOfFc.size()-1+m_Info.numAttributes()), cSetList);
					JsonNode vertices = mapper.readTree(cSet.getConstraints());
					ArrayList<double[]> attrVertices = generateVerticesList(vertices);
					List<Attribute> aList = generateAttributeList(cSet, m_Info, d);
					double[] testPoint = new double[2];
						testPoint[0] = instance.value(aList.get(0));
						testPoint[1] = instance.value(aList.get(1));
						int check = checkPointInPolygon(attrVertices, testPoint);
						if(m_Successors[check].getM_Attribute() == -1){
							parentNode.setM_pred(m_ClassAssignment.get((check == 0) ? "Outside": "Inside"));
						}
						returnedDist = m_Successors[check].distributionForInstance(instance);
						parentNode.setJsonnode(m_Successors[check].getJsonnode());
					
				} else {
					String classifierId = "";
					classifierId = getKeyinMap(listOfFc, m_Attribute, m_Info);
					Classifier fc = listOfFc.get(classifierId);
					double predictedClass = fc.classifyInstance(instance);
					if (predictedClass != Instance.missingValue()) {
						returnedDist = m_Successors[(int) predictedClass]
								.distributionForInstance(instance);
						parentNode.setJsonnode(m_Successors[(int) predictedClass].getJsonnode());
					}
				}
			}
		}

		// Node is a leaf or successor is empty?
		if ((m_Attribute == -1) || (returnedDist == null)) {

			// Is node empty?
			if (m_ClassDistribution == null) {
				if (getAllowUnclassifiedInstances()) {
					return new double[m_Info.numClasses()];
				} else {
					return null;
				}
			}

			// Else return normalized distribution
			double[] normalizedDistribution = m_ClassDistribution.clone();
			try{
				Utils.normalize(normalizedDistribution);
			} catch(Exception e) {
				LOGGER.error("Sum is 0. Coudln't Normalize");
			}
			return normalizedDistribution;
		} else {
			return returnedDist;
		}
	}
	
	/**
	 * Computes class distribution of an instance using the decision tree.
	 * 
	 * @param instance
	 *            the instance to compute the distribution for
	 * @return the computed class distribution
	 * @throws Exception
	 *             if computation fails
	 */
	public double[] predForInstance(Instance instance) throws Exception {

		// default model?
		if (m_ZeroR != null) {
			return m_ZeroR.distributionForInstance(instance);
		}

		double[] returnedDist = null;

		if (m_Attribute > -1 && m_Attribute < m_Info.numAttributes()) {

			// Node is not a leaf
			if (instance.isMissing(m_Attribute)) {

				// Value is missing
				returnedDist = new double[m_Info.numClasses()];

				// Split instance up
				for (int i = 0; i < m_Successors.length; i++) {
					double[] help = m_Successors[i]
							.distributionForInstance(instance);
					if (help != null) {
						for (int j = 0; j < help.length; j++) {
							returnedDist[j] += m_Prop[i] * help[j];
						}
					}
				}
			} else if (m_Info.attribute(m_Attribute).isNominal()) {

				// For nominal attributes
				returnedDist = m_Successors[(int) instance.value(m_Attribute)]
						.distributionForInstance(instance);
			} else {

				// For numeric attributes
				if (instance.value(m_Attribute) < m_SplitPoint) {
					returnedDist = m_Successors[0]
							.distributionForInstance(instance);
				} else {
					returnedDist = m_Successors[1]
							.distributionForInstance(instance);
				}
			}
		} else if (m_Attribute >= m_Info.numAttributes()-1) {
			if(m_Attribute>=(listOfFc.size()+m_Info.numAttributes())-1){
				CustomSet cSet = getReqCustomSet(m_Attribute-(listOfFc.size()-1+m_Info.numAttributes()), cSetList);
				JsonNode vertices = mapper.readTree(cSet.getConstraints());
				ArrayList<double[]> attrVertices = generateVerticesList(vertices);
				List<Attribute> aList = generateAttributeList(cSet, m_Info, d);
				double[] testPoint = new double[2];
					testPoint[0] = instance.value(aList.get(0));
					testPoint[1] = instance.value(aList.get(1));
					int check = checkPointInPolygon(attrVertices, testPoint);
					returnedDist = m_Successors[check]
							.distributionForInstance(instance);
				
			} else {
				String classifierId = "";
				classifierId = getKeyinMap(listOfFc, m_Attribute, m_Info);
				Classifier fc = listOfFc.get(classifierId);
				double predictedClass = fc.classifyInstance(instance);
				if (predictedClass != Instance.missingValue()) {
					returnedDist = m_Successors[(int) predictedClass]
							.distributionForInstance(instance);
				}
			}
		}

		// Node is a leaf or successor is empty?
		if ((m_Attribute == -1) || (returnedDist == null)) {

			// Is node empty?
			if (m_ClassDistribution == null) {
				if (getAllowUnclassifiedInstances()) {
					return new double[m_Info.numClasses()];
				} else {
					return null;
				}
			}

			// Else return normalized distribution
			double[] normalizedDistribution = m_ClassDistribution.clone();
			Utils.normalize(normalizedDistribution);
			return normalizedDistribution;
		} else {
			return returnedDist;
		}
	}
	
	/**
	 * Computes value of splitting criterion after split.
	 * 
	 * @param dist
	 *            the distributions
	 * @param priorVal
	 *            the splitting criterion
	 * @return the gain after the split
	 */
	protected double gain(double[][] dist, double priorVal) {

		return priorVal - ContingencyTables.entropyConditionedOnRows(dist);
	}

	public List<Attribute> generateAttributeList(CustomSet cSet, Instances data, Dataset d){
		List<Feature> fList= cSet.getFeatures();
		List<Attribute> aList = new ArrayList<Attribute>();
		Attribute attr = null;
		Feature f =null;
		for(int i=1;i>=0;i--){
			f = new Feature();
			f = fList.get(i);
			for(org.scripps.branch.entity.Attribute a: f.getAttributes()){
				if(a.getDataset().getId() == d.getId()){
					attr = data.attribute(a.getName());
				}
			}
			aList.add(attr);
		}
		return aList;
	}

	public ArrayList<double[]> generateVerticesList(JsonNode vertices){
		ArrayList<double[]> attrVertices = new ArrayList<double[]>();
		double[] vertex = new double[2];
		int ctr = 0;
		for (JsonNode v : vertices) {
			vertex = new double[2];
			ctr = 0;
			for(JsonNode i:v){
				vertex[ctr] = i.asDouble();
				ctr++;
			}
			attrVertices.add(vertex);
		}
		return attrVertices;
	}

	/**
	 * Get the value of NumFolds.
	 *
	 * @return Value of NumFolds.
	 */
	public boolean getAllowUnclassifiedInstances() {

		return m_AllowUnclassifiedInstances;
	}

	/**
	 * Returns default capabilities of the classifier.
	 * 
	 * @return the capabilities of this classifier
	 */
	@Override
	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();
		result.disableAll();

		// attributes
		result.enable(Capability.NOMINAL_ATTRIBUTES);
		result.enable(Capability.NUMERIC_ATTRIBUTES);
		result.enable(Capability.DATE_ATTRIBUTES);
		result.enable(Capability.MISSING_VALUES);

		// class
		result.enable(Capability.NOMINAL_CLASS);
		result.enable(Capability.MISSING_CLASS_VALUES);

		return result;
	}

	public HashMap getDistributionData() {
		return m_distributionData;
	}

	public JsonNode getJsontree() {
		return jsontree;
	}

	public String getKeyinMap(HashMap<String, Classifier> custom_classifiers,
			int att, Instances data) {
		int ctr = 0;
		String CustomClassifierId = "";
		for (String key : custom_classifiers.keySet()) {
			if (ctr + (data.numAttributes() - 1) == att) {
				CustomClassifierId = key;
			}
			ctr++;
		}
		return CustomClassifierId;
	}
	
	

	/**
	 * Get the value of K.
	 * 
	 * @return Value of K.
	 */
	public int getKValue() {

		return m_KValue;
	}

	public ObjectMapper getMapper() {
		return mapper;
	}

	/**
	 * Get the maximum depth of trh tree, 0 for unlimited.
	 * 
	 * @return the maximum depth.
	 */
	public int getMaxDepth() {
		return m_MaxDepth;
	}

	/**
	 * Get the value of MinNum.
	 * 
	 * @return Value of MinNum.
	 */
	public double getMinNum() {

		return m_MinNum;
	}

	/**
	 * Get the value of NumFolds.
	 *
	 * @return Value of NumFolds.
	 */
	public int getNumFolds() {

		return m_NumFolds;
	}

	/**
	 * Gets options from this classifier.
	 * 
	 * @return the options for the current setup
	 */
	@Override
	public String[] getOptions() {
		Vector<String> result;
		String[] options;
		int i;

		result = new Vector<String>();

		result.add("-K");
		result.add("" + getKValue());

		result.add("-M");
		result.add("" + getMinNum());

		result.add("-S");
		result.add("" + getSeed());

		if (getMaxDepth() > 0) {
			result.add("-depth");
			result.add("" + getMaxDepth());
		}

		if (getNumFolds() > 0) {
			result.add("-N");
			result.add("" + getNumFolds());
		}

		if (getAllowUnclassifiedInstances()) {
			result.add("-U");
		}

		options = super.getOptions();
		for (i = 0; i < options.length; i++)
			result.add(options[i]);

		return result.toArray(new String[result.size()]);
	}

	public CustomSet getReqCustomSet(int att, List<CustomSet> csList){
		CustomSet cs = new CustomSet();
		for(int i=0;i<att;i++){
			cs = csList.get(att);
		}
		return cs;
	}

	public HashMap<String, Integer> getM_ClassAssignment() {
		return m_ClassAssignment;
	}

	public void setM_ClassAssignment(HashMap<String, Integer> m_ClassAssignment) {
		this.m_ClassAssignment = m_ClassAssignment;
	}

	/**
	 * Holds a required instances for distribution
	 * 
	 * @param rootNode
	 */
	public Instances getRequiredInst() {
		return requiredInst;
	}

	/**
	 * Returns the revision string.
	 * 
	 * @return the revision
	 */
	@Override
	public String getRevision() {
		return RevisionUtils.extract("$Revision: 5535 $");
	}

	/**
	 * Gets the seed for the random number generations
	 * 
	 * @return the seed for the random number generation
	 */
	@Override
	public int getSeed() {

		return m_randomSeed;
	}
	
	
	public int getM_pred() {
		return m_pred;
	}

	public void setM_pred(int m_pred) {
		this.m_pred = m_pred;
	}

	/**
	 * Returns a string describing classifier
	 * 
	 * @return a description suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String globalInfo() {

		return "Class for constructing a tree when the structure of the tree has been completely determined elsewhere.";
	}

	/**
	 * Returns graph describing the tree.
	 * 
	 * @return the graph describing the tree
	 * @throws Exception
	 *             if graph can't be computed
	 */
	@Override
	public String graph() throws Exception {

		if (m_Successors == null) {
			throw new Exception("RandomTree: No model built yet.");
		}
		StringBuffer resultBuff = new StringBuffer();
		toGraph(resultBuff, 0, null);
		String result = "digraph RandomTree {\n" + "edge [style=bold]\n"
				+ resultBuff.toString() + "\n}\n";
		return result;
	}

	/**
	 * Returns the type of graph this classifier represents.
	 * 
	 * @return Drawable.TREE
	 */
	@Override
	public int graphType() {
		return Drawable.TREE;
	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String KValueTipText() {
		return "Sets the number of randomly chosen attributes. If 0, log_2(number_of_attributes) + 1 is used.";
	}

	/**
	 * Outputs a leaf.
	 * 
	 * @return the leaf as string
	 * @throws Exception
	 *             if generation fails
	 */
	protected String leafString() throws Exception {

		double sum = 0, maxCount = 0;
		int maxIndex = 0;
		if (m_ClassDistribution != null) {
			sum = Utils.sum(m_ClassDistribution);
			maxIndex = Utils.maxIndex(m_ClassDistribution);
			maxCount = m_ClassDistribution[maxIndex];
		}
		if (m_Info == null) {
			return " : " + "None" + " (" + 0 + "/" + 0 + ")";
		} else {
			return " : " + m_Info.classAttribute().value(maxIndex) + " ("
					+ Utils.doubleToString(sum, 2) + "/"
					+ Utils.doubleToString(sum - maxCount, 2) + ")";
		}
	}

	/**
	 * Lists the command-line options for this classifier.
	 * 
	 * @return an enumeration over all possible options
	 */
	@Override
	public Enumeration<Option> listOptions() {

		Vector<Option> newVector = new Vector<Option>();

		newVector.addElement(new Option(
				"\tNumber of attributes to randomly investigate\n"
						+ "\t(<0 = int(log_2(#attributes)+1)).", "K", 1,
				"-K <number of attributes>"));

		newVector.addElement(new Option(
				"\tSet minimum number of instances per leaf.", "M", 1,
				"-M <minimum number of instances>"));

		newVector.addElement(new Option("\tSeed for random number generator.\n"
				+ "\t(default 1)", "S", 1, "-S <num>"));

		newVector.addElement(new Option(
				"\tThe maximum depth of the tree, 0 for unlimited.\n"
						+ "\t(default 0)", "depth", 1, "-depth <num>"));

		newVector.addElement(new Option("\tNumber of folds for backfitting "
				+ "(default 0, no backfitting).", "N", 1, "-N <num>"));
		newVector.addElement(new Option("\tAllow unclassified instances.", "U",
				0, "-U"));

		Enumeration enu = super.listOptions();
		while (enu.hasMoreElements()) {
			newVector.addElement((Option) enu.nextElement());
		}

		return newVector.elements();
	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String maxDepthTipText() {
		return "The maximum depth of the tree, 0 for unlimited.";
	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String minNumTipText() {
		return "The minimum total weight of the instances in a leaf.";
	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String numFoldsTipText() {
		return "Determines the amount of data used for backfitting. One fold is used for "
				+ "backfitting, the rest for growing the tree. (Default: 0, no backfitting)";
	}

	/**
	 * Computes size of the tree.
	 * 
	 * @return the number of nodes
	 */
	public int numNodes() {

		if (m_Attribute == -1) {
			return 1;
		} else {
			int size = 1;
			for (int i = 0; i < m_Successors.length; i++) {
				size += m_Successors[i].numNodes();
			}
			return size;
		}
	}

	/**
	 * Computes value of splitting criterion before split.
	 * 
	 * @param dist
	 *            the distributions
	 * @return the splitting criterion
	 */
	protected double priorVal(double[][] dist) {

		return ContingencyTables.entropyOverColumns(dist);
	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String seedTipText() {
		return "The random number seed used for selecting attributes.";
	}

	/**
	 * Set the value of AllowUnclassifiedInstances.
	 *
	 * @param newAllowUnclassifiedInstances
	 *            Value to assign to AllowUnclassifiedInstances.
	 */
	public void setAllowUnclassifiedInstances(
			boolean newAllowUnclassifiedInstances) {

		m_AllowUnclassifiedInstances = newAllowUnclassifiedInstances;
	}

	public void setCcSer(CustomClassifierService ccSer) {
		this.ccSer = ccSer;
	}

	public void setCustomRepo(List<CustomSet> csList) {
		this.cSetList = csList;
	}

	public void setDistributionData(HashMap newDistMap) {
		m_distributionData = new HashMap(newDistMap);
	}
	
	/**
	 * Set the value of K.
	 * 
	 * @param k
	 *            Value to assign to K.
	 */
	public void setKValue(int k) {

		m_KValue = k;
	}

	public void setListOfFc(LinkedHashMap<String, Classifier> listOfFc) {
		this.listOfFc = listOfFc;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Set the maximum depth of the tree, 0 for unlimited.
	 * 
	 * @param value
	 *            the maximum depth.
	 */
	public void setMaxDepth(int value) {
		m_MaxDepth = value;
	}

	/**
	 * Set the value of MinNum.
	 * 
	 * @param newMinNum
	 *            Value to assign to MinNum.
	 */
	public void setMinNum(double newMinNum) {

		m_MinNum = newMinNum;
	}

	/**
	 * Set the value of NumFolds.
	 *
	 * @param newNumFolds
	 *            Value to assign to NumFolds.
	 */
	public void setNumFolds(int newNumFolds) {

		m_NumFolds = newNumFolds;
	}
	
	public Dataset getD() {
		return d;
	}

	public void setD(Dataset d) {
		this.d = d;
	}
	
	public ManualTree getParentNode() {
		return parentNode;
	}

	public void setParentNode(ManualTree parentNode) {
		this.parentNode = parentNode;
	}

	/**
	 * Parses a given list of options.
	 * <p/>
	 * 
	 * <!-- options-start --> Valid options are:
	 * <p/>
	 * 
	 * <pre>
	 * -K &lt;number of attributes&gt;
	 *  Number of attributes to randomly investigate
	 *  (&lt;0 = int(log_2(#attributes)+1)).
	 * </pre>
	 * 
	 * <pre>
	 * -M &lt;minimum number of instances&gt;
	 *  Set minimum number of instances per leaf.
	 * </pre>
	 * 
	 * <pre>
	 * -S &lt;num&gt;
	 *  Seed for random number generator.
	 *  (default 1)
	 * </pre>
	 * 
	 * <pre>
	 * -depth &lt;num&gt;
	 *  The maximum depth of the tree, 0 for unlimited.
	 *  (default 0)
	 * </pre>
	 * 
	 * <pre>
	 * -N &lt;num&gt;
	 *  Number of folds for backfitting (default 0, no backfitting).
	 * </pre>
	 * 
	 * <pre>
	 * -U
	 *  Allow unclassified instances.
	 * </pre>
	 * 
	 * <pre>
	 * -D
	 *  If set, classifier is run in debug mode and
	 *  may output additional info to the console
	 * </pre>
	 * 
	 * <!-- options-end -->
	 * 
	 * @param options
	 *            the list of options as an array of strings
	 * @throws Exception
	 *             if an option is not supported
	 */
	@Override
	public void setOptions(String[] options) throws Exception {
		String tmpStr;

		tmpStr = Utils.getOption('K', options);
		if (tmpStr.length() != 0) {
			m_KValue = Integer.parseInt(tmpStr);
		} else {
			m_KValue = 0;
		}

		tmpStr = Utils.getOption('M', options);
		if (tmpStr.length() != 0) {
			m_MinNum = Double.parseDouble(tmpStr);
		} else {
			m_MinNum = 1;
		}

		tmpStr = Utils.getOption('S', options);
		if (tmpStr.length() != 0) {
			setSeed(Integer.parseInt(tmpStr));
		} else {
			setSeed(1);
		}

		tmpStr = Utils.getOption("depth", options);
		if (tmpStr.length() != 0) {
			setMaxDepth(Integer.parseInt(tmpStr));
		} else {
			setMaxDepth(0);
		}
		String numFoldsString = Utils.getOption('N', options);
		if (numFoldsString.length() != 0) {
			m_NumFolds = Integer.parseInt(numFoldsString);
		} else {
			m_NumFolds = 0;
		}

		setAllowUnclassifiedInstances(Utils.getFlag('U', options));

		super.setOptions(options);

		Utils.checkForRemainingOptions(options);
	}

	public void setRequiredInst(Instances requiredInst) {
		this.requiredInst = requiredInst;
	}
	
	public int getM_Attribute() {
		return m_Attribute;
	}

	/**
	 * Set the seed for random number generation.
	 * 
	 * @param seed
	 *            the seed
	 */
	@Override
	public void setSeed(int seed) {

		m_randomSeed = seed;
	}

	/**
	 * Holds a tree structure delivered via json
	 * 
	 * @param rootNode
	 */
	public void setTreeStructure(JsonNode rootNode) {
		jsontree = rootNode;

	}

	/**
	 * Splits instances into subsets based on the given split.
	 * 
	 * @param data
	 *            the data to work with
	 * @return the subsets of instances
	 * @throws Exception
	 *             if something goes wrong
	 */
	protected Instances[] splitData(Instances data) throws Exception {

		// Allocate array of Instances objects
		Instances[] subsets = new Instances[m_Prop.length];
		for (int i = 0; i < m_Prop.length; i++) {
			subsets[i] = new Instances(data, data.numInstances());
		}

		if (m_Attribute >= data.numAttributes()) {
			if(m_Attribute>=listOfFc.size()+data.numAttributes()-1){
				CustomSet cSet = getReqCustomSet(m_Attribute-(data.numAttributes()-1+listOfFc.size()), cSetList);
				JsonNode vertices = mapper.readTree(cSet.getConstraints());
				ArrayList<double[]> attrVertices = generateVerticesList(vertices);
				List<Attribute> aList = generateAttributeList(cSet, data, d);
				double[] testPoint = new double[2];
				int ctr = 0;
				for(int k=0;k<data.numInstances();k++){
					ctr = 0;
					for(Attribute a:aList){
							testPoint[ctr] = data.instance(k).value(a);
						ctr++;
					}
					int check = checkPointInPolygon(attrVertices, testPoint);
						subsets[check].add(data.instance(k));
						continue;
				}	
			} else {
				Classifier fc;
				double predictedClass;
				// Go through the data
				for (int i = 0; i < data.numInstances(); i++) {

					// Get instance
					Instance inst = data.instance(i);
					String classifierId = getKeyinMap(listOfFc, m_Attribute, data);
					fc = listOfFc.get(classifierId);
					predictedClass = fc.classifyInstance(inst);
					if (predictedClass != Instance.missingValue()) {
						subsets[(int) predictedClass].add(inst);
						continue;
					}

					// Else throw an exception
					throw new IllegalArgumentException("Unknown attribute type");
				}
			}
			} else {
			// Go through the data
			for (int i = 0; i < data.numInstances(); i++) {

				// Get instance
				Instance inst = data.instance(i);

				// Does the instance have a missing value?
				if (inst.isMissing(m_Attribute)) {

					// Split instance up
					for (int k = 0; k < m_Prop.length; k++) {
						if (m_Prop[k] > 0) {
							Instance copy = (Instance) inst.copy();
							copy.setWeight(m_Prop[k] * inst.weight());
							subsets[k].add(copy);
						}
					}

					// Proceed to next instance
					continue;
				}

				// Do we have a nominal attribute?
				if (data.attribute(m_Attribute).isNominal()) {
					subsets[(int) inst.value(m_Attribute)].add(inst);

					// Proceed to next instance
					continue;
				}

				// Do we have a numeric attribute?
				if (data.attribute(m_Attribute).isNumeric()) {
					subsets[(inst.value(m_Attribute) < m_SplitPoint) ? 0 : 1]
							.add(inst);

					// Proceed to next instance
					continue;
				}

				// Else throw an exception
				throw new IllegalArgumentException("Unknown attribute type");
			}
		}

		// Save memory
		for (int i = 0; i < m_Prop.length; i++) {
			subsets[i].compactify();
		}

		// Return the subsets
		return subsets;
	}

	/**
	 * Outputs the decision tree as a graph
	 * 
	 * @return the tree as a graph
	 */
	public String toGraph() {

		try {
			StringBuffer resultBuff = new StringBuffer();
			toGraph(resultBuff, 0);
			String result = "digraph Tree {\n" + "edge [style=bold]\n"
					+ resultBuff.toString() + "\n}\n";
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Outputs one node for graph.
	 * 
	 * @param text
	 *            the buffer to append the output to
	 * @param num
	 *            unique node id
	 * @return the next node id
	 * @throws Exception
	 *             if generation fails
	 */
	public int toGraph(StringBuffer text, int num) throws Exception {

		int maxIndex = Utils.maxIndex(m_ClassDistribution);
		String classValue = m_Info.classAttribute().value(maxIndex);

		num++;
		if (m_Attribute == -1) {
			text.append("N" + Integer.toHexString(hashCode()) + " [label=\""
					+ num + ": " + classValue + "\"" + "shape=box]\n");
		} else {
			text.append("N" + Integer.toHexString(hashCode()) + " [label=\""
					+ num + ": " + classValue + "\"]\n");
			for (int i = 0; i < m_Successors.length; i++) {
				text.append("N" + Integer.toHexString(hashCode()) + "->" + "N"
						+ Integer.toHexString(m_Successors[i].hashCode())
						+ " [label=\"" + m_Info.attribute(m_Attribute).name());
				if (m_Info.attribute(m_Attribute).isNumeric()) {
					if (i == 0) {
						text.append(" < "
								+ Utils.doubleToString(m_SplitPoint, 2));
					} else {
						text.append(" >= "
								+ Utils.doubleToString(m_SplitPoint, 2));
					}
				} else {
					text.append(" = " + m_Info.attribute(m_Attribute).value(i));
				}
				text.append("\"]\n");
				num = m_Successors[i].toGraph(text, num);
			}
		}

		return num;
	}

	/**
	 * Outputs one node for graph.
	 * 
	 * @param text
	 *            the buffer to append the output to
	 * @param num
	 *            the current node id
	 * @param parent
	 *            the parent of the nodes
	 * @return the next node id
	 * @throws Exception
	 *             if something goes wrong
	 */
	protected int toGraph(StringBuffer text, int num, ManualTree parent)
			throws Exception {

		num++;
		if (m_Attribute == -1) {
			text.append("N" + Integer.toHexString(ManualTree.this.hashCode())
					+ " [label=\"" + num + leafString() + "\""
					+ " shape=box]\n");

		} else {
			text.append("N" + Integer.toHexString(ManualTree.this.hashCode())
					+ " [label=\"" + num + ": "
					+ m_Info.attribute(m_Attribute).name() + "\"]\n");
			for (int i = 0; i < m_Successors.length; i++) {
				text.append("N"
						+ Integer.toHexString(ManualTree.this.hashCode())
						+ "->" + "N"
						+ Integer.toHexString(m_Successors[i].hashCode())
						+ " [label=\"");
				if (m_Info.attribute(m_Attribute).isNumeric()) {
					if (i == 0) {
						text.append(" < "
								+ Utils.doubleToString(m_SplitPoint, 2));
					} else {
						text.append(" >= "
								+ Utils.doubleToString(m_SplitPoint, 2));
					}
				} else {
					text.append(" = " + m_Info.attribute(m_Attribute).value(i));
				}
				text.append("\"]\n");
				num = m_Successors[i].toGraph(text, num, this);
			}
		}

		return num;
	}

	/**
	 * Outputs the decision tree.
	 * 
	 * @return a string representation of the classifier
	 */
	@Override
	public String toString() {

		// only ZeroR model?
		if (m_ZeroR != null) {
			StringBuffer buf = new StringBuffer();
			buf.append(this.getClass().getName().replaceAll(".*\\.", "") + "\n");
			buf.append(this.getClass().getName().replaceAll(".*\\.", "")
					.replaceAll(".", "=")
					+ "\n\n");
			buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
			buf.append(m_ZeroR.toString());
			return buf.toString();
		}

		if (m_Successors == null) {
			return "ManualTree: no model has been built yet.";
		} else {
			return "\nManualTree\n==========\n"
					+ toString(0)
					+ "\n"
					+ "\nSize of the tree : "
					+ numNodes()
					+ (getMaxDepth() > 0 ? ("\nMax depth of tree: " + getMaxDepth())
							: (""));
		}
	}

	/**
	 * Recursively outputs the tree.
	 * 
	 * @param level
	 *            the current level of the tree
	 * @return the generated subtree
	 */
	protected String toString(int level) {

		try {
			StringBuffer text = new StringBuffer();

			if (m_Attribute == -1 || m_Attribute >= m_Info.numAttributes()) {

				// Output leaf info
				return leafString();
			} else if (m_Info.attribute(m_Attribute).isNominal()) {

				// For nominal attributes
				for (int i = 0; i < m_Successors.length; i++) {
					text.append("\n");
					for (int j = 0; j < level; j++) {
						text.append("|   ");
					}
					text.append(m_Info.attribute(m_Attribute).name() + " = "
							+ m_Info.attribute(m_Attribute).value(i));
					text.append(m_Successors[i].toString(level + 1));
				}
			} else {

				// For numeric attributes
				text.append("\n");
				for (int j = 0; j < level; j++) {
					text.append("|   ");
				}
				text.append(m_Info.attribute(m_Attribute).name() + " < "
						+ Utils.doubleToString(m_SplitPoint, 2));
				text.append(m_Successors[0].toString(level + 1));
				text.append("\n");
				for (int j = 0; j < level; j++) {
					text.append("|   ");
				}
				text.append(m_Info.attribute(m_Attribute).name() + " >= "
						+ Utils.doubleToString(m_SplitPoint, 2));
				text.append(m_Successors[1].toString(level + 1));
			}

			return text.toString();
		} catch (Exception e) {
			LOGGER.error("Excepion",e);
			return "RandomTree: tree can't be printed";
		}
	}
	
	public ObjectNode getJsonnode() {
		return jsonnode;
	}

	public void setJsonnode(ObjectNode jsonnode) {
		this.jsonnode = jsonnode;
	}
}
