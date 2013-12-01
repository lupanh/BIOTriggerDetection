package edu.ktlab.evex.util.classifier.deeplearning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
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
import edu.ktlab.evex.util.classifier.SVMVector;

public class DBNTraining {
	static String corpusName = "GE";
	static String corpusType = "training";
	static String folderTraining = "corpus/" + corpusName + "2013/BioNLP-ST_2013_" + corpusName
			+ "_" + corpusType + "_data";
	static String fileTraining = "model/" + corpusName + "2013/TriggerDetection.dbn.training";
	static String fileModel = "model/" + corpusName + "2013/TriggerDetection.dbn.model";
	static String fileWordlist = "model/" + corpusName + "2013/TriggerDetection.dbn.wordlist";
	static String fileDictionary = "data/" + corpusName + "2013/TriggerDictionary.txt";

	@SuppressWarnings("unchecked")
	public static FeatureGenerator<BIONLPToken, BIONLPSentence>[] mFeatureGenerators = new FeatureGenerator[] {
			new TDTokenFeatureGenerator(fileDictionary), new TDTreeFeatureGenerator(),
			new TDShortestPathFeatureGenerator() };

	static TDContextGenerator contextGenerator;
	static FeatureSet featureSet;
	static List<SVMVector> svm_vectors = new ArrayList<SVMVector>();

	public static void init() {
		contextGenerator = new TDContextGenerator(mFeatureGenerators);
		featureSet = new FeatureSet();
	}

	private static void createVectorTrainingFile() throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileTraining)));

		ArrayList<BIONLPDocument> documents = StandoffCorporaLoading.loadCorpora(folderTraining);
		for (BIONLPDocument doc : documents) {
			System.out.println("Processing " + doc.getIdFile() + "................");
			for (BIONLPSentence sentence : doc.getSentences()) {
				for (BIONLPToken token : sentence.getTokens()) {
					if (token.getAnnotation() == null)
						continue;
					if (token.getAnnotation() != null)
						if (token.getAnnotation().getDescription().equals("ENTITY"))
							continue;
					String label = (token.getAnnotation() == null) ? "NOT_TRIGGER" : token
							.getAnnotation().getType();
					// String label = (token.getAnnotation() == null) ?
					// "NOT_TRIGGER" : "TRIGGER";
					String vector = featureSet.addprintSVMVector(
							contextGenerator.getContext(token, sentence), label, false);
					writer.append(vector).append("\n");
				}
			}
		}
		writer.close();

		FileOutputStream fileOut = new FileOutputStream(fileWordlist);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(featureSet);
		out.close();
		fileOut.close();
	}

	static void loadWordlist() throws Exception {
		FileInputStream fileIn = new FileInputStream(fileWordlist);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		featureSet = (FeatureSet) in.readObject();
		in.close();
		fileIn.close();
	}

	static void loadTrainingfile() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(fileTraining));

		String line = "";
		while ((line = reader.readLine()) != null) {
			if (line.trim().equals(""))
				continue;
			String[] fields = line.split(" ");
			if (fields.length < 2)
				continue;
			SVMVector vector = new SVMVector();
			vector.setLabel(fields[0]);
			for (int i = 1; i < fields.length; i++) {
				String[] segs = fields[i].split(":");
				vector.addFeatureValue(segs[0], segs[0]);
			}
			svm_vectors.add(vector);
		}
		reader.close();
	}

	static void buildFeatureMatrix() {
		int[][] X = new int[svm_vectors.size()][featureSet.getWordlist().size()];
		int[][] Y = new int[svm_vectors.size()][featureSet.getLabels().size()];
		for (int i = 0; i < svm_vectors.size(); i++) {
			Y[i][svm_vectors.get(i).getLabel()] = 1;
			for (int key : svm_vectors.get(i).getFeaturevalues().keys()) {
				X[i][key - 1] = svm_vectors.get(i).getFeaturevalues().get(key);
			}
		}
	}

	static void trainDBN() throws Exception {
		init();
		createVectorTrainingFile();

		System.out.print("Loading feature list......");
		loadWordlist();
		System.out.println("completed.");
		System.out.print("Loading training file......");
		loadTrainingfile();
		System.out.println("completed.");

		Random rng = new Random(123);

		double pretrain_lr = 0.1;
		int pretraining_epochs = 5;
		int k = 1;
		double finetune_lr = 0.1;
		int finetune_epochs = 5;

		int train_N = svm_vectors.size();
		int n_ins = featureSet.getWordlist().size();
		int n_outs = featureSet.getLabels().size();
		//DoubleMatrix2D X = new SparseDoubleMatrix2D(svm_vectors.size(), featureSet.getWordlist()
		//		.size());
		DoubleMatrix2D Y = new SparseDoubleMatrix2D(svm_vectors.size(), featureSet.getLabels()
				.size());
		for (int i = 0; i < svm_vectors.size(); i++) {
			Y.set(i, svm_vectors.get(i).getLabel(), 1);
			//for (int key : svm_vectors.get(i).getFeaturevalues().keys()) {
			//	X.set(i, key - 1, svm_vectors.get(i).getFeaturevalues().get(key));
			//}
		}

		int[] hidden_layer_sizes = { 100, 100 };
		int n_layers = hidden_layer_sizes.length;
		System.out.println(train_N);
		System.out.println(n_ins);

		System.out.print("Construct DBN......");
		DBN dbn = new DBN(train_N, n_ins, hidden_layer_sizes, n_outs, n_layers, rng);
		System.out.println("completed.");

		System.out.print("Pretrain......");
		dbn.pretrain(svm_vectors, pretrain_lr, k, pretraining_epochs);
		System.out.println("completed.");

		System.out.print("Finetune......");
		dbn.finetune(svm_vectors, Y, finetune_lr, finetune_epochs);
		System.out.println("completed.");

		dbn.saveModel(fileModel);

		// test
		double[][] test_Y = new double[svm_vectors.size()][n_outs];
		for (int i = 0; i < svm_vectors.size(); i++) {
			dbn.predict(svm_vectors.get(i), test_Y[i]);
			for (int j = 0; j < n_outs; j++) {
				System.out.print(test_Y[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) throws Exception {
		trainDBN();
	}

}
