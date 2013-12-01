package edu.ktlab.evex.util;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.text.DecimalFormat;
import java.util.Set;

public class Fscore {
	static DecimalFormat df = new DecimalFormat("#.####");

	public static void printF1(Set<String> labels, TObjectIntHashMap<String> statistical) {
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
			System.out.print("Precision:" + tp + "/" + np + "=" + df.format(precision) + "\t");
			System.out.print("Recall:" + tp + "/" + nl + "=" + df.format(recall) + "\t");
			System.out.print("F1:" + df.format((double) 2 * precision * recall / (precision + recall)) + "\t");
			System.out.println();
		}

		double allPrecision = (allNP == 0) ? 0 : (double) allTP / allNP;
		double allRecall = (allNL == 0) ? 0 : (double) allTP / allNL;
		System.out.print("ALL\t");
		System.out.print("Precision:" + allTP + "/" + allNP + "=" + df.format(allPrecision) + "\t");
		System.out.print("Recall:" + allTP + "/" + allNL + "=" + df.format(allRecall) + "\t");
		System.out.print("F1:" + df.format((double) 2 * allPrecision * allRecall / (allPrecision + allRecall)) + "\t");
		System.out.println();
	}

	public static void printF1(Set<String> labels, TObjectIntHashMap<String> statistical, String labelNegative) {
		int allTP = 0;
		int allNP = 0;
		int allNL = 0;
		for (String label : labels) {
			if (label.equals(labelNegative))
				continue;
			int tp = statistical.get("TP_" + label);
			int np = statistical.get("NP_" + label);
			int nl = statistical.get("NL_" + label);
			allTP += tp;
			allNP += np;
			allNL += nl;
			double precision = (np == 0) ? 0 : (double) tp / np;
			double recall = (nl == 0) ? 0 : (double) tp / nl;
			System.out.print(label + "\t");
			System.out.print("Precision:" + tp + "/" + np + "=" + df.format(precision) + "\t");
			System.out.print("Recall:" + tp + "/" + nl + "=" + df.format(recall) + "\t");
			System.out.print("F1:" + df.format((double) 2 * precision * recall / (precision + recall)) + "\t");
			System.out.println();
		}

		double allPrecision = (allNP == 0) ? 0 : (double) allTP / allNP;
		double allRecall = (allNL == 0) ? 0 : (double) allTP / allNL;
		System.out.print("ALL\t");
		System.out.print("Precision:" + allTP + "/" + allNP + "=" + df.format(allPrecision) + "\t");
		System.out.print("Recall:" + allTP + "/" + allNL + "=" + df.format(allRecall) + "\t");
		System.out.print("F1:" + df.format((double) 2 * allPrecision * allRecall / (allPrecision + allRecall)) + "\t");
		System.out.println();
	}
}
