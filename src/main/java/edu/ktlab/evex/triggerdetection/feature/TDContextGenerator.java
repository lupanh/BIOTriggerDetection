/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.triggerdetection.feature;

import java.util.ArrayList;

import edu.ktlab.evex.corpora.bean.BIONLPSentence;
import edu.ktlab.evex.corpora.bean.BIONLPToken;
import edu.ktlab.evex.util.classifier.FeatureGenerator;


public class TDContextGenerator {
	private FeatureGenerator<BIONLPToken, BIONLPSentence>[] mFeatureGenerators;

	public TDContextGenerator(FeatureGenerator<BIONLPToken, BIONLPSentence>[] mFeatureGenerators) {		
		this.mFeatureGenerators = mFeatureGenerators;
	}
	
	public ArrayList<String> getContext(BIONLPToken token, BIONLPSentence sentence) {
		ArrayList<String> context = new ArrayList<String>();
		for (FeatureGenerator<BIONLPToken, BIONLPSentence> generator : mFeatureGenerators) {
			ArrayList<String> extractedFeatures = generator.extractFeatures(token, sentence);
			context.addAll(extractedFeatures);
		}
		return context;
	}
}
