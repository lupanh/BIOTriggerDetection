/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.triggerdetection.feature;

import java.util.ArrayList;
import java.util.Set;

import edu.ktlab.evex.corpora.bean.BIONLPSentence;
import edu.ktlab.evex.corpora.bean.BIONLPToken;
import edu.ktlab.evex.util.Ngram;
import edu.ktlab.evex.util.WordUtil;
import edu.ktlab.evex.util.classifier.FeatureGenerator;

import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;

public class TDTokenFeatureGenerator implements FeatureGenerator<BIONLPToken, BIONLPSentence> {
	TDDictionary dictionary;

	public TDTokenFeatureGenerator(String fileDictionary) {
		dictionary = new TDDictionary(fileDictionary, true);
	}

	public ArrayList<String> extractFeatures(BIONLPToken token, BIONLPSentence sentence) {
		ArrayList<String> featureCollector = new ArrayList<String>();
		String word = token.getTextToken();

		String tf = WordUtil.wordFeatures(word);
		if (tf != null) {
			//
			featureCollector.add("TC:" + tf);
			if (!tf.equals("NUM") && !tf.equals("PUNCT")) {
				featureCollector.addAll(getCurrentWordFeatures(token, sentence));
			}
		} else {
			featureCollector.addAll(getCurrentWordFeatures(token, sentence));
		}

		return featureCollector;
	}

	public ArrayList<String> getCurrentWordFeatures(BIONLPToken token, BIONLPSentence sentence) {
		ArrayList<String> featureCollector = new ArrayList<String>();
		SemanticGraph semgraph = sentence.getSemanticGraph();
		String word = token.getTextToken().toLowerCase();
		String stemText = WordUtil.stemmerFeature(word);
		//
		featureCollector.add("W:" + word);
		Set<String> labels = dictionary.lookup(word);
		if (labels != null) {
			for (String label : labels)
				//
				featureCollector.add("DIC:" + label);
		}
		//
		featureCollector.add("STEM:" + stemText);
		featureCollector.addAll(Ngram.ngramCharacter(2, 4, word, "", "CNG:"));
		featureCollector.addAll(getNGramWindowFeatures(token, sentence, 4, 3));
		// Get POS of this token
		try {
			String postag = semgraph.getNodeByIndex(token.getIdToken()).tag();
			String lemma = Morphology.lemmaStatic(word, postag, true);
			//
			featureCollector.add("POS:" + postag);
			if (!lemma.equals(word)) {
				labels = dictionary.lookup(lemma);
				if (labels != null) {
					for (String label : labels)
						//
						featureCollector.add("DIC:" + label);
				}
				//
				featureCollector.add("BASE:" + lemma);
			}
		} catch (Exception e) {
		}

		return featureCollector;
	}

	public ArrayList<String> getNGramWindowFeatures(BIONLPToken token, BIONLPSentence sentence, int size, int ngram) {
		ArrayList<String> featureCollector = new ArrayList<String>();

		ArrayList<String> window = new ArrayList<String>();
		int indexToken = token.getIdToken();
		int leftIndex = indexToken - size;
		if (leftIndex < 1)
			leftIndex = 1;
		int rightIndex = indexToken + size;
		if (rightIndex > sentence.getTokens().size())
			rightIndex = sentence.getTokens().size();
		for (int i = leftIndex; i <= rightIndex; i++)
			window.add(sentence.getTokens().get(i - 1).getTextToken().toLowerCase());
		//
		featureCollector.addAll(Ngram.ngramWord(ngram, window.toArray(new String[window.size()]), "_", "WNG:"));

		return featureCollector;
	}
}
