/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.triggerdetection.feature;

import java.util.ArrayList;
import java.util.List;

import edu.ktlab.evex.corpora.bean.BIONLPPair;
import edu.ktlab.evex.corpora.bean.BIONLPSentence;
import edu.ktlab.evex.corpora.bean.BIONLPToken;
import edu.ktlab.evex.util.Ngram;
import edu.ktlab.evex.util.classifier.FeatureGenerator;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class TDTreeFeatureGenerator implements FeatureGenerator<BIONLPToken, BIONLPSentence> {

	public ArrayList<String> extractFeatures(BIONLPToken candidate, BIONLPSentence sentence) {
		ArrayList<String> featureCollector = new ArrayList<String>();
		try {
			SemanticGraph semgraph = sentence.getSemanticGraph();
			IndexedWord candidateNode = semgraph.getNodeByIndex(candidate.getIdToken());

			// Get neighbouring features
			ArrayList<String> neighfeatures = getNeighbouringFeatures(candidateNode, semgraph);
			if (neighfeatures != null)
				//
				featureCollector.addAll(neighfeatures);
		} catch (Exception e) {
		}

		return featureCollector;
	}

	ArrayList<String> getNeighbouringFeatures(IndexedWord word, SemanticGraph semgraph) {
		ArrayList<String> featureCollector = new ArrayList<String>();

		List<SemanticGraphEdge> layer1 = semgraph.getOutEdgesSorted(word);
		ArrayList<BIONLPPair<SemanticGraphEdge, SemanticGraphEdge>> twoStepPaths = new ArrayList<BIONLPPair<SemanticGraphEdge, SemanticGraphEdge>>();
		if (layer1 != null)
			for (SemanticGraphEdge edge1 : layer1) {
				List<SemanticGraphEdge> layer2 = semgraph.getOutEdgesSorted(edge1.getSource());
				if (layer2 != null) {
					for (SemanticGraphEdge edge2 : layer2) {
						BIONLPPair<SemanticGraphEdge, SemanticGraphEdge> twoStepPath = new BIONLPPair<SemanticGraphEdge, SemanticGraphEdge>();
						twoStepPath.setFirst(edge1);
						twoStepPath.setSecond(edge2);
						twoStepPaths.add(twoStepPath);
					}
				} else {
					String step0 = Morphology.lemmaStatic(edge1.getSource().word(), edge1
							.getSource().tag(), true);
					String step1 = Morphology.lemmaStatic(edge1.getTarget().word(), edge1
							.getTarget().tag(), true);
					//
					featureCollector.add("1DP:F:" + step0 + "_"
							+ edge1.getRelation().getShortName().toUpperCase() + "_" + step1);
					featureCollector.add("1DP:W:" + step0 + "_" + step1);
				}
			}

		if (twoStepPaths.size() == 0)
			return null;
		else {
			for (BIONLPPair<SemanticGraphEdge, SemanticGraphEdge> twoStepPath : twoStepPaths) {
				String[] twoStepFullSequence = new String[5];
				String[] twoStepWordSequence = new String[3];
				String[] twoStepDependencySequence = new String[2];

				String step0 = Morphology.lemmaStatic(twoStepPath.first().getSource().word(),
						twoStepPath.first().getSource().tag(), true);
				String step1 = Morphology.lemmaStatic(twoStepPath.first().getTarget().word(),
						twoStepPath.first().getTarget().tag(), true);
				String step2 = Morphology.lemmaStatic(twoStepPath.second().getTarget().word(),
						twoStepPath.second().getTarget().tag(), true);

				twoStepFullSequence[0] = step0;
				twoStepFullSequence[1] = twoStepPath.first().getRelation().getShortName()
						.toUpperCase();
				twoStepFullSequence[2] = step1;
				twoStepFullSequence[3] = twoStepPath.second().getRelation().getShortName()
						.toUpperCase();
				twoStepFullSequence[4] = step2;

				twoStepWordSequence[0] = step0;
				twoStepWordSequence[1] = step1;
				twoStepWordSequence[2] = step2;

				twoStepDependencySequence[0] = twoStepPath.first().getRelation().getShortName()
						.toUpperCase();
				twoStepDependencySequence[1] = twoStepPath.second().getRelation().getShortName()
						.toUpperCase();

				//
				featureCollector.addAll(Ngram.ngramWord(2, 3, twoStepFullSequence, "_", "2DP:F:"));
				featureCollector.addAll(Ngram.ngramWord(2, 2, twoStepWordSequence, "_", "2DP:W:"));
				featureCollector
						.addAll(Ngram.gramWord(2, twoStepDependencySequence, "_", "2DP:D:"));
			}
		}

		return featureCollector;
	}

}
