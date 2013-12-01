/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.bean;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BIONLPRelation extends BIONLPAnnotation {
	protected String type;
	protected Map<String, String> arguments = new LinkedHashMap<String, String>();

	// get type
	public String getType() {
		return type;
	}

	// set type
	public void setType(String type) {
		this.type = type;
	}

	// get argument
	public Map<String, String> getArguments() {
		return arguments;
	}

	public void setArguments(Map<String, String> arguments) {
		this.arguments = arguments;
	}

	// add argument: key-value
	public void addArgument(String key, String value) {
		if (arguments.containsKey(key)) // chua xu ly truong hop trung
										// key!!!!!!!!!!
			;// throw new IllegalStateException("Duplicate key: " + key); // da
				// ton tai key
		else
			arguments.put(key, value);
	}

	@Override
	String toBioNLPString() {
		List<String> items = new ArrayList<String>(arguments.size() + 1);
		items.add(type);
		for (Entry<String, String> arg : arguments.entrySet()) {
			items.add(arg.getKey() + keyValueDelimiter + arg.getValue());
		}
		return joinFields(id, joinItems(items));
		// vi du: R1 Expression Arg1:T10 Arg2:T9
	}

	@Override
	public void fromBioNLPString(String line) {
		// throw new InternalError("not implemented");
		// System.out.println(line);
		id = line.split("\t")[0];
		type = line.split("\t")[1].split(" ")[0];
		int pair = line.split("\t")[1].split(" ").length - 1;
		// System.out.println(pair);
		for (int i = 1; i <= pair; i++)
			addArgument(line.split("\t")[1].split(" ")[i].split(":")[0], line.split("\t")[1].split(" ")[i].split(":")[1]);
	}
}
