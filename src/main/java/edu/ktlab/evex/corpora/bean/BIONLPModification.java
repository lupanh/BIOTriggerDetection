/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.bean;

public class BIONLPModification extends BIONLPAnnotation {
	protected String type;
	protected String argument;

	// get type
	public String getType() {
		return type;
	}

	// set type
	public void setType(String type) {
		this.type = type;
	}

	// get argument
	public String getArgument() {
		return argument;
	}

	// set argument
	public void setArgument(String argument) {
		this.argument = argument;
	}

	@Override
	String toBioNLPString() {
		String data = joinItems(type, argument);
		return joinFields(id, data); // id<tab>type argument
		// vi du: M11 Speculation E27
	}

	@Override
	public void fromBioNLPString(String line) {
		// throw new InternalError("not implemented");
		// System.out.println(line);
		id = line.split("\t")[0];
		type = line.split("\t")[1].split(" ")[0];
		argument = line.split("\t")[1].split(" ")[1];
		// System.out.println(id + "\t" + type + " " + argument);
	}
}
