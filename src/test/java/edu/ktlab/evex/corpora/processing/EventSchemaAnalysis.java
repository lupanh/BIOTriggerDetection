package edu.ktlab.evex.corpora.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import edu.ktlab.evex.corpora.bean.BIONLPDocument;
import edu.ktlab.evex.corpora.bean.BIONLPEvent;
import edu.ktlab.evex.corpora.bean.BIONLPSentence;

public class EventSchemaAnalysis {
	static String folderCorpora = "corpus/CG2013/BioNLP-ST_2013_CG_full_data";

	public static void main(String[] args) throws Exception {
		TreeSet<String> schemas = new TreeSet<String>();
		ArrayList<BIONLPDocument> corpora = StandoffCorporaLoading.loadCorpora(folderCorpora, true);
		for (BIONLPDocument doc : corpora) {
			for (BIONLPSentence sentence : doc.getSentences()) {
				HashMap<String, BIONLPEvent> events = sentence.getEvents();
				if (events.size() == 0)
					continue;
				for (BIONLPEvent event : events.values()) {
					Map<String, String> arguments = event.getArguments();
					if (arguments == null)
						continue;
					TreeSet<String> pairs = new TreeSet<String>();
					String pair = "";
					boolean error = false;
					for (String key : arguments.keySet()) {
						String argument = "";
						if (arguments.get(key).startsWith("T")) {
							if (sentence.getEntity(arguments.get(key)) == null) {
								error = true;
								break;
							}
							argument = sentence.getEntity(arguments.get(key)).getType();
						} else if (arguments.get(key).startsWith("E")) {
							if (sentence.getEvent(arguments.get(key)) == null) {
								error = true;
								break;
							}
							argument = sentence.getEvent(arguments.get(key)).getType();
						}
						pair = "-" + key + "-" + argument;
						pairs.add(pair);
					}

					if (!error) {
						String schema = event.getType();						
						for (String p : pairs)
							schema += p.replaceAll("[1-9]", "");
						if (pairs.size() == 0)
						//if (!schema.contains("Theme") && pairs.size() != 0)
							schemas.add(schema);
					}

				}

			}
		}
		for (String schema : schemas)
			System.out.println(schema);
	}

}
