/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.bean;

public class BIONLPToken {
	int idToken;
	String textToken;
	int startOffsetDocument;
	int startOffsetSentence;
	int startOriginalOffsetDocument;
	int endOffsetDocument;
	int endOffsetSentence;
	int endOriginalOffsetDocument;
	BIONLPTerm annotation;

	public BIONLPToken() {
	}

	public BIONLPToken(int idToken, String textToken, int startOffsetDocument, int startOffsetSentence, int endOffsetDocument,
			int endOffsetSentence, BIONLPTerm annotation) {
		this.idToken = idToken;
		this.textToken = textToken;
		this.startOffsetDocument = startOffsetDocument;
		this.startOffsetSentence = startOffsetSentence;
		this.endOffsetDocument = endOffsetDocument;
		this.endOffsetSentence = endOffsetSentence;
		this.annotation = annotation;
	}

	public int getIdToken() {
		return idToken;
	}

	public void setIdToken(int idToken) {
		this.idToken = idToken;
	}

	public String getTextToken() {
		return textToken;
	}

	public void setTextToken(String textToken) {
		this.textToken = textToken;
	}

	public int getStartOffsetDocument() {
		return startOffsetDocument;
	}

	public void setStartOffsetDocument(int startOffsetDocument) {
		this.startOffsetDocument = startOffsetDocument;
	}

	public int getStartOffsetSentence() {
		return startOffsetSentence;
	}

	public void setStartOffsetSentence(int startOffsetSentence) {
		this.startOffsetSentence = startOffsetSentence;
	}

	public int getEndOffsetDocument() {
		return endOffsetDocument;
	}

	public void setEndOffsetDocument(int endOffsetDocument) {
		this.endOffsetDocument = endOffsetDocument;
	}

	public int getEndOffsetSentence() {
		return endOffsetSentence;
	}

	public void setEndOffsetSentence(int endOffsetSentence) {
		this.endOffsetSentence = endOffsetSentence;
	}

	public BIONLPTerm getAnnotation() {
		return annotation;
	}

	public void setAnnotation(BIONLPTerm annotation) {
		this.annotation = annotation;
	}

	public int getStartOriginalOffsetDocument() {
		return startOriginalOffsetDocument;
	}

	public void setStartOriginalOffsetDocument(int startOriginalOffsetDocument) {
		this.startOriginalOffsetDocument = startOriginalOffsetDocument;
	}

	public int getEndOriginalOffsetDocument() {
		return endOriginalOffsetDocument;
	}

	public void setEndOriginalOffsetDocument(int endOriginalOffsetDocument) {
		this.endOriginalOffsetDocument = endOriginalOffsetDocument;
	}

	@Override
	public String toString() {
		return "BIONLPToken [idToken=" + idToken + ", textToken=" + textToken + ", startOffsetDocument=" + startOffsetDocument
				+ ", startOffsetSentence=" + startOffsetSentence + ", startOriginalOffsetDocument=" + startOriginalOffsetDocument
				+ ", endOffsetDocument=" + endOffsetDocument + ", endOffsetSentence=" + endOffsetSentence + ", endOriginalOffsetDocument="
				+ endOriginalOffsetDocument + ", annotation=" + annotation + "]";
	}
}
