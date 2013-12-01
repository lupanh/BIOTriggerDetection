/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.bean;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

public class BIONLPSentence {
	int idSentence;
	String tokenizedText;
	ArrayList<BIONLPToken> tokens;

	String dtree;
	Tree tree;
	SemanticGraph semanticGraph;
	EnglishGrammaticalStructure grammarStructure;

	int startSentenceOfDocument;
	int endSentenceOfDocument;

	HashMap<String, BIONLPTerm> entities;
	HashMap<String, BIONLPTerm> triggers;
	HashMap<String, BIONLPEvent> events;
	HashMap<String, BIONLPModification> modifications;

	public BIONLPSentence() {
		entities = new HashMap<String, BIONLPTerm>();
		triggers = new HashMap<String, BIONLPTerm>();
		events = new HashMap<String, BIONLPEvent>();
		modifications = new HashMap<String, BIONLPModification>();
		tokens = new ArrayList<BIONLPToken>();
	}

	public BIONLPSentence(String tokenizedText) {
		this.tokenizedText = tokenizedText;
		entities = new HashMap<String, BIONLPTerm>();
		triggers = new HashMap<String, BIONLPTerm>();
		events = new HashMap<String, BIONLPEvent>();
		modifications = new HashMap<String, BIONLPModification>();
		tokens = new ArrayList<BIONLPToken>();
	}

	public int getIdSentence() {
		return idSentence;
	}

	public void setIdSentence(int idSentence) {
		this.idSentence = idSentence;
	}

	public String getTokenizedText() {
		return tokenizedText;
	}

	public void setTokenizedText(String tokenizedText) {
		this.tokenizedText = tokenizedText;
	}

	public String getDtree() {
		return dtree;
	}

	@SuppressWarnings("resource")
	public void setDtree(String dtree) throws Exception {
		this.dtree = dtree;
		this.tree = (new PennTreeReader(new StringReader(dtree))).readTree();
		this.grammarStructure = new EnglishGrammaticalStructure(tree);
		this.semanticGraph = new SemanticGraph(grammarStructure.typedDependencies());
	}

	public Tree getTree() {
		return tree;
	}

	public HashMap<String, BIONLPTerm> getEntities() {
		return entities;
	}

	public BIONLPTerm getEntity(String idEntity) {
		return entities.get(idEntity);
	}

	public void setEntities(HashMap<String, BIONLPTerm> entities) {
		this.entities = entities;
	}

	public HashMap<String, BIONLPTerm> getTriggers() {
		return triggers;
	}

	public BIONLPTerm getTrigger(String idTrigger) {
		return triggers.get(idTrigger);
	}

	public void setTriggers(HashMap<String, BIONLPTerm> triggers) {
		this.triggers = triggers;
	}

	public void addTrigger(BIONLPTerm trigger) {
		triggers.put(trigger.getId(), trigger);
	}

	public void addEntity(BIONLPTerm entity) {
		entities.put(entity.getId(), entity);
	}

	public HashMap<String, BIONLPEvent> getEvents() {
		return events;
	}

	public BIONLPEvent getEvent(String idEvent) {
		return events.get(idEvent);
	}

	public BIONLPEvent addEvent(BIONLPEvent event) {
		return events.put(event.getId(), event);
	}

	public void removeEvent(BIONLPEvent event) {
		events.remove(event.getId());
	}

	public void setEvents(HashMap<String, BIONLPEvent> events) {
		this.events = events;
	}

	public HashMap<String, BIONLPModification> getModifications() {
		return modifications;
	}

	public BIONLPModification getModification(String idModification) {
		return modifications.get(idModification);
	}

	public void setModifications(HashMap<String, BIONLPModification> modifications) {
		this.modifications = modifications;
	}

	public int getStartSentenceOfDocument() {
		return startSentenceOfDocument;
	}

	public void setStartSentenceOfDocument(int startSentenceOfDocument) {
		this.startSentenceOfDocument = startSentenceOfDocument;
	}

