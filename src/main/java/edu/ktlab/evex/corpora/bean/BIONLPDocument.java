/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.bean;

import java.util.ArrayList;

public class BIONLPDocument {
	String idFile;
	String originalText;
	String tokenizedText;
	ArrayList<BIONLPSentence> sentences;
	int lastIdOfEntity;

	public BIONLPDocument(String idFile, String originalText, String tokenizedText) {
		this.idFile = idFile;
		this.originalText = originalText;
		this.tokenizedText = tokenizedText;
	}

	public BIONLPDocument(String idFile, String originalText, String tokenizedText,
			ArrayList<BIONLPSentence> sentences) {
		this.idFile = idFile;
		this.originalText = originalText;
		this.tokenizedText = tokenizedText;
		this.sentences = sentences;
	}

	public String getIdFile() {
		return idFile;
	}

	public void setIdFile(String idFile) {
		this.idFile = idFile;
	}

	public String getOriginalText() {
		return originalText;
	}

	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	public String getTokenizedText() {
		return tokenizedText;
	}

	public void setTokenizedText(String tokenizedText) {
		this.tokenizedText = tokenizedText;
	}

	public ArrayList<BIONLPSentence> getSentences() {
		return sentences;
	}

	public void setSentences(ArrayList<BIONLPSentence> sentences) {
		this.sentences = sentences;
	}

	public int getLastIdOfEntity() {
		return lastIdOfEntity;
	}

	public void setLastIdOfEntity(int lastIdOfEntity) {
		this.lastIdOfEntity = lastIdOfEntity;
	}
}
