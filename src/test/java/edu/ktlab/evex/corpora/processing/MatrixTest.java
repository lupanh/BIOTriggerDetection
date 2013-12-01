package edu.ktlab.evex.corpora.processing;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class MatrixTest {

	public static void main(String[] args) {
		DoubleMatrix2D matrix = new SparseDoubleMatrix2D(3000,700000);
		DoubleMatrix1D vector = matrix.viewRow(1);
		System.out.println(vector);
	}

}
