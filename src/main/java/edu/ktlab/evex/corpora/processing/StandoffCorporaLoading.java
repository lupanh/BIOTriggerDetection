/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.processing;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import jp.ac.u_tokyo.s.is.standoff.StandoffConverter;

import edu.ktlab.evex.corpora.bean.BIONLPAnnotation;
import edu.ktlab.evex.corpora.bean.BIONLPDocument;
import edu.ktlab.evex.corpora.bean.BIONLPEvent;
import edu.ktlab.evex.corpora.bean.BIONLPModification;
import edu.ktlab.evex.corpora.bean.BIONLPSentence;
import edu.ktlab.evex.corpora.bean.BIONLPTerm;
import edu.ktlab.evex.corpora.bean.BIONLPToken;

public class StandoffCorporaLoading {
	static String folderTraining = "corpus/CG2013/BioNLP-ST_2013_CG_development_data";
	static StandoffCorporaParser cgParser = new StandoffCorporaParser();

	public static ArrayList<BIONLPDocument> loadCorpora(String folderCorpora) throws Exception {
		return loadCorpora(folderCorpora, false);
	}

	public static ArrayList<BIONLPDocument> loadCorpora(String folderCorpora, boolean silent) throws Exception {
		File folder = new File(folderCorpora);
		ArrayList<BIONLPDocument> documents = new ArrayList<BIONLPDocument>();
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".txt")) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				String name = file.getPath().replaceAll(".txt", "");
				if (!silent)
					System.out.println(name);

				String contentTextFile = readFile(file);
				String contentTokenizerFile = readFile(new File(name + ".tok"));
				String contentA1File = readFile(new File(name + ".a1"));
				String contentPTBFile = readFile(new File(name + ".ptb"));

				BIONLPDocument document = new BIONLPDocument(name, contentTextFile, contentTokenizerFile);

				StandoffConverter.convert(false, new ByteArrayInputStream(contentTextFile.getBytes()), new ByteArrayInputStream(
						contentA1File.getBytes()), new ByteArrayInputStream(contentTokenizerFile.getBytes()), new PrintStream(out));
				if (new File(name + ".a2").exists()) {
					String contentA2File = readFile(new File(name + ".a2"));
					StandoffConverter.convert(false, new ByteArrayInputStream(contentTextFile.getBytes()), new ByteArrayInputStream(
							contentA2File.getBytes()), new ByteArrayInputStream(contentTokenizerFile.getBytes()), new PrintStream(out));
				}

				ArrayList<BIONLPAnnotation> annotationFulltext = new ArrayList<BIONLPAnnotation>();

				int lastId = 0;
				for (String line : out.toString().split("\r\n")) {
					// System.out.println(line);
					BIONLPAnnotation annotation = cgParser.parseBioNLPString(line);
					if (annotation == null)
						continue;
					if (annotation.getId().startsWith("T")) {
						int id = Integer.parseInt(annotation.getId().replace("T", ""));
						if (id >= lastId)
							lastId = id;
					}

					annotationFulltext.add(annotation);
					// System.out.println(annotation);
				}
				document.setLastIdOfEntity(lastId);

				String[] sens = contentTokenizerFile.split("\n");
				String[] trees = contentPTBFile.split("\n");

				int indexStartSen = 0;
				int indexEndSen = 0;
				ArrayList<BIONLPSentence> bioSents = new ArrayList<BIONLPSentence>();

				for (int i = 0; i < sens.length; i++) {
					BIONLPSentence bioSent = new BIONLPSentence(sens[i]);
					bioSent.setIdSentence(i);
					bioSent.setDtree(trees[i]);

					// System.out.println(sens[i]);
					// System.out.println(trees[i]);

					indexStartSen = indexEndSen;
					indexEndSen += sens[i].length() + 1;

					bioSent.setStartSentenceOfDocument(indexStartSen);
					bioSent.setEndSentenceOfDocument(indexEndSen);

					HashMap<String, BIONLPTerm> entitiesSentence = new HashMap<String, BIONLPTerm>();
					HashMap<String, BIONLPTerm> triggersSentence = new HashMap<String, BIONLPTerm>();
					for (BIONLPAnnotation annFull : annotationFulltext) {
						if (annFull instanceof BIONLPTerm) {
							if (((BIONLPTerm) annFull).getEnd() >= indexStartSen && ((BIONLPTerm) annFull).getEnd() < indexEndSen - 1) {
								((BIONLPTerm) annFull).setOriginalbegin(((BIONLPTerm) annFull).getBegin());
								((BIONLPTerm) annFull).setOriginalend(((BIONLPTerm) annFull).getEnd());

								((BIONLPTerm) annFull).setBegin(((BIONLPTerm) annFull).getBegin() - indexStartSen);
								((BIONLPTerm) annFull).setEnd(((BIONLPTerm) annFull).getEnd() - indexStartSen);
								if (((BIONLPTerm) annFull).getDescription().equals("ENTITY"))
									entitiesSentence.put(annFull.getId(), (BIONLPTerm) annFull);
								else if (((BIONLPTerm) annFull).getDescription().equals("TRIGGER"))
									triggersSentence.put(annFull.getId(), (BIONLPTerm) annFull);
								// System.out.println(annFull);
							}
						}
					}
					bioSent.setEntities(entitiesSentence);
					bioSent.setTriggers(triggersSentence);

					HashMap<String, BIONLPEvent> eventsSentence = new HashMap<String, BIONLPEvent>();
					for (BIONLPAnnotation annFull : annotationFulltext) {
						if (annFull instanceof BIONLPEvent) {
							if (triggersSentence.containsKey(((BIONLPEvent) annFull).getTriggerTerm())) {
								eventsSentence.put(annFull.getId(), (BIONLPEvent) annFull);
								// System.out.println(((Event)
								// annFull).getArguments());
							}
						}
					}
					bioSent.setEvents(eventsSentence);

					HashMap<String, BIONLPModification> modificationsSentence = new HashMap<String, BIONLPModification>();
					for (BIONLPAnnotation annFull : annotationFulltext) {
						if (annFull instanceof BIONLPModification) {
							if (eventsSentence.containsKey(((BIONLPModification) annFull).getArgument())) {
								modificationsSentence.put(annFull.getId(), (BIONLPModification) annFull);
								// System.out.println(annFull);
							}
						}
					}
					bioSent.setModifications(modificationsSentence);
					bioSent.convertTokens();

					bioSents.add(bioSent);

				}
				document.setSentences(bioSents);				
				
				int index = 0;
				for (BIONLPSentence sentence : document.getSentences()) {
					for (BIONLPToken token : sentence.getTokens()) {
						index = contentTextFile.indexOf(token.getTextToken(), index);
						token.setStartOriginalOffsetDocument(index);
						token.setEndOriginalOffsetDocument(index + token.getTextToken().length());
						index = index + token.getTextToken().length();
					}					
				}
				
				documents.add(document);
			}
		}
		return documents;
	}

	public static void main(String[] args) throws Exception {
		ArrayList<BIONLPDocument> corpora = loadCorpora(folderTraining);
		for (BIONLPDocument doc : corpora) {
			for (BIONLPSentence sentence : doc.getSentences()) {
				System.out.println(sentence.getEntities());
			}
		}
	}

	public static String readFile(File f) {
		String text = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			String line = new String();
			while ((line = in.readLine()) != null) {
				text += line + "\n";
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return text;
	}

}
