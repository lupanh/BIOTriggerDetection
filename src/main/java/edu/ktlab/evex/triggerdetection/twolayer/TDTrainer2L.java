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
import edu.ktlab.evex.util.classifier.FeatureGenerator;
import edu.ktlab.evex.util.classifier.FeatureSet;

public class TDTrainer2L {
	static String corpusName = "CG";
	static String corpusType = "training";
	static String folderTraining = "corpus/" + corpusName + "2013/BioNLP-ST_2013_" + corpusName + "_" + corpusType + "_data";
	static String fileTrainingL1 = "model/" + corpusName + "2013/TriggerDetection.2L.L1." + corpusType;
	static String fileTrainingL2 = "model/" + corpusName + "2013/TriggerDetection.2L.L2." + corpusType;
	static String fileModelL1 = "model/" + corpusName + "2013/TriggerDetection." + corpusType + ".2L.L1.model";
	static String fileModelL2 = "model/" + corpusName + "2013/TriggerDetection." + corpusType + ".2L.L2.model";
	static String fileWordlistL1 = "model/" + corpusName + "2013/TriggerDetection." + corpusType + ".2L.L1.wordlist";
	static String fileWordlistL2 = "model/" + corpusName + "2013/TriggerDetection." + corpusType + ".2L.L2.wordlist";
	static String fileDictionary = "data/" + corpusName + "2013/TriggerDictionary.txt";
	static ArrayList<BIONLPDocument> documents;

	@SuppressWarnings("unchecked")
	public static FeatureGenerator<BIONLPToken, BIONLPSentence>[] mFeatureGenerators = new FeatureGenerator[] {
			new TDTokenFeatureGenerator(fileDictionary), new TDTreeFeatureGenerator(), new TDShortestPathFeatureGenerator() };

	static TDContextGenerator contextGenerator;

	public static void init() throws Exception {
		contextGenerator = new TDContextGenerator(mFeatureGenerators);
		documents = StandoffCorporaLoading.loadCorpora(folderTraining);
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		init();
		createVectorTrainingFile();

		new Train().main(new String[] { "-c", "1.0", "-s", "6", fileTrainingL1, fileModelL1 });
		new Train().main(new String[] { "-c", "1.0", "-s", "6", fileTrainingL2, fileModelL2 });
	}

	private static void createVectorTrainingFile() throws Exception {
		FeatureSet featureSetL1 = new FeatureSet();
		FeatureSet featureSetL2 = new FeatureSet();
		BufferedWriter writerL1 = new BufferedWriter(new FileWriter(new File(fileTrainingL1)));
		BufferedWriter writerL2 = new BufferedWriter(new FileWriter(new File(fileTrainingL2)));

		for (BIONLPDocument doc : documents) {
			System.out.println("Processing " + doc.getIdFile() + "................");
			for (BIONLPSentence sentence : doc.getSentences()) {
				for (BIONLPToken token : sentence.getTokens()) {
					if (token.getAnnotation() != null)
						if (token.getAnnotation().getDescription().equals("ENTITY"))
							continue;
					String label = (token.getAnnotation() == null) ? "NOT_TRIGGER" : token.getAnnotation().getType();
					String labelL1 = "";
					String labelL2 = "";
					if (label.contains("NOT_TRIGGER")) {
						labelL1 = "NOT_TRIGGER";
						labelL2 = "NOT_TRIGGER";
					} else {
						labelL1 = "TRIGGER";
						labelL2 = label;
					}
					ArrayList<String> features = contextGenerator.getContext(token, sentence);

					String vectorL1 = featureSetL1.addprintSVMVector(features, labelL1, false);
					writerL1.append(vectorL1).append("\n");

					if (!labelL2.contains("NOT_TRIGGER")) {
						String vectorL2 = featureSetL1.addprintSVMVector(features, labelL2, false);
						writerL1.append(vectorL2).append("\n");
					}
				}
			}

		}
		writerL1.close();
		writerL2.close();

		FileHelper.writeObjectToFile(featureSetL1, new File(fileWordlistL1));
		FileHelper.writeObjectToFile(featureSetL2, new File(fileWordlistL2));
	}

}
