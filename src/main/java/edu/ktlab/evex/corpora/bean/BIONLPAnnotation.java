/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.bean;

import java.util.List;

public abstract class BIONLPAnnotation {
	protected String id;
	private final static String fieldDelimiter = "\t";
	private final static String itemDelimiter = " ";
	protected final static String keyValueDelimiter = ":";

	abstract String toBioNLPString();

	public abstract void fromBioNLPString(String line);

	// get ID
	public String getId() {
		return id;
	}

	// set ID
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return toBioNLPString();
	}

	// noi cac item, xen giua la cac ki tu ngan cach
	protected final static String joinItems(String... items) {
		return join(itemDelimiter, items); // itemDelimiter = " "
	}

	protected final static String joinItems(List<String> items) {
		return join(itemDelimiter, items); // itemDelimiter = " "
	}

	protected final static String joinFields(String... fields) {
		return join(fieldDelimiter, fields); // fieldDelimiter = "\t"
	}

	protected final static String joinFields(List<String> fields) {
		return join(fieldDelimiter, fields); // fieldDelimiter = "\t"
	}

	static private final String join(String glue, String... fields) {
		if (fields.length == 0)
			return "";
		if (fields.length == 1)
			return fields[0];
		StringBuilder sb = new StringBuilder();
		for (String field : fields) {
			if (sb.length() > 0)
				sb.append(glue);
			sb.append(field);
		}
		return sb.toString();
	}

	static private final String join(String glue, List<String> fields) {
		if (fields.isEmpty())
			return "";
		if (fields.size() == 1)
			return fields.get(0);
		StringBuilder sb = new StringBuilder();
		for (String field : fields) {
			if (sb.length() > 0)
				sb.append(glue);
			sb.append(field);
		}
		return sb.toString();
	}
}
