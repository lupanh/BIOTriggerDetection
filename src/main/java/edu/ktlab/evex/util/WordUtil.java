/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.util;

import org.tartarus.snowball.ext.EnglishStemmer;

import edu.stanford.nlp.util.StringUtils;

public class WordUtil {
	public static String wordFeatures(String word) {
		if (StringUtils.isPunct(word))
			return "PUNCT";
		if (StringUtils.isNumeric(word))
			return "NUM";
		if (StringUtils.isAcronym(word))
			return "ALLCAP";
		if (StringUtils.isCapitalized(word))
			return "CAPT";
		return null;
	}

	public static String stemmerFeature(String token) {
		EnglishStemmer stemmer = new EnglishStemmer();
		stemmer.setCurrent(token);
		stemmer.stem();
		return stemmer.getCurrent();
	}
}
