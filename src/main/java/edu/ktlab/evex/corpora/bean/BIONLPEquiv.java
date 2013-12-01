/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.bean;

//Class Equiv: bieu dien 2 entity tuong duong
public class BIONLPEquiv extends BIONLPAnnotation {
	protected String term1;
	protected String term2;

	// get term1
	public String getTerm1() {
		return term1;
	}

	// set term1
	public void setTerm1(String term1) {
		this.term1 = term1;
	}

	// get term2
	public String getTerm2() {
		return term2;
	}

	// set term2
	public void setTerm2(String term2) {
		this.term2 = term2;
	}

	@Override
	String toBioNLPString() {
		return joinFields("*", joinItems("Equiv", term1, term2)); // *<tab>Equiv
																	// term1
																	// term 2
	}

	@Override
	public void fromBioNLPString(String line) {
		// throw new InternalError("not implemented");
		id = "Equiv";
		term1 = line.split("\t")[1].split(" ")[1];
		term2 = line.split("\t")[1].split(" ")[2];
		// System.out.println("*\t" + id + " " + term1 + " " + term2);
	}
}