	public int getEndSentenceOfDocument() {
		return endSentenceOfDocument;
	}

	public void setEndSentenceOfDocument(int endSentenceOfDocument) {
		this.endSentenceOfDocument = endSentenceOfDocument;
	}

	public void setTree(Tree tree) {
		this.tree = tree;
	}

	public ArrayList<BIONLPAnnotation> getAnnotations() {
		ArrayList<BIONLPAnnotation> annotations = new ArrayList<BIONLPAnnotation>();
		annotations.addAll(entities.values());
		annotations.addAll(triggers.values());
		annotations.addAll(events.values());
		annotations.addAll(modifications.values());
		return annotations;
	}

	public EnglishGrammaticalStructure getGrammarticalStructure() {
		return grammarStructure;
	}

	public Collection<TypedDependency> getTypedDependencies() {
		return grammarStructure.typedDependencies();
	}

	public SemanticGraph getSemanticGraph() {
		return semanticGraph;
	}

	public ArrayList<BIONLPToken> getTokens() {
		return tokens;
	}

	public void setTokens(ArrayList<BIONLPToken> tokens) {
		this.tokens = tokens;
	}

	public EnglishGrammaticalStructure getGrammarStructure() {
		return grammarStructure;
	}

	public void setGrammarStructure(EnglishGrammaticalStructure grammarStructure) {
		this.grammarStructure = grammarStructure;
	}

	public void convertTokens() {
		String[] segs = tokenizedText.split(" ");

		int startToken = 0;
		int endToken = 0;
		for (int i = 0; i < segs.length; i++) {
			BIONLPToken token = new BIONLPToken();
			token.setIdToken(i + 1);
			token.setTextToken(segs[i]);

			int newStartOffset = startSentenceOfDocument + startToken;
			token.setStartOffsetSentence(startToken);
			endToken = startToken + segs[i].length();
			token.setEndOffsetSentence(endToken);
			int newEndOffset = startSentenceOfDocument + endToken;
			startToken = endToken + 1;

			BIONLPTerm annotation = null;
			for (BIONLPTerm entity : entities.values()) {
				if ((token.getStartOffsetSentence() >= entity.getBegin() && token.getEndOffsetSentence() <= entity.getEnd())
						|| (token.getStartOffsetSentence() <= entity.getBegin() && token.getEndOffsetSentence() >= entity.getEnd()))
					annotation = entity;
			}

			for (BIONLPTerm trigger : triggers.values()) {
				if ((token.getStartOffsetSentence() >= trigger.getBegin() && token.getEndOffsetSentence() <= trigger.getEnd())
						|| (token.getStartOffsetSentence() <= trigger.getBegin() && token.getEndOffsetSentence() >= trigger.getEnd()))
					annotation = trigger;
			}

			token.setStartOffsetDocument(newStartOffset);
			token.setEndOffsetDocument(newEndOffset);

			token.setAnnotation(annotation);
			tokens.add(token);
		}
	}

	public ArrayList<BIONLPPair<BIONLPTerm, BIONLPTerm>> getEventEntityRelationPair() {
		ArrayList<BIONLPPair<BIONLPTerm, BIONLPTerm>> listPair = new ArrayList<BIONLPPair<BIONLPTerm, BIONLPTerm>>();
		for (BIONLPEvent event : events.values()) {
			Map<String, String> arguments = event.getArguments();
			Map<BIONLPTerm, String> entitiesArgument = new HashMap<BIONLPTerm, String>();
			for (String type : arguments.keySet()) {
				BIONLPTerm entity = entities.get(arguments.get(type));
				if (entity != null)
					entitiesArgument.put(entity, type.replaceAll("\\d+", ""));
				// entitiesArgument.put(entity, type.replaceAll("\\d+", "") +
				// "+" + entity.getType());
			}

			for (BIONLPTerm entity : entities.values()) {
				BIONLPPair<BIONLPTerm, BIONLPTerm> TEpair = new BIONLPPair<BIONLPTerm, BIONLPTerm>();
				TEpair.setFirst(triggers.get(event.getTriggerTerm()));
				TEpair.setSecond(entity);
				if (entitiesArgument.containsKey(entity))
					TEpair.setLabel(entitiesArgument.get(entity));
				else
					TEpair.setLabel("NO_RELATION");
				listPair.add(TEpair);
			}
		}

		return listPair;
	}

