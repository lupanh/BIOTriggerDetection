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
import edu.ktlab.evex.util.classifier.Classifier;
import edu.ktlab.evex.util.classifier.FeatureGenerator;
import edu.ktlab.evex.util.classifier.FeatureSet;
import gnu.trove.map.hash.TObjectIntHashMap;

public class TDBaggingClassifierL1 implements Classifier {
	static String corpusName = "CG";
	static String corpusType = "development";
	static String folderTesting = "corpus/" + corpusName + "2013/BioNLP-ST_2013_" + corpusName + "_" + corpusType + "_data";
	String fileModel = "model/" + corpusName + "2013/TriggerDetection.training.2L.L1.model";
	String fileWordlist = "model/" + corpusName + "2013/TriggerDetection.training.2L.L1.wordlist";
	static String fileDictionary = "data/" + corpusName + "2013/TriggerDictionary.txt";
	int numModels = 50;

	@SuppressWarnings("unchecked")
	public FeatureGenerator<BIONLPToken, BIONLPSentence>[] mFeatureGenerators = new FeatureGenerator[] {
			new TDTokenFeatureGenerator(fileDictionary), new TDTreeFeatureGenerator(), new TDShortestPathFeatureGenerator() };

	static TDContextGenerator contextGenerator;
	ArrayList<Model> models = new ArrayList<Model>();
	ArrayList<FeatureSet> featureSets = new ArrayList<FeatureSet>();

	void init() {
		contextGenerator = new TDContextGenerator(mFeatureGenerators);
	}

	public TDBaggingClassifierL1() throws Exception {
		init();
		loadModels();
		loadWordlists();
	}

	public TDBaggingClassifierL1(String model, String wordlist) throws Exception {
		this.fileModel = model;
		this.fileWordlist = wordlist;
		init();
		loadModels();
		loadWordlists();
	}

	void loadWordlists() throws Exception {
		for (int i = 0; i < numModels; i++)
			featureSets.add((FeatureSet) FileHelper.readObjectFromFile(new File(fileWordlist + i)));
	}

	void loadModels() throws Exception {
		for (int i = 0; i < numModels; i++)
			models.add(Linear.loadModel(new File(fileModel + i)));
	}

	public String predict(ArrayList<String> features, String label) {
		String predict = "";

		if (features == null)
			return predict;

		int max = 0;
		TObjectIntHashMap<String> predicts = new TObjectIntHashMap<String>();

		for (int i = 0; i < numModels; i++) {
			TreeMap<Integer, Integer> vector = featureSets.get(i).addStringFeatureVector(features, label, true);
			if (vector == null)
				throw new IllegalArgumentException("Vector is error!!!");

			ArrayList<FeatureNode> vfeatures = new ArrayList<FeatureNode>();
			for (int key : vector.keySet()) {
				if (key == featureSets.get(i).getLabelKey())
					continue;
				FeatureNode featurenode = new FeatureNode(key, vector.get(key));
				vfeatures.add(featurenode);
			}
			double output = Linear.predict(models.get(i), vfeatures.toArray(new FeatureNode[vfeatures.size()]));
			String elementPredict = featureSets.get(i).getLabels().get((int) output);
			predicts.adjustOrPutValue(elementPredict, 1, 1);
		}

		for (Object out : predicts.keys()) {
			if (predicts.get(out) > max) {
				predict = (String) out;
				max = predicts.get(out);
			}
		}
		return predict;
	}

	public static void main(String[] args) throws Exception {
		TDBaggingClassifierL1 classifier = new TDBaggingClassifierL1();
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
					String label = (token.getAnnotation() == null) ? "NOT_TRIGGER" : "TRIGGER";
					ArrayList<String> features = contextGenerator.getContext(token, sentence);

					String predict = classifier.predict(features, label);
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
		// Fscore.printF1(labels, statistical);
		System.out.println(countErr);
	}

}
