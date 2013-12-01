package edu.ktlab.evex.corpora.processing;

import java.util.ArrayList;

import edu.ktlab.evex.corpora.bean.BIONLPDocument;
import edu.ktlab.evex.corpora.bean.BIONLPSentence;
import edu.ktlab.evex.corpora.bean.BIONLPToken;

public class IndexBIONLPTokenUnitTest {
	static String folderCorpora = "corpus/CG2013/dataTest";

	public static void main(String[] args) throws Exception {
		ArrayList<BIONLPDocument> documents = StandoffCorporaLoading.loadCorpora(folderCorpora, true);
		for (BIONLPDocument doc : documents) {
			System.out.println(doc.getIdFile());
			for (BIONLPSentence sentence : doc.getSentences()) {
				for (BIONLPToken token : sentence.getTokens()) {
					System.out.println(token);
				}					
			}			
		}
	}

}
