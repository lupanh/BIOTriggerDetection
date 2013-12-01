/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.bean;

public class BIONLPTerm extends BIONLPAnnotation {
	protected int begin;
	protected int originalbegin;
	protected int end;
	protected int originalend;
	protected String text;
	protected String type;
	protected String description;

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getOriginalbegin() {
		return originalbegin;
	}

	public void setOriginalbegin(int originalbegin) {
		this.originalbegin = originalbegin;
	}

	public int getOriginalend() {
		return originalend;
	}

	public void setOriginalend(int originalend) {
		this.originalend = originalend;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	String toBioNLPString() {
		String data = joinItems(type, String.valueOf(begin),
				String.valueOf(end));
		if (text == null)
			return joinFields(id, data); // id<tab>type begin end
		else
			return joinFields(id, data, text); // id<tab>type begin end<tab>text
	}

	@Override
	public void fromBioNLPString(String line) {
		// throw new InternalError("not implemented");
		// System.out.println(line);
		id = line.split("\t")[0];
		type = line.split("\t")[1].split(" ")[0];
		begin = Integer.valueOf(line.split("\t")[1].split(" ")[1]);
		end = Integer.valueOf(line.split("\t")[1].split(" ")[2]);
		text = line.split("\t")[2];
	}
}