	public ArrayList<BIONLPPair<BIONLPTerm, BIONLPTerm>> getTriggerEntityRelationPair() {
		ArrayList<BIONLPPair<BIONLPTerm, BIONLPTerm>> listPair = new ArrayList<BIONLPPair<BIONLPTerm, BIONLPTerm>>();
		for (BIONLPTerm trigger : triggers.values()) {
			for (BIONLPTerm entity : entities.values()) {
				BIONLPPair<BIONLPTerm, BIONLPTerm> TEpair = new BIONLPPair<BIONLPTerm, BIONLPTerm>();
				TEpair.setFirst(trigger);
				TEpair.setSecond(entity);
				TEpair.setLabel("");
				listPair.add(TEpair);
			}
		}

		return listPair;
	}

	ArrayList<String> complexEvents = new ArrayList<String>(Arrays.asList("Regulation", "Positive_regulation", "Negative_regulation",
			"Planned_process"));

	public ArrayList<BIONLPPair<BIONLPEvent, BIONLPEvent>> getEventEventRelationPairNoLabel() {
		ArrayList<BIONLPPair<BIONLPEvent, BIONLPEvent>> listPair = new ArrayList<BIONLPPair<BIONLPEvent, BIONLPEvent>>();
		for (BIONLPEvent eventSrc : events.values()) {
			if (!complexEvents.contains(eventSrc.getType()))
				continue;
			for (BIONLPEvent eventTar : events.values()) {
				if (eventSrc.equals(eventTar))
					continue;
				BIONLPPair<BIONLPEvent, BIONLPEvent> TEpair = new BIONLPPair<BIONLPEvent, BIONLPEvent>();
				TEpair.setFirst(eventSrc);
				TEpair.setSecond(eventTar);
				TEpair.setLabel("");
				listPair.add(TEpair);
			}
		}
		return listPair;
	}

	public ArrayList<BIONLPPair<BIONLPEvent, BIONLPEvent>> getEventEventRelationPair() {
		ArrayList<BIONLPPair<BIONLPEvent, BIONLPEvent>> listPair = new ArrayList<BIONLPPair<BIONLPEvent, BIONLPEvent>>();
		for (BIONLPEvent event : events.values()) {
			if (!complexEvents.contains(event.getType()))
				continue;
			Map<String, String> arguments = event.getArguments();
			Map<BIONLPEvent, String> triggersArgument = new HashMap<BIONLPEvent, String>();
			for (String type : arguments.keySet()) {
				BIONLPEvent triggerCandidate = events.get(arguments.get(type));
				if (triggerCandidate != null) {
					triggersArgument.put(triggerCandidate, type.replace("CSite", "Site").replaceAll("\\d+", ""));
				}
			}

			for (BIONLPEvent eventCandidate : events.values()) {
				if (eventCandidate.equals(event))
					continue;
				BIONLPPair<BIONLPEvent, BIONLPEvent> TTpair = new BIONLPPair<BIONLPEvent, BIONLPEvent>();
				TTpair.setFirst(event);
				TTpair.setSecond(eventCandidate);
				if (triggersArgument.containsKey(eventCandidate))
					TTpair.setLabel(triggersArgument.get(eventCandidate));
				else
					TTpair.setLabel("NO_RELATION");
				listPair.add(TTpair);
			}
		}

		return listPair;
	}

	public ArrayList<String> getComplexEvents() {
		return complexEvents;
	}

	public void setComplexEvents(ArrayList<String> complexEvents) {
		this.complexEvents = complexEvents;
	}
}
