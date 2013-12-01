package edu.ktlab.evex.util.classifier;

import java.util.ArrayList;

public interface Classifier {
	public String predict(ArrayList<String> features, String label);
}
