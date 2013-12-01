/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.triggerdetection.onelayer;

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

public class TDTrainer1L {
	static String corpusName = "CG";
	static String corpusType = "full";
	static String folderTraining = "corpus/" + corpusName + "2013/BioNLP-ST_2013_" + corpusName + "_" + corpusType + "_data";
	static String fileTraining = "model/" + corpusName + "2013/TriggerDetection.1L." + corpusType;
	static String fileModel = "model/" + corpusName + "2013/TriggerDetection." + corpusType + ".1L.model";
	static String fileWordlist = "model/" + corpusName + "2013/TriggerDetection." + corpusType + ".1L.wordlist";
	static String fileDictionary = "data/" + corpusName + "2013/TriggerDictionary_adv.txt";

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
		long current = System.currentTimeMillis();
		init();
		createVectorTrainingFile();
		new Train().main(new String[] { "-c", "1.0", "-s", "6", "-q", fileTraining, fileModel });
		System.out.println(System.currentTimeMillis() - current + "ms");
	}

	private static void createVectorTrainingFile() throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileTraining)));

		for (BIONLPDocument doc : documents) {
			System.out.println("Processing " + doc.getIdFile() + "................");
			for (BIONLPSentence sentence : doc.getSentences()) {
				for (BIONLPToken token : sentence.getTokens()) {
					if (token.getAnnotation() != null)
						if (token.getAnnotation().getDescription().equals("ENTITY"))
							continue;
					String label = (token.getAnnotation() == null) ? "NOT_TRIGGER" : token.getAnnotation().getType();
					String vector = featureSet.addprintSVMVector(contextGenerator.getContext(token, sentence), label, false);
					writer.append(vector).append("\n");
				}
			}
		}
		writer.close();

		FileHelper.writeObjectToFile(featureSet, new File(fileWordlist));
	}

}
