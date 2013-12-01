package edu.ktlab.evex.corpora.processing;

import java.util.ArrayList;
import java.util.Map;

import edu.ktlab.evex.corpora.bean.BIONLPDocument;
import edu.ktlab.evex.corpora.bean.BIONLPEvent;
import edu.ktlab.evex.corpora.bean.BIONLPPair;
import edu.ktlab.evex.corpora.bean.BIONLPSentence;

public class ComplexEventParser {
	static String folderCorpora = "corpus/CG2013/BioNLP-ST_2013_CG_full_data";

	public static void main(String[] args) throws Exception {
		ArrayList<BIONLPDocument> documents = StandoffCorporaLoading.loadCorpora(folderCorpora, true);
		for (BIONLPDocument doc : documents) {
			for (BIONLPSentence sentence : doc.getSentences()) {
				for (BIONLPPair<BIONLPEvent, BIONLPEvent> pair : sentence.getEventEventRelationPair()) {
					Map<String, String> arguments = pair.first().getArguments();
					String argus = "";
					for (String key : arguments.keySet()) {
						if (arguments.get(key).startsWith("T")) {
							String entity = (sentence.getEntity(arguments.get(key)) != null) ? sentence.getEntity(arguments.get(key))
									.getType() : "";
							if (!entity.equals(""))
								argus += ":" + key.replaceAll("[1-9]", "") + ":" + entity;
							else
								argus += ":" + key.replaceAll("[1-9]", "");
						}

					}
					String label = pair.first().getType() + argus + "-" + pair.getLabel() + "-" + pair.second().getType();
					if (label.contains("NO_RELATION"))
						label = "NO_RELATION";
					System.out.println(label);
				}
			}
		}
	}

}
