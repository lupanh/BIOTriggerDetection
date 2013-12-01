package edu.ktlab.evex.corpora.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import edu.ktlab.evex.corpora.bean.BIONLPAnnotation;
import edu.ktlab.evex.corpora.bean.BIONLPTerm;

public class EntityTriggerAnalysis {
	static String folderCorpora = "corpus/CG2013/BioNLP-ST_2013_CG_training_data";

	public static void main(String[] args) throws Exception {
		StandoffCorporaParser cgParser = new StandoffCorporaParser();
		File folder = new File(folderCorpora);
		HashSet<String> entities = new HashSet<String>();
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".a2")) {
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				String line = new String();
				while ((line = in.readLine()) != null) {
					if (line.trim().equals(""))
						continue;
					BIONLPAnnotation annotation = cgParser.parseBioNLPString(line);
					if (annotation instanceof BIONLPTerm) {
						entities.add(((BIONLPTerm) annotation).getType());
					}
				}
			}
		}
		for (String entity : entities) {
			System.out.println(entity);
		}
	}

}
