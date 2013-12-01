package edu.ktlab.evex.util.classifier.deeplearning;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import edu.ktlab.evex.corpora.bean.BIONLPDocument;
import edu.ktlab.evex.corpora.bean.BIONLPSentence;
import edu.ktlab.evex.corpora.bean.BIONLPToken;
import edu.ktlab.evex.corpora.processing.StandoffCorporaLoading;
import edu.ktlab.evex.triggerdetection.feature.TDContextGenerator;
import edu.ktlab.evex.triggerdetection.feature.TDShortestPathFeatureGenerator;
import edu.ktlab.evex.triggerdetection.feature.TDTokenFeatureGenerator;
import edu.ktlab.evex.triggerdetection.feature.TDTreeFeatureGenerator;
import edu.ktlab.evex.util.classifier.FeatureGenerator;
import edu.ktlab.evex.util.classifier.FeatureSet;
import gnu.trove.map.hash.TObjectIntHashMap;

public class DBNPredict {
	static String corpusName = "GE";
	static String folderTesting = "corpus/" + corpusName + "2013/BioNLP-ST_2013_" + corpusName + "_training_data";
	static String fileModel = "model/" + corpusName + "2013/TriggerDetection.dbn.model";
	static String fileWordlist = "model/" + corpusName + "2013/TriggerDetection.dbn.wordlist";
	static String fileDictionary = "data/" + corpusName + "2013/TriggerDictionary.txt";

	TDContextGenerator contextGenerator;
	FeatureSet featureSet;
	DBN model;

	@SuppressWarnings("unchecked")
	public FeatureGenerator<BIONLPToken, BIONLPSentence>[] mFeatureGenerators = new FeatureGenerator[] {
			new TDTokenFeatureGenerator(fileDictionary), new TDTreeFeatureGenerator(), new TDShortestPathFeatureGenerator() };

	public void init() {
		contextGenerator = new TDContextGenerator(mFeatureGenerators);
	}

	public DBNPredict() throws Exception {
		init();
		loadModel();
		loadWordlist();
	}

	@SuppressWarnings("static-access")
	public DBNPredict(String model, String wordlist) throws Exception {
		this.fileModel = model;
		this.fileWordlist = wordlist;
		init();
		loadModel();
		loadWordlist();
	}

	void loadWordlist() throws Exception {
		FileInputStream fileIn = new FileInputStream(fileWordlist);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		featureSet = (FeatureSet) in.readObject();
		in.close();
		fileIn.close();
	}

	void loadModel() throws Exception {
		model = new DBN(fileModel);
		System.out.println(model.n_ins);
	}

	public int classify(BIONLPToken token, BIONLPSentence sentence) {
		TreeMap<Integer, Integer> vector = featureSet.addStringFeatureVector(contextGenerator.getContext(token, sentence),
				((token.getAnnotation() == null) ? "NOT_TRIGGER" : token.getAnnotation().getType()), true);

		DoubleMatrix1D svm_vector = new SparseDoubleMatrix1D(model.n_ins);
		for (int key : vector.keySet()) {
			if (key == featureSet.getLabelKey())
				continue;
			svm_vector.set(key - 1, vector.get(key));
		}
		double[] Y = new double[model.n_outs];
		model.predict(svm_vector, Y);
		double bestScore = 0.0;
		int bestLabel = 0;
		for (int i = 0; i < Y.length; i++) {
			System.out.print(Y[i] + " ");
			if (Y[i] > bestScore) {
				bestScore = Y[i];
				bestLabel = i;
			}
		}
		System.out.println();
		return bestLabel;
	}

	public FeatureSet getFeatureSet() {
		return featureSet;
	}

	public void setFeatureSet(FeatureSet featureSet) {
		this.featureSet = featureSet;
	}

	static TObjectIntHashMap<String> statistical = new TObjectIntHashMap<String>();
	static Set<String> labels = new HashSet<String>();

	public static void main(String[] args) throws Exception {
		DBNPredict classifier = new DBNPredict();
		ArrayList<BIONLPDocument> documents = StandoffCorporaLoading.loadCorpora(folderTesting);

		for (BIONLPDocument doc : documents) {
			for (BIONLPSentence sentence : doc.getSentences()) {
				for (BIONLPToken token : sentence.getTokens()) {
					if (token.getAnnotation() == null)
						continue;
					if (token.getAnnotation() != null)
						if (token.getAnnotation().getDescription().equals("ENTITY"))
							continue;
					int output = classifier.classify(token, sentence);
					String label = (token.getAnnotation() == null) ? "NOT_TRIGGER" : token.getAnnotation().getType();

					// if (label.equals("NOT_TRIGGER")) continue;
					// Set<String> dictLabels =
					// dictionary.lookup(token.getTextToken().toLowerCase());
					// if (dictLabels.size() > 0) continue;

					String predict = classifier.getFeatureSet().getLabels().get(output);
					// System.out.println("Labeled:" + label + "\tPredict:" +
					// predict);

					if (label.equals(predict)) {
						// if (label.equals(predict) &&
						// !label.equals("NOT_TRIGGER")) {
						statistical.adjustOrPutValue("TP_" + label, 1, 1);
					}
					// if (!label.equals("NOT_TRIGGER")) {
					labels.add(label);
					statistical.adjustOrPutValue("NL_" + label, 1, 1);
					// }
					// if (!predict.equals("NOT_TRIGGER")) {
					statistical.adjustOrPutValue("NP_" + predict, 1, 1);
					// }
				}
			}
		}

		int allTP = 0;
		int allNP = 0;
		int allNL = 0;
		for (String label : labels) {
			int tp = statistical.get("TP_" + label);
			int np = statistical.get("NP_" + label);
			int nl = statistical.get("NL_" + label);
			allTP += tp;
			allNP += np;
			allNL += nl;
			double precision = (np == 0) ? 0 : (double) tp / np;
			double recall = (nl == 0) ? 0 : (double) tp / nl;
			System.out.print(label + "\t");
			System.out.print("Precision:" + tp + "/" + np + "=" + precision + "\t");
			System.out.print("Recall:" + tp + "/" + nl + "=" + recall + "\t");
			System.out.print("F1:" + (double) 2 * precision * recall / (precision + recall) + "\t");
			System.out.println();
		}

		double allPrecision = (allNP == 0) ? 0 : (double) allTP / allNP;
		double allRecall = (allNL == 0) ? 0 : (double) allTP / allNL;
		System.out.print("ALL\t");
		System.out.print("Precision:" + allTP + "/" + allNP + "=" + allPrecision + "\t");
		System.out.print("Recall:" + allTP + "/" + allNL + "=" + allRecall + "\t");
		System.out.print("F1:" + (double) 2 * allPrecision * allRecall / (allPrecision + allRecall) + "\t");
		System.out.println();
	}
}
