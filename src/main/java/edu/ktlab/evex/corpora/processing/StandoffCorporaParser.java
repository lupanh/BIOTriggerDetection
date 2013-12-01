/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.processing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import edu.ktlab.evex.corpora.bean.ANNSentence;
import edu.ktlab.evex.corpora.bean.BIONLPAnnotation;
import edu.ktlab.evex.corpora.bean.BIONLPEquiv;
import edu.ktlab.evex.corpora.bean.BIONLPEvent;
import edu.ktlab.evex.corpora.bean.BIONLPModification;
import edu.ktlab.evex.corpora.bean.BIONLPRelation;
import edu.ktlab.evex.corpora.bean.BIONLPTerm;

public class StandoffCorporaParser {	
	String[] entityTypes;
	String[] triggerTypes;

	public StandoffCorporaParser() {
		entityTypes = EventElementsConstant.entityType.toLowerCase().split(" ");
		triggerTypes = EventElementsConstant.triggerType.toLowerCase().split(" ");
	}

	public BIONLPAnnotation parseBioNLPString(String line) {
		BIONLPAnnotation x;
		if (line.startsWith("E")) {
			x = new BIONLPEvent();
		} else if (line.startsWith("R")) {
			x = new BIONLPRelation();
		} else if (line.startsWith("*"))
			x = new BIONLPEquiv();
		else if (line.startsWith("T")) {
			x = new BIONLPTerm();
		} else if (line.startsWith("M")) {
			x = new BIONLPModification();
		} else {
			return null;
		}
		x.fromBioNLPString(line);
		if (x instanceof BIONLPTerm) {
			for (String type : entityTypes)
				if (((BIONLPTerm) x).getType().toLowerCase().equals(type))
					((BIONLPTerm) x).setDescription("ENTITY");
			for (String type : triggerTypes)
				if (((BIONLPTerm) x).getType().toLowerCase().equals(type))
					((BIONLPTerm) x).setDescription("TRIGGER");
		}
		return x;
	}

	public ArrayList<ANNSentence> loadCorpora(String file) throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),
				"UTF-8"));
		ArrayList<ANNSentence> sentences = new ArrayList<ANNSentence>();
		String line = new String();
		while ((line = in.readLine()) != null) {
			if (!line.contains("\t") && line.length() > 1) {
				ANNSentence sentence = new ANNSentence(line);
				while ((line = in.readLine()) != null && line.length() != 0) {
					BIONLPAnnotation annotation = parseBioNLPString(line);
					if (annotation != null)
						sentence.addAnnotation(annotation);
				}
				sentences.add(sentence);
			}
		}
		in.close();
		return sentences;
	}
}
