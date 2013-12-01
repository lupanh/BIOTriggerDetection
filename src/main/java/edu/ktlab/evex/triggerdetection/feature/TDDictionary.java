/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.triggerdetection.feature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import edu.ktlab.evex.corpora.bean.BIONLPPair;
import edu.ktlab.evex.util.WordUtil;


public class TDDictionary {
	Set<BIONLPPair<String, String>> dictionary = new HashSet<BIONLPPair<String, String>>();

	public TDDictionary(String file, boolean flagStemmer) {
		this.dictionary = loadDictionary(file, flagStemmer);
	}

	Set<BIONLPPair<String, String>> loadDictionary(String file, boolean flagStemmer) {
		Set<BIONLPPair<String, String>> dict = new HashSet<BIONLPPair<String, String>>();
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String line = "";
			while ((line = reader.readLine()) != null) {
				if (line.trim().equals("")) continue;			
				String[] segs = line.split("\t");			
				dict.add(new BIONLPPair<String, String>(segs[0], segs[1]));

				if (flagStemmer) {				
					dict.add(new BIONLPPair<String, String>(WordUtil.stemmerFeature(segs[0]), segs[1]));;	
				}			
			}
			reader.close();
		} catch (Exception e) {
			return null;
		}
		return dict;		
	}
	
	public Set<String> lookup(String token) {
		if (dictionary == null) return null;
		
		Set<String> labels = new HashSet<String>();
		for (BIONLPPair<String, String> entry : dictionary) {
			if (entry.first().equals(token))
				labels.add(entry.second());
		}
		return labels;
	}

	public static void main(String[] args) throws Exception {
		String file = "data/CG2013/TriggerDictionary.txt";
		TDDictionary dictionary = new TDDictionary(file, false);
		Set<String> labels = dictionary.lookup("used");
		System.out.println(labels);
	}

}
