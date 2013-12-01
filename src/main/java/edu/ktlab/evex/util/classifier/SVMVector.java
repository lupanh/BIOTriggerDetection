package edu.ktlab.evex.util.classifier;

import gnu.trove.map.hash.TIntByteHashMap;
import gnu.trove.map.hash.TIntIntHashMap;

public class SVMVector {
	int label = -9999;
	TIntIntHashMap featurevalues = new TIntIntHashMap();

	public SVMVector() {
	}

	public int addFeatureValue(int featureId, int value) {
		return featurevalues.put(featureId, value);
	}

	public int addFeatureValue(String featureId, String value) {
		return featurevalues.put(Integer.parseInt(featureId), Integer.parseInt(value));
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public void setLabel(String label) {
		this.label = Integer.parseInt(label);
	}

	public TIntIntHashMap getFeaturevalues() {
		return featurevalues;
	}

	public int getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return "SVMVector [label=" + label + ", featurevalues=" + featurevalues + "]";
	}	
}
