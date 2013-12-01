/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.bean;

import org.apache.commons.lang3.StringEscapeUtils;

//Class AnnotatorNote : bieu dien comment cua nguoi ghi chu thich
public class BIONLPAnnotatorNote extends BIONLPAnnotation {
	protected String referredId; // id
	protected String noteText; // text

	// get referred ID
	public String getReferredId() {
		return referredId;
	}

	// set referred ID
	public void setReferredId(String id1) {
		this.referredId = id1;
	}

	// get text
	public String getNoteText() {
		return noteText;
	}

	// set text
	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}

	@Override
	String toBioNLPString() {
		return joinFields(getId(), joinItems("AnnotatorNote", referredId),
				StringEscapeUtils.escapeJava(noteText));
		// vi du: #1<tab>AnnotatorNote T4<tab>comment
		// vi du: #2<tab>AnnotatorNotes T1<tab>this annotation is suspect
		// idAnnotation<tab>nguoiNote referredID<text> (kieu note ?? nguoi note)
	}

	@Override
	public void fromBioNLPString(String line) {
		throw new InternalError("not implemented");
	}

}
