package edu.ktlab.evex.util.classifier.deeplearning;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Random;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

import edu.ktlab.evex.util.classifier.SVMVector;

public class DBN implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public int N;
	public int n_ins;
	public int[] hidden_layer_sizes;
	public int n_outs;
	public int n_layers;
	public HiddenLayer[] sigmoid_layers;
	public RBM[] rbm_layers;
	public LogisticRegression log_layer;
	public Random rng;

	public static double sigmoid(double x) {
		return 1.0 / (1.0 + Math.pow(Math.E, -x));
	}

	public DBN(DBN dbn) {
		this(dbn.N, dbn.n_ins, dbn.hidden_layer_sizes, dbn.n_outs, dbn.n_layers, dbn.rng);
	}

	public DBN(String model) {
		try {
			DBN dbn = loadModel(model);
			this.N = dbn.N;
			this.n_ins = dbn.n_ins;
			this.hidden_layer_sizes = dbn.hidden_layer_sizes;
			this.n_outs = dbn.n_outs;
			this.n_layers = dbn.n_layers;
			this.sigmoid_layers = dbn.sigmoid_layers;
			this.rbm_layers = dbn.rbm_layers;
			this.log_layer = dbn.log_layer;
			this.rng = dbn.rng;
		} catch (Exception e) {
			System.err.print(e);
		}
	}

	public DBN loadModel(String model) throws Exception {
		FileInputStream fileIn = new FileInputStream(model);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		DBN dbn = (DBN) in.readObject();
		in.close();
		fileIn.close();
		return dbn;
	}

	public void saveModel(String model) throws Exception {
		FileOutputStream fileOut = new FileOutputStream(model);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(this);
		out.close();
		fileOut.close();
	}

	public DBN(int N, int n_ins, int[] hidden_layer_sizes, int n_outs, int n_layers, Random rng) {
		int input_size;

		this.N = N;
		this.n_ins = n_ins;
		this.hidden_layer_sizes = hidden_layer_sizes;
		this.n_outs = n_outs;
		this.n_layers = n_layers;

		this.sigmoid_layers = new HiddenLayer[n_layers];
		this.rbm_layers = new RBM[n_layers];

		if (rng == null)
			this.rng = new Random(1234);
		else
			this.rng = rng;

		// construct multi-layer
		for (int i = 0; i < this.n_layers; i++) {
			if (i == 0) {
				input_size = this.n_ins;
			} else {
				input_size = this.hidden_layer_sizes[i - 1];
			}

			// construct sigmoid_layer
			this.sigmoid_layers[i] = new HiddenLayer(this.N, input_size,
					this.hidden_layer_sizes[i], null, null, rng);

			// construct rbm_layer
			this.rbm_layers[i] = new RBM(this.N, input_size, this.hidden_layer_sizes[i],
					this.sigmoid_layers[i].W, this.sigmoid_layers[i].b, null, rng);
		}

		// layer for output using LogisticRegression
		this.log_layer = new LogisticRegression(this.N, this.hidden_layer_sizes[this.n_layers - 1],
				this.n_outs);
	}

	public void pretrain(DoubleMatrix2D train_X, double lr, int k, int epochs) {
		int[] layer_input = new int[0];
		int prev_layer_input_size;
		int[] prev_layer_input;

		for (int i = 0; i < n_layers; i++) { // layer-wise
			for (int epoch = 0; epoch < epochs; epoch++) { // training epochs
				System.out.println("Pretrain epoch " + epoch);
				long current = System.currentTimeMillis();
				for (int n = 0; n < N; n++) { // input x1...xN
					// layer input
					for (int l = 0; l <= i; l++) {

						if (l == 0) {
							layer_input = new int[n_ins];
							for (int j = 0; j < n_ins; j++)
								layer_input[j] = (int) train_X.get(n, j);
						} else {
							if (l == 1)
								prev_layer_input_size = n_ins;
							else
								prev_layer_input_size = hidden_layer_sizes[l - 2];

							prev_layer_input = new int[prev_layer_input_size];
							for (int j = 0; j < prev_layer_input_size; j++)
								prev_layer_input[j] = layer_input[j];

							layer_input = new int[hidden_layer_sizes[l - 1]];

							sigmoid_layers[l - 1].sample_h_given_v(prev_layer_input, layer_input);
						}
					}

					rbm_layers[i].contrastive_divergence(layer_input, lr, k);
				}
				System.out.println(System.currentTimeMillis() - current + "ms");
			}
		}
	}
	
	public void pretrain(List<SVMVector> train_X, double lr, int k, int epochs) {
		int[] layer_input = new int[0];
		int prev_layer_input_size;
		int[] prev_layer_input;

		for (int i = 0; i < n_layers; i++) { // layer-wise
			for (int epoch = 0; epoch < epochs; epoch++) { // training epochs
				System.out.println("Pretrain epoch " + epoch);
				long current = System.currentTimeMillis();
				for (int n = 0; n < N; n++) { // input x1...xN
					// layer input
					for (int l = 0; l <= i; l++) {

						if (l == 0) {
							layer_input = new int[n_ins];
							for (int j = 0; j < n_ins; j++)								
								layer_input[j] = train_X.get(n).getFeaturevalues().get(j + 1);
						} else {
							if (l == 1)
								prev_layer_input_size = n_ins;
							else
								prev_layer_input_size = hidden_layer_sizes[l - 2];

							prev_layer_input = new int[prev_layer_input_size];
							for (int j = 0; j < prev_layer_input_size; j++)
								prev_layer_input[j] = layer_input[j];

							layer_input = new int[hidden_layer_sizes[l - 1]];

							sigmoid_layers[l - 1].sample_h_given_v(prev_layer_input, layer_input);
						}
					}

					rbm_layers[i].contrastive_divergence(layer_input, lr, k);
				}
				System.out.println(System.currentTimeMillis() - current + "ms");
			}
		}
	}

	public void finetune(DoubleMatrix2D train_X, DoubleMatrix2D train_Y, double lr, int epochs) {
		int[] layer_input = new int[0];
		// int prev_layer_input_size;
		int[] prev_layer_input = new int[0];

		for (int epoch = 0; epoch < epochs; epoch++) {
			System.out.println("Finetune epoch " + epoch);
			for (int n = 0; n < N; n++) {

				// layer input
				for (int i = 0; i < n_layers; i++) {
					if (i == 0) {
						prev_layer_input = new int[n_ins];
						for (int j = 0; j < n_ins; j++)
							prev_layer_input[j] = (int) train_X.get(n, j);
					} else {
						prev_layer_input = new int[hidden_layer_sizes[i - 1]];
						for (int j = 0; j < hidden_layer_sizes[i - 1]; j++)
							prev_layer_input[j] = layer_input[j];
					}

					layer_input = new int[hidden_layer_sizes[i]];
					sigmoid_layers[i].sample_h_given_v(prev_layer_input, layer_input);
				}

				log_layer.train(layer_input, train_Y.viewRow(n), lr);
			}
			// lr *= 0.95;
		}
	}
	
	public void finetune(List<SVMVector> train_X, DoubleMatrix2D train_Y, double lr, int epochs) {
		int[] layer_input = new int[0];
		// int prev_layer_input_size;
		int[] prev_layer_input = new int[0];

		for (int epoch = 0; epoch < epochs; epoch++) {
			System.out.println("Finetune epoch " + epoch);
			for (int n = 0; n < N; n++) {

				// layer input
				for (int i = 0; i < n_layers; i++) {
					if (i == 0) {
						prev_layer_input = new int[n_ins];
						for (int j = 0; j < n_ins; j++)
							prev_layer_input[j] = train_X.get(n).getFeaturevalues().get(j + 1);
					} else {
						prev_layer_input = new int[hidden_layer_sizes[i - 1]];
						for (int j = 0; j < hidden_layer_sizes[i - 1]; j++)
							prev_layer_input[j] = layer_input[j];
					}

					layer_input = new int[hidden_layer_sizes[i]];
					sigmoid_layers[i].sample_h_given_v(prev_layer_input, layer_input);
				}

				log_layer.train(layer_input, train_Y.viewRow(n), lr);
			}
			// lr *= 0.95;
		}
	}

	public void predict(DoubleMatrix1D x, double[] y) {
		double[] layer_input = new double[0];
		// int prev_layer_input_size;
		double[] prev_layer_input = new double[n_ins];
		for (int j = 0; j < n_ins; j++) {
			//System.out.println(j + ":" + (int)x.get(j));
			prev_layer_input[j] = x.get(j);
		}			

		double linear_output;

		// layer activation
		for (int i = 0; i < n_layers; i++) {
			layer_input = new double[sigmoid_layers[i].n_out];

			for (int k = 0; k < sigmoid_layers[i].n_out; k++) {
				linear_output = 0.0;

				for (int j = 0; j < sigmoid_layers[i].n_in; j++) {
					linear_output += sigmoid_layers[i].W[k][j] * prev_layer_input[j];
				}
				linear_output += sigmoid_layers[i].b[k];
				layer_input[k] = sigmoid(linear_output);
			}

			if (i < n_layers - 1) {
				prev_layer_input = new double[sigmoid_layers[i].n_out];
				for (int j = 0; j < sigmoid_layers[i].n_out; j++)
					prev_layer_input[j] = layer_input[j];
			}
		}

		for (int i = 0; i < log_layer.n_out; i++) {
			y[i] = 0;
			for (int j = 0; j < log_layer.n_in; j++) {
				y[i] += log_layer.W[i][j] * layer_input[j];
			}
			y[i] += log_layer.b[i];
		}

		log_layer.softmax(y);
	}
	
	public void predict(SVMVector x, double[] y) {
		double[] layer_input = new double[0];
		// int prev_layer_input_size;
		double[] prev_layer_input = new double[n_ins];
		for (int j = 0; j < n_ins; j++) {
			//System.out.println(j + ":" + x.getFeaturevalues().get(j + 1));
			prev_layer_input[j] = x.getFeaturevalues().get(j + 1);
		}			

		double linear_output;

		// layer activation
		for (int i = 0; i < n_layers; i++) {
			layer_input = new double[sigmoid_layers[i].n_out];

			for (int k = 0; k < sigmoid_layers[i].n_out; k++) {
				linear_output = 0.0;

				for (int j = 0; j < sigmoid_layers[i].n_in; j++) {
					linear_output += sigmoid_layers[i].W[k][j] * prev_layer_input[j];
				}
				linear_output += sigmoid_layers[i].b[k];
				layer_input[k] = sigmoid(linear_output);
			}

			if (i < n_layers - 1) {
				prev_layer_input = new double[sigmoid_layers[i].n_out];
				for (int j = 0; j < sigmoid_layers[i].n_out; j++)
					prev_layer_input[j] = layer_input[j];
			}
		}

		for (int i = 0; i < log_layer.n_out; i++) {
			y[i] = 0;
			for (int j = 0; j < log_layer.n_in; j++) {
				y[i] += log_layer.W[i][j] * layer_input[j];
			}
			y[i] += log_layer.b[i];
		}

		log_layer.softmax(y);
	}
	
	private static void test_dbn() {
		Random rng = new Random(123);

		double pretrain_lr = 0.1;
		int pretraining_epochs = 1000;
		int k = 1;
		double finetune_lr = 0.1;
		int finetune_epochs = 500;

		int train_N = 6;
		int test_N = 4;
		int n_ins = 6;
		int n_outs = 2;
		int[] hidden_layer_sizes = {10, 10};
		int n_layers = hidden_layer_sizes.length;

		// training data
		double[][] train_X = {
			{1, 1, 1, 0, 0, 0},
			{1, 0, 1, 0, 0, 0},
			{1, 1, 1, 0, 0, 0},
			{0, 0, 1, 1, 1, 0},
			{0, 0, 1, 1, 0, 0},
			{0, 0, 1, 1, 1, 0}
		};
		
		DoubleFactory2D factory2D = DoubleFactory2D.sparse;
		DoubleMatrix2D trainX = factory2D.make(train_X);

		double[][] train_Y = {
			{1, 0},
			{1, 0},
			{1, 0},
			{0, 1},
			{0, 1},
			{0, 1},
		};
		
		DoubleMatrix2D trainY = factory2D.make(train_Y);

		// construct DBN
		DBN dbn = new DBN(train_N, n_ins, hidden_layer_sizes, n_outs, n_layers, rng);

		// pretrain
		dbn.pretrain(trainX, pretrain_lr, k, pretraining_epochs);

		// finetune
		dbn.finetune(trainX, trainY, finetune_lr, finetune_epochs);


		// test data
		double[][] test_X = {
			{1, 1, 0, 0, 0, 0},
			{1, 1, 1, 1, 0, 0},
			{0, 0, 0, 1, 1, 0},
			{0, 0, 1, 1, 1, 0},
		};

		double[][] test_Y = new double[test_N][n_outs];
		DoubleMatrix2D testX = factory2D.make(test_X);
		// test
		for(int i=0; i<test_N; i++) {
			dbn.predict(testX.viewRow(i), test_Y[i]);
			for(int j=0; j<n_outs; j++) {
				System.out.print(test_Y[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		test_dbn();
	}
}