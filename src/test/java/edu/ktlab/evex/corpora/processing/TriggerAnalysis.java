package edu.ktlab.evex.corpora.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import edu.ktlab.evex.corpora.bean.BIONLPAnnotation;
import edu.ktlab.evex.corpora.bean.BIONLPTerm;
import edu.ktlab.evex.corpora.processing.StandoffCorporaParser;
import edu.ktlab.evex.corpora.processing.EventElementsConstant;
import edu.ktlab.evex.triggerdetection.feature.TDDictionary;

public class TriggerAnalysis {
	static String folderCorpora = "corpus/CG2013/BioNLP-ST_2013_CG_full_data";
	static String fileDictionary = "data/CG2013/TriggerDictionary.txt";

	public static void main(String[] args) throws Exception {
		TDDictionary dictionary = new TDDictionary(fileDictionary, true);

		String[] triggerTypes = EventElementsConstant.triggerType.toLowerCase().split(" ");
		StandoffCorporaParser cgParser = new StandoffCorporaParser();
		File folder = new File(folderCorpora);
		HashSet<String> entities = new HashSet<String>();
		int count = 0;
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".a2") || file.getName().endsWith(".a1")) {
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(
						file), "UTF-8"));
				String line = new String();
				while ((line = in.readLine()) != null) {
					if (line.trim().equals(""))
						continue;
					BIONLPAnnotation annotation = cgParser.parseBioNLPString(line);
					if (annotation instanceof BIONLPTerm) {
						for (String type : triggerTypes)
							if (((BIONLPTerm) annotation).getType().toLowerCase().equals(type)) {
								//Set<String> labels = dictionary.lookup(((BIONLPTerm) annotation).getText().toLowerCase());
								//if (labels.size() > 0) continue;
								entities.add(((BIONLPTerm) annotation).getText().toLowerCase()
										+ "\t" + ((BIONLPTerm) annotation).getType());
								count++;
							}
					}
				}
				in.close();
			}
		}
		for (String entity : entities) {
			System.out.println(entity);
		}
		System.out.println(count);
	}

}
