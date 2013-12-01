/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.util.classifier;

import java.util.ArrayList;

public interface FeatureGenerator<T1, T2> {
	public ArrayList<String> extractFeatures(T1 candidate, T2 context);
}
