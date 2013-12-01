/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LongestMatching {
	HashMap<String, String> dictionary = new HashMap<String, String>();
	
	public LongestMatching(HashMap<String, String> dictionary) {
		this.dictionary = dictionary;
	}

	public Span[] tagging(String[] sentence
			, int max_key_size, boolean caseSensitive) {
		ArrayList<Span> names = new ArrayList<Span>();

		int N = sentence.length;
		if (max_key_size == -1) {
			max_key_size = N;
		}
		int i = 0;
		while (i < N) {
			boolean tagged = false;
			int j = Math.min(i + max_key_size, N);
			while (j > i) {
				String[] literal_tokens = Arrays.copyOfRange(sentence, i, j);
				String literal = join(literal_tokens, " ");
				String tag;
				if (caseSensitive)
					tag = (String) dictionary.get(literal.toLowerCase());
				else 
					tag = (String) dictionary.get(literal);
	
				if (tag != null) {
					Span token = new Span(i, j, tag);
					names.add(token);
					i = j;
					tagged = true;
				} else {
					j -= 1;
				}
			}
			if (!tagged) {
				i += 1;
			}
		}
		
		return names.toArray(new Span[names.size()]);
	}

	private static String join(String[] sentence, String separator) {
		String result = "";
		for (int i = 0; i < sentence.length; i++) {
			String word = sentence[i];
			result += word + separator;
		}
		return result.trim();
	}
}
