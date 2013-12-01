package edu.ktlab.evex.util.classifier.deeplearning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import edu.ktlab.evex.util.classifier.SVMVector;

public class DBNSVMFormatTraining {
	static String fileTraining = "model/GE2013/TriggerDetection.training";
	static String fileModel = "model/GE2013/TriggerDetection.dbn.model";

	static List<SVMVector> svm_vectors = new ArrayList<SVMVector>();
	static int numFeatures = 0;
	static int numLabel = 0;

	static void loadTrainingfile() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(fileTraining));

		String line = "";
		Set<String> labels = new HashSet<String>();
		while ((line = reader.readLine()) != null) {
			if (line.trim().equals(""))
				continue;
			String[] fields = line.split(" ");
			if (fields.length < 2)
				continue;
			SVMVector vector = new SVMVector();
			vector.setLabel(fields[0]);
			labels.add(fields[0]);
			for (int i = 1; i < fields.length; i++) {
				String[] segs = fields[i].split(":");
				vector.addFeatureValue(segs[0], segs[1]);
				if (Integer.parseInt(segs[0]) > numFeatures)
					numFeatures = Integer.parseInt(segs[0]);
			}
			svm_vectors.add(vector);
		}
		numLabel = labels.size();
		reader.close();
	}

	static void trainDBN() throws Exception {
		System.out.print("Loading training file......");
		loadTrainingfile();
		System.out.println("completed.");
		Random rng = new Random(123);

		double pretrain_lr = 0.1;
		int pretraining_epochs = 500;
		int k = 1;
		double finetune_lr = 0.1;
		int finetune_epochs = 500;

		int train_N = svm_vectors.size();
		int n_ins = numFeatures;
		int n_outs = numLabel;
		//DoubleMatrix2D X = new SparseDoubleMatrix2D(svm_vectors.size(), numFeatures);
		DoubleMatrix2D Y = new SparseDoubleMatrix2D(svm_vectors.size(), numLabel);
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
		System.out.println(n_outs);

		System.out.print("Construct DBN......");
		DBN dbn = new DBN(
				
				train_N, n_ins, hidden_layer_sizes, n_outs, n_layers, rng);
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
			//dbn.predict(X.viewRow(i), test_Y[i]);
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
