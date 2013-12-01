/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.util;

import java.util.HashMap;
import java.util.Map;

public class TupleToken {	
	String[] tokens;
	Map<String, Span[]> annotation = new HashMap<String, Span[]>();
	
	public TupleToken(String[] tokens) {
		this.tokens = tokens;		
	}
	
	public TupleToken(String[] tokens, Map<String, Span[]> annotation) {
		this.tokens = tokens;
		this.annotation = annotation;
	}
	
	public void put(String label, Span[] tagged) {
		annotation.put(label, tagged);
	}

	public String[] getTokens() {
		return tokens;
	}

	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	public Map<String, Span[]> getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Map<String, Span[]> annotation) {
		this.annotation = annotation;
	}	
}
