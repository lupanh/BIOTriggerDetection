/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.corpora.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class BIONLPEvent extends BIONLPRelation {

	protected String triggerTerm;

	// get trigger term
	public String getTriggerTerm() {
		return triggerTerm;
	}

	// set trigger term
	public void setTriggerTerm(String triggerTerm) {
		this.triggerTerm = triggerTerm;
	}

	@Override
	String toBioNLPString() {
		// throw new InternalError("not implemented");
		List<String> items = new ArrayList<String>(arguments.size() + 1);
		items.add(type + keyValueDelimiter + triggerTerm); // them trigger
		if (arguments.entrySet().isEmpty() == true)
			items.add(""); // neu event khong co tham so thi them 1 dau " " o
							// cuoi cung cua line
		else
			for (Entry<String, String> arg : arguments.entrySet()) {
				items.add(arg.getKey() + keyValueDelimiter + arg.getValue());
			}
		return joinFields(id, joinItems(items));
		// vi du: E6 Regulation:T55 Theme:E18 Cause:T17
	}

	@Override
	public void fromBioNLPString(String line) {
		// throw new InternalError("not implemented");
		// System.out.println(line);
		id = line.split("\t")[0];
		type = line.split("\t")[1].split(" ")[0].split(":")[0];
		setTriggerTerm(line.split("\t")[1].split(" ")[0].split(":")[1]);
		int pair = line.split("\t")[1].split(" ").length - 1;
		// System.out.println(pair);
		for (int i = 1; i <= pair; i++)
			addArgument(line.split("\t")[1].split(" ")[i].split(":")[0], line.split("\t")[1].split(" ")[i].split(":")[1]);
	}

}
