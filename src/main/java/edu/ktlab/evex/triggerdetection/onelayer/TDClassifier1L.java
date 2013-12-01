/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.triggerdetection.onelayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

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
import edu.ktlab.evex.util.classifier.Classifier;
import edu.ktlab.evex.util.classifier.FeatureGenerator;
import edu.ktlab.evex.util.classifier.FeatureSet;
import gnu.trove.map.hash.TObjectIntHashMap;

public class TDClassifier1L implements Classifier {
	static String corpusName = "CG";
	static String corpusType = "development";
	static String folderTesting = "corpus/" + corpusName + "2013/BioNLP-ST_2013_" + corpusName + "_" + corpusType + "_data";
	String fileModel = "model/" + corpusName + "2013/TriggerDetection.training.1L.model";
	String fileWordlist = "model/" + corpusName + "2013/TriggerDetection.training.1L.wordlist";
	static String fileDictionary = "data/" + corpusName + "2013/TriggerDictionary.txt";

	static TDContextGenerator contextGenerator;
	Model model;
	FeatureSet featureSet;

	@SuppressWarnings("unchecked")
	public FeatureGenerator<BIONLPToken, BIONLPSentence>[] mFeatureGenerators = new FeatureGenerator[] {
			new TDTokenFeatureGenerator(fileDictionary), new TDTreeFeatureGenerator(), new TDShortestPathFeatureGenerator() };

	public void init() {
		contextGenerator = new TDContextGenerator(mFeatureGenerators);
	}

	public TDClassifier1L() throws Exception {
		init();
		loadModel();
		loadWordlist();
	}

	public TDClassifier1L(String model, String wordlist) throws Exception {
		this.fileModel = model;
		this.fileWordlist = wordlist;
		init();
		loadModel();
		loadWordlist();
	}

	void loadWordlist() throws Exception {
		featureSet = (FeatureSet) FileHelper.readObjectFromFile(new File(fileWordlist));
	}

	void loadModel() throws Exception {
		model = Linear.loadModel(new File(fileModel));
	}

	public double classify(ArrayList<String> features, String label) {
		TreeMap<Integer, Integer> vector = featureSet.addStringFeatureVector(features, label, true);
		if (vector == null)
			throw new IllegalArgumentException("Vector is error!!!");
		ArrayList<FeatureNode> vfeatures = new ArrayList<FeatureNode>();
		for (int key : vector.keySet()) {
			if (key == featureSet.getLabelKey())
				continue;
			FeatureNode featurenode = new FeatureNode(key, vector.get(key));
			vfeatures.add(featurenode);
		}

		double output = Linear.predict(model, vfeatures.toArray(new FeatureNode[vfeatures.size()]));
		return output;
	}

	public String predict(ArrayList<String> features, String label) {
		String predict = "";
		double output;
		try {
			output = classify(features, label);
		} catch (IllegalArgumentException e) {
			return predict;
		}
		predict = featureSet.getLabels().get((int) output);

		return predict;
	}

	public String predict(BIONLPToken token, BIONLPSentence sentence, String label) {
		ArrayList<String> features = contextGenerator.getContext(token, sentence);
		return predict(features, label);
	}

	public FeatureSet getFeatureSet() {
		return featureSet;
	}

	public void setFeatureSet(FeatureSet featureSet) {
		this.featureSet = featureSet;
	}

	public static void main(String[] args) throws Exception {
		TDClassifier1L classifier = new TDClassifier1L();
		TObjectIntHashMap<String> statistical = new TObjectIntHashMap<String>();
		Set<String> labels = new HashSet<String>();

		ArrayList<BIONLPDocument> documents = StandoffCorporaLoading.loadCorpora(folderTesting);
		for (BIONLPDocument doc : documents) {
			for (BIONLPSentence sentence : doc.getSentences()) {
				for (BIONLPToken token : sentence.getTokens()) {
					if (token.getAnnotation() != null)
						if (token.getAnnotation().getDescription().equals("ENTITY"))
							continue;
					String label = (token.getAnnotation() == null) ? "NOT_TRIGGER" : token.getAnnotation().getType();
					ArrayList<String> features = contextGenerator.getContext(token, sentence);
					String predict = classifier.predict(features, label);
					
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
	}
}
