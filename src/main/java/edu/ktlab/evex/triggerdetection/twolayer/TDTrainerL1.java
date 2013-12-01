/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.triggerdetection.twolayer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import de.bwaldvogel.liblinear.Train;
import edu.ktlab.evex.corpora.bean.BIONLPDocument;
import edu.ktlab.evex.corpora.bean.BIONLPSentence;
import edu.ktlab.evex.corpora.bean.BIONLPToken;
import edu.ktlab.evex.corpora.processing.StandoffCorporaLoading;
import edu.ktlab.evex.triggerdetection.feature.TDContextGenerator;
import edu.ktlab.evex.triggerdetection.feature.TDShortestPathFeatureGenerator;
import edu.ktlab.evex.triggerdetection.feature.TDTokenFeatureGenerator;
import edu.ktlab.evex.triggerdetection.feature.TDTreeFeatureGenerator;
import edu.ktlab.evex.util.FileHelper;
import edu.ktlab.evex.util.RandomIntSelection;
import edu.ktlab.evex.util.classifier.FeatureGenerator;
import edu.ktlab.evex.util.classifier.FeatureSet;

public class TDTrainerL1 {
	static String corpusName = "CG";
	static String corpusType = "training";
	static String folderTraining = "corpus/" + corpusName + "2013/BioNLP-ST_2013_" + corpusName + "_" + corpusType + "_data";
	static String fileTraining = "model/" + corpusName + "2013/TriggerDetection.2L.L2." + corpusType;
	static String fileModel = "model/" + corpusName + "2013/TriggerDetection." + corpusType + ".2L.L1.model";
	static String fileWordlist = "model/" + corpusName + "2013/TriggerDetection." + corpusType + ".2L.L1.wordlist";
	static String fileDictionary = "data/" + corpusName + "2013/TriggerDictionary.txt";
	static double sample_ratio = 2.5; // Neg/Pos = 1.2;

	@SuppressWarnings("unchecked")
	public static FeatureGenerator<BIONLPToken, BIONLPSentence>[] mFeatureGenerators = new FeatureGenerator[] {
			new TDTokenFeatureGenerator(fileDictionary), new TDTreeFeatureGenerator(), new TDShortestPathFeatureGenerator() };

	static TDContextGenerator contextGenerator;
	static FeatureSet featureSet;
	static ArrayList<BIONLPDocument> documents;

	public static void init() throws Exception {
		contextGenerator = new TDContextGenerator(mFeatureGenerators);
		featureSet = new FeatureSet();
		documents = StandoffCorporaLoading.loadCorpora(folderTraining);
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		init();
		createVectorTrainingFile(sample_ratio);
		new Train().main(new String[] { "-c", "1.0", "-s", "6", fileTraining, fileModel });
	}

	private static void createVectorTrainingFile(double sample_ratio) throws Exception {
		FeatureSet featureSet = new FeatureSet();
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileTraining)));

		ArrayList<BIONLPDocument> documents = StandoffCorporaLoading.loadCorpora(folderTraining);
		int posCount = 0;
		ArrayList<String> negVectors = new ArrayList<String>();

		for (BIONLPDocument doc : documents) {
			System.out.println("Processing " + doc.getIdFile() + "................");
			for (BIONLPSentence sentence : doc.getSentences()) {
				for (BIONLPToken token : sentence.getTokens()) {
					if (token.getAnnotation() != null)
						if (token.getAnnotation().getDescription().equals("ENTITY"))
							continue;
					String label = (token.getAnnotation() == null) ? "NOT_TRIGGER" : "TRIGGER";

					ArrayList<String> features = contextGenerator.getContext(token, sentence);
					String vector = featureSet.addprintSVMVector(features, label, false);

					if (label.equals("TRIGGER")) {
						writer.append(vector).append("\n");
						posCount++;
					} else
						negVectors.add(vector);
				}
			}
		}

		if (sample_ratio != -1) {
			int negCount = (int) (posCount * sample_ratio);
			RandomIntSelection random = new RandomIntSelection(0, negVectors.size() - 1);
			for (int i : random.nextInt(negCount)) {
				String vector = negVectors.get(i);
				writer.append(vector).append("\n");
			}
		} else {
			for (String vector : negVectors)
				writer.append(vector).append("\n");
		}
		writer.close();

		FileHelper.writeObjectToFile(featureSet, new File(fileWordlist));
	}

}
