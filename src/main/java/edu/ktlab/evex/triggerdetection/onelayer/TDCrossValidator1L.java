/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.triggerdetection.onelayer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
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

public class TDCrossValidator1L {
	static String corpusName = "CG";
	static String corpusType = "full";
	static String folderFulldata = "corpus/" + corpusName + "2013/BioNLP-ST_2013_" + corpusName + "_" + corpusType + "_data";
	static String fileTraining = "model/" + corpusName + "2013/TriggerDetection.1L.eval";
	static String fileModel = "model/" + corpusName + "2013/TriggerDetection.1L.eval.model";
	static String fileWordlist = "model/" + corpusName + "2013/TriggerDetection.1L.eval.wordlist";
	static String fileDictionary = "data/" + corpusName + "2013/TriggerDictionary.txt";

	@SuppressWarnings("unchecked")
	public static FeatureGenerator<BIONLPToken, BIONLPSentence>[] mFeatureGenerators = new FeatureGenerator[] {
			new TDTokenFeatureGenerator(fileDictionary), new TDTreeFeatureGenerator(), new TDShortestPathFeatureGenerator() };

	static TDContextGenerator contextGenerator;
	static FeatureSet featureSet;

	public static void init() {
		contextGenerator = new TDContextGenerator(mFeatureGenerators);
		featureSet = new FeatureSet();
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		init();
		createVectorTrainingFile();
		PrintStream print = new PrintStream(new File("outputTD_mccc.txt"));
		System.setOut(print);

		for (int i = 0; i <= 7; i++) {
			System.out.println("=> Evaluation method " + i + " with c=0.5");
			new Train().main(new String[] { "-v", "5", "-c", "0.5", "-s", "" + i, fileTraining });
			System.out.println("=> Evaluation method " + i + " with c=1");
			new Train().main(new String[] { "-v", "5", "-c", "1", "-s", "" + i, fileTraining });
		}
	}

	private static void createVectorTrainingFile() throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileTraining)));

		ArrayList<BIONLPDocument> documents = StandoffCorporaLoading.loadCorpora(folderFulldata);
		for (BIONLPDocument doc : documents) {
			System.out.println("Processing " + doc.getIdFile() + "................");
			for (BIONLPSentence sentence : doc.getSentences()) {
				for (BIONLPToken token : sentence.getTokens()) {
					// if (token.getAnnotation() == null)
					// continue;
					if (token.getAnnotation() != null)
						if (token.getAnnotation().getDescription().equals("ENTITY"))
							continue;
					String label = (token.getAnnotation() == null) ? "NOT_TRIGGER" : token.getAnnotation().getType();
					String vector = featureSet.addprintSVMVector(contextGenerator.getContext(token, sentence), label, false);
					if (vector.equals(""))
						continue;
					writer.append(vector).append("\n");
				}
			}
		}
		writer.close();

		FileHelper.writeObjectToFile(featureSet, new File(fileWordlist));
	}

}
