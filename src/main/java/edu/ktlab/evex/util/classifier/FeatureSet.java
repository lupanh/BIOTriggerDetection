/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.util.classifier;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

public class FeatureSet implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	TObjectIntHashMap<String> wordlist;
	ArrayList<String> labels;

	int labelKey = 0;

	public FeatureSet() {
		wordlist = new TObjectIntHashMap<String>();
		wordlist.put("NO_USE", 0);
		labels = new ArrayList<String>();
	}

	public TObjectIntHashMap<String> getWordlist() {
		return wordlist;
	}

	public void setWordlist(TObjectIntHashMap<String> wordlist) {
		this.wordlist = wordlist;
	}

	public ArrayList<String> getLabels() {
		return labels;
	}

	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}

	public int getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(int labelKey) {
		this.labelKey = labelKey;
	}

	public void addFeatureLabel(String[] strFeatures, String label, boolean flagTest) {
		HashSet<String> setFeatures = new HashSet<String>();
		for (String feature : strFeatures)
			setFeatures.add(feature);
		if (!label.equals(""))
			if (!labels.contains(label))
				if (!flagTest) {
					labels.add(label);
				}
	}

	public TreeMap<Integer, Integer> addStringFeatureVector(String[] strFeatures, String label, boolean flagTest) {
		HashSet<String> setFeatures = new HashSet<String>();
		TreeMap<Integer, Integer> vector = new TreeMap<Integer, Integer>();

		for (String feature : strFeatures)
			setFeatures.add(feature);
		if (setFeatures.size() == 0)
			return null;

		if (!label.equals("") || !flagTest)
			if (labels.contains(label))
				vector.put(labelKey, labels.indexOf(label));
			else {
				labels.add(label);
				vector.put(labelKey, labels.indexOf(label));
			}

		for (String feature : setFeatures) {
			if (wordlist.contains(feature)) {
				vector.put(wordlist.get(feature), 1);
			} else {
				if (!flagTest) {
					wordlist.put(feature, wordlist.size());
					vector.put(wordlist.get(feature), 1);
				}
			}
		}

		return vector;
	}

	public TreeMap<Integer, Integer> addStringFeatureVector(ArrayList<String> strFeatures, String label, boolean flagTest) {
		if (strFeatures == null)
			return null;
		return addStringFeatureVector(strFeatures.toArray(new String[strFeatures.size()]), label, flagTest);
	}

	public TreeMap<Integer, Integer> addStringFeatureVector(String strFeatures, String label, boolean flagTest) {
		return addStringFeatureVector(strFeatures.split(" "), label, flagTest);
	}

	public String addprintSVMVector(ArrayList<String> strFeatures, String label, boolean flagTest) {
		TreeMap<Integer, Integer> vector = addStringFeatureVector(strFeatures, label, flagTest);
		if (vector == null)
			return "";

		String text = "" + vector.get(labelKey);
		for (int key : vector.keySet()) {
			if (key == labelKey)
				continue;
			text += " " + key + ":" + vector.get(key);
		}
		return text;
	}

	public static void main(String[] args) {
		String strVector1 = "shuttle shuttl 2G:sh 2G:hu 2G:ut";
		String strVector2 = "abt shuttl anf 2G:hu 2G:ut";

		FeatureSet featureSet = new FeatureSet();
		TreeMap<Integer, Integer> vector1 = featureSet.addStringFeatureVector(strVector1, "ABC", false);
		TreeMap<Integer, Integer> vector2 = featureSet.addStringFeatureVector(strVector2, "XYZ", true);
		System.out.println(featureSet.getWordlist());
		System.out.println(vector1);
		System.out.println(vector2);
		System.out.println(featureSet.getLabels());
	}

}
