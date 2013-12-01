/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.triggerdetection.twolayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import edu.ktlab.evex.corpora.bean.BIONLPDocument;
import edu.ktlab.evex.corpora.bean.BIONLPSentence;
import edu.ktlab.evex.corpora.bean.BIONLPToken;
import edu.ktlab.evex.corpora.processing.StandoffCorporaLoading;
import edu.ktlab.evex.triggerdetection.feature.TDContextGenerator;
import edu.ktlab.evex.triggerdetection.feature.TDShortestPathFeatureGenerator;
import edu.ktlab.evex.triggerdetection.feature.TDTokenFeatureGenerator;
import edu.ktlab.evex.triggerdetection.feature.TDTreeFeatureGenerator;
import edu.ktlab.evex.util.FileHelper;
import edu.ktlab.evex.util.Fscore;
import edu.ktlab.evex.util.classifier.FeatureGenerator;
import edu.ktlab.evex.util.classifier.FeatureSet;
import gnu.trove.map.hash.TObjectIntHashMap;

public class TDClassifier2L {
	static String corpusName = "CG";
	static String corpusType = "development";
	static String folderTesting = "corpus/" + corpusName + "2013/BioNLP-ST_2013_" + corpusName + "_" + corpusType + "_data";
	String fileModelL1 = "model/" + corpusName + "2013/TriggerDetection.training.2L.L1.model";
	String fileModelL2 = "model/" + corpusName + "2013/TriggerDetection.training.2L.L2.model";
	String fileWordlistL1 = "model/" + corpusName + "2013/TriggerDetection.training.2L.L1.wordlist";
	String fileWordlistL2 = "model/" + corpusName + "2013/TriggerDetection.training.2L.L2.wordlist";
	static String fileDictionary = "data/" + corpusName + "2013/TriggerDictionary.txt";

	@SuppressWarnings("unchecked")
	public FeatureGenerator<BIONLPToken, BIONLPSentence>[] mFeatureGenerators = new FeatureGenerator[] {
			new TDTokenFeatureGenerator(fileDictionary), new TDTreeFeatureGenerator(), new TDShortestPathFeatureGenerator() };

	static TDContextGenerator contextGenerator;
	Model modelL1;
	Model modelL2;
	FeatureSet featureSetL1;
	FeatureSet featureSetL2;

	void init() {
		contextGenerator = new TDContextGenerator(mFeatureGenerators);
	}

	public TDClassifier2L() throws Exception {
		init();
		loadModel();
		loadWordlist();
	}

	public TDClassifier2L(String modelL1, String modelL2, String wordlistL1, String wordlistL2) throws Exception {
		this.fileModelL1 = modelL1;
		this.fileModelL2 = modelL2;
		this.fileWordlistL1 = wordlistL1;
		this.fileWordlistL1 = wordlistL2;
		init();
		loadModel();
		loadWordlist();
	}

	void loadWordlist() throws Exception {
		featureSetL1 = (FeatureSet) FileHelper.readObjectFromFile(new File(fileWordlistL1));
		featureSetL2 = (FeatureSet) FileHelper.readObjectFromFile(new File(fileWordlistL2));
	}

	void loadModel() throws Exception {
		modelL1 = Linear.loadModel(new File(fileModelL1));
		modelL2 = Linear.loadModel(new File(fileModelL2));
	}

	public double classifyL1(ArrayList<String> features, String label) {
		if (features == null)
			throw new IllegalArgumentException("Features is error!!!");

		TreeMap<Integer, Integer> vectorL1 = featureSetL1.addStringFeatureVector(features, label, true);
		if (vectorL1 == null)
			throw new IllegalArgumentException("Vector is error!!!");

		ArrayList<FeatureNode> vfeatures = new ArrayList<FeatureNode>();
		for (int key : vectorL1.keySet()) {
			if (key == featureSetL1.getLabelKey())
				continue;
			FeatureNode featurenode = new FeatureNode(key, vectorL1.get(key));
			vfeatures.add(featurenode);
		}

		double output = Linear.predict(modelL1, vfeatures.toArray(new FeatureNode[vfeatures.size()]));
		return output;
	}

	public double classifyL2(ArrayList<String> features, String label) {
		if (features == null)
			throw new IllegalArgumentException("Features is error!!!");

		TreeMap<Integer, Integer> vectorL2 = featureSetL2.addStringFeatureVector(features, label, true);
		if (vectorL2 == null)
			throw new IllegalArgumentException("Vector is error!!!");

		ArrayList<FeatureNode> vfeatures = new ArrayList<FeatureNode>();
		for (int key : vectorL2.keySet()) {
			if (key == featureSetL2.getLabelKey())
				continue;
			FeatureNode featurenode = new FeatureNode(key, vectorL2.get(key));
			vfeatures.add(featurenode);
		}

		double output = Linear.predict(modelL2, vfeatures.toArray(new FeatureNode[vfeatures.size()]));
		return output;
	}

	public FeatureSet getFeatureSetL1() {
		return featureSetL1;
	}

	public FeatureSet getFeatureSetL2() {
		return featureSetL2;
	}

	public void setFeatureSetL1(FeatureSet featureSetL1) {
		this.featureSetL1 = featureSetL1;
	}

	public void setFeatureSetL2(FeatureSet featureSetL2) {
		this.featureSetL2 = featureSetL2;
	}

	public String predict(ArrayList<String> features, String labelL1, String labelL2) {
		String predict = "";
		double output = 0;
		try {
			output = classifyL1(features, labelL1);
		} catch (IllegalArgumentException e) {
			return "";
		}
		String predictL1 = featureSetL1.getLabels().get((int) output);
		if (predictL1.contains("NOT_TRIGGER")) {
			predict = "NOT_TRIGGER";
		} else {
			try {
				output = classifyL2(features, labelL2);
			} catch (IllegalArgumentException e) {
				return "";
			}
			predict = featureSetL2.getLabels().get((int) output);
		}

		return predict;
	}

	public static void main(String[] args) throws Exception {
		TDClassifier2L classifier = new TDClassifier2L();
		TObjectIntHashMap<String> statistical = new TObjectIntHashMap<String>();
		Set<String> labels = new TreeSet<String>();
		int countErr = 0;
		ArrayList<BIONLPDocument> documents = StandoffCorporaLoading.loadCorpora(folderTesting, true);
		for (BIONLPDocument doc : documents) {
			for (BIONLPSentence sentence : doc.getSentences()) {
				for (BIONLPToken token : sentence.getTokens()) {
					if (token.getAnnotation() != null)
						if (token.getAnnotation().getDescription().equals("ENTITY"))
							continue;
					String label = (token.getAnnotation() == null) ? "NOT_TRIGGER" : token.getAnnotation().getType();
					String labelL1 = "";
					String labelL2 = "";
					if (label.contains("NOT_TRIGGER")) {
						labelL1 = "NOT_TRIGGER";
						labelL2 = "NOT_TRIGGER";
					} else {
						labelL1 = "TRIGGER";
						labelL2 = label;
					}
					ArrayList<String> features = contextGenerator.getContext(token, sentence);

					String predict = classifier.predict(features, labelL1, labelL2);
					if (predict.equals("")) {
						countErr++;
						continue;
					}

					if (label.equals(predict)) {
						statistical.adjustOrPutValue("TP_" + label, 1, 1);
					}
					labels.add(label);
					statistical.adjustOrPutValue("NL_" + label, 1, 1);
					statistical.adjustOrPutValue("NP_" + predict, 1, 1);
				}
			}
		}
		Fscore.printF1(labels, statistical, "NOT_TRIGGER");
		System.out.println(countErr);
	}

}
