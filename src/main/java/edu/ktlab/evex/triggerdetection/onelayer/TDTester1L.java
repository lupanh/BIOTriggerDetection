/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.triggerdetection.onelayer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import de.bwaldvogel.liblinear.Predict;
import edu.ktlab.evex.corpora.bean.BIONLPDocument;
import edu.ktlab.evex.corpora.bean.BIONLPSentence;
import edu.ktlab.evex.corpora.bean.BIONLPToken;
import edu.ktlab.evex.corpora.processing.StandoffCorporaLoading;
import edu.ktlab.evex.triggerdetection.feature.TDContextGenerator;
import edu.ktlab.evex.util.FileHelper;
import edu.ktlab.evex.util.classifier.FeatureSet;

public class TDTester1L {
	static String corpusName = "GE";
	static String corpusType = "development";
	static String folderTesting = "corpus/" + corpusName + "2013/BioNLP-ST_2013_" + corpusName + "_" + corpusType + "_data";
	static String fileTesting = "model/" + corpusName + "2013/TriggerDetection.1L." + corpusType;
	static String fileModel = "model/" + corpusName + "2013/TriggerDetection.1L.model";
	static String fileWordlist = "model/" + corpusName + "2013/TriggerDetection.1L.wordlist";

	static TDContextGenerator contextGenerator;

	public static void init() {
		contextGenerator = new TDContextGenerator(TDTrainer1L.mFeatureGenerators);
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		createTestVectorFile();
		new Predict().main(new String[] { fileTesting, fileModel, "TDdev.output" });
	}

	public static void createTestVectorFile() throws Exception {
		init();
		FeatureSet featureSet = (FeatureSet) FileHelper.readObjectFromFile(new File(fileWordlist));

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileTesting)));

		ArrayList<BIONLPDocument> documents = StandoffCorporaLoading.loadCorpora(folderTesting);
		for (BIONLPDocument doc : documents) {
			System.out.println("Processing " + doc.getIdFile() + "................");
			for (BIONLPSentence sentence : doc.getSentences()) {
				for (BIONLPToken token : sentence.getTokens()) {
					// if (token.getAnnotation() == null)
					// continue;
					if (token.getAnnotation() != null)
						if (token.getAnnotation().getDescription().equals("ENTITY"))
							continue;
					String vector = featureSet.addprintSVMVector(contextGenerator.getContext(token, sentence),
							((token.getAnnotation() == null) ? "NOT_TRIGGER" : token.getAnnotation().getType()), true);
					if (vector.equals(""))
						continue;
					writer.append(vector).append("\n");
				}
			}
		}
		writer.close();
	}
}
