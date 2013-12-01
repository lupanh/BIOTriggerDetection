/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.triggerdetection.feature;

import java.util.ArrayList;
import java.util.List;

import edu.ktlab.evex.corpora.bean.BIONLPSentence;
import edu.ktlab.evex.corpora.bean.BIONLPTerm;
import edu.ktlab.evex.corpora.bean.BIONLPToken;
import edu.ktlab.evex.util.Ngram;
import edu.ktlab.evex.util.classifier.FeatureGenerator;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class TDShortestPathFeatureGenerator implements FeatureGenerator<BIONLPToken, BIONLPSentence> {
	public ArrayList<String> extractFeatures(BIONLPToken candidate, BIONLPSentence sentence) {
		ArrayList<String> featureCollector = new ArrayList<String>();

		try {
			SemanticGraph semgraph = sentence.getSemanticGraph();
			IndexedWord candidateNode = semgraph.getNodeByIndex(candidate.getIdToken());
			// Get shortest path from this token to other entities
			for (BIONLPToken token : sentence.getTokens()) {
				if (token.getAnnotation() != null) {
					BIONLPTerm annotation = token.getAnnotation();
					if (annotation.getDescription().equals("ENTITY")) {
						IndexedWord entityNode = semgraph.getNodeByIndex(token.getIdToken());
						ArrayList<String> shortestfeatures = getShortestFeatures(candidateNode, entityNode, semgraph, annotation.getType());
						if (shortestfeatures != null)
							//
							featureCollector.addAll(shortestfeatures);
					}
				}
			}
		} catch (Exception e) {
		}

		return featureCollector;
	}

	public ArrayList<String> getShortestFeatures(IndexedWord word, IndexedWord entity, SemanticGraph semgraph, String entityType) {
		ArrayList<String> featureCollector = new ArrayList<String>();

		List<SemanticGraphEdge> shortestPathEdges = semgraph.getShortestUndirectedPathEdges(word, entity);
		// featureCollector.add("SPE:LEN:" + entityType + ":" +
		// shortestPathEdges.size());

		//
		featureCollector.add("SPE:LEN:" + shortestPathEdges.size());

		if (shortestPathEdges.size() == 0)
			return featureCollector;

		ArrayList<String> fullSPSequence = new ArrayList<String>();
		ArrayList<String> wordSPSequence = new ArrayList<String>();
		ArrayList<String> wordposSPSequence = new ArrayList<String>();
		ArrayList<String> dependencySPSequence = new ArrayList<String>();

		fullSPSequence.add(Morphology.lemmaStatic(word.word(), word.tag(), true));
		wordSPSequence.add(Morphology.lemmaStatic(word.word(), word.tag(), true));
		wordposSPSequence.add(Morphology.lemmaStatic(word.word(), word.tag(), true) + "-" + word.tag().toUpperCase());

		for (SemanticGraphEdge edge : shortestPathEdges) {
			fullSPSequence.add(edge.getRelation().getShortName().toUpperCase());
			fullSPSequence.add(Morphology.lemmaStatic(edge.getTarget().word(), edge.getTarget().tag(), true));

			wordSPSequence.add(Morphology.lemmaStatic(edge.getTarget().word(), edge.getTarget().tag(), true));
			wordposSPSequence.add(Morphology.lemmaStatic(edge.getTarget().word(), edge.getTarget().tag(), true) + "-"
					+ edge.getTarget().tag().toUpperCase());

			dependencySPSequence.add(edge.getRelation().getShortName().toUpperCase());
		}

		//featureCollector.addAll(Ngram.ngramWord(2, 2, fullSPSequence.toArray(new String[fullSPSequence.size()]), "_", "SPE:F:" + entityType
		//		+ ":"));
		//featureCollector.addAll(Ngram.ngramWord(2, 2, wordSPSequence.toArray(new String[wordSPSequence.size()]), "_", "SPE:W:" + entityType
		//		+ ":"));
		//featureCollector.addAll(Ngram.ngramWord(2, 2, wordposSPSequence.toArray(new String[wordposSPSequence.size()]), "_", "SPE:WP:"
		//		+ entityType + ":"));
		//featureCollector.addAll(Ngram.gramWord(2, dependencySPSequence.toArray(new String[dependencySPSequence.size()]), "_", "SPE:D:"
		//		+ entityType + ":"));

		//
		featureCollector.addAll(Ngram.ngramWord(2, 2, fullSPSequence.toArray(new String[fullSPSequence.size()]), "_", "SPE:F:"));
		featureCollector.addAll(Ngram.ngramWord(2, 2, wordSPSequence.toArray(new String[wordSPSequence.size()]), "_", "SPE:W:"));
		featureCollector.addAll(Ngram.ngramWord(2, 2, wordposSPSequence.toArray(new String[wordposSPSequence.size()]), "_", "SPE:WP:"));
		featureCollector.addAll(Ngram.gramWord(2, dependencySPSequence.toArray(new String[dependencySPSequence.size()]), "_", "SPE:D:"));

		return featureCollector;
	}
}
