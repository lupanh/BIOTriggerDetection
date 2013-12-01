/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.bean;

import java.util.ArrayList;

public class ANNSentence {
	String idFile;
	String text;	
	ArrayList<BIONLPAnnotation> annotations;
	
	public ANNSentence() {
		annotations = new ArrayList<BIONLPAnnotation>();
	}
	
	public ANNSentence(String text) {
		this.text = text;
		annotations = new ArrayList<BIONLPAnnotation>();
	}
	
	public String getIdFile() {
		return idFile;
	}

	public void setIdFile(String idFile) {
		this.idFile = idFile;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ArrayList<BIONLPAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(ArrayList<BIONLPAnnotation> annotations) {
		this.annotations = annotations;
	}	
	
	public void addAnnotation(BIONLPAnnotation annotation) {
		annotations.add(annotation);
	}
	
	public BIONLPAnnotation getAnnotation(String id) {
		for (BIONLPAnnotation ann : annotations) {
			if (ann.getId().equals(id)) return ann;
		}
		return null;
	}
	
}
