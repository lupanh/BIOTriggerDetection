package edu.ktlab.evex.corpora.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import edu.ktlab.evex.corpora.bean.BIONLPAnnotation;
import edu.ktlab.evex.corpora.bean.BIONLPEquiv;
import edu.ktlab.evex.corpora.bean.BIONLPEvent;
import edu.ktlab.evex.corpora.bean.BIONLPModification;
import edu.ktlab.evex.corpora.bean.BIONLPRelation;
import edu.ktlab.evex.corpora.bean.BIONLPTerm;

public class StandoffAnnParser {
	static String folderCorpora = "corpus/MLEE-1.0.2-rev1";
	static String[] entityTypes;
	static String[] triggerTypes;

	public static BIONLPAnnotation parseBioNLPString(String line) {
		BIONLPAnnotation x;
		if (line.startsWith("E")) {
			x = new BIONLPEvent();
		} else if (line.startsWith("R")) {
			x = new BIONLPRelation();
		} else if (line.startsWith("*"))
			x = new BIONLPEquiv();
		else if (line.startsWith("T")) {
			x = new BIONLPTerm();
		} else if (line.startsWith("M")) {
			x = new BIONLPModification();
		} else {
			return null;
		}
		x.fromBioNLPString(line);
		if (x instanceof BIONLPTerm) {
			for (String type : entityTypes)
				if (((BIONLPTerm) x).getType().toLowerCase().equals(type))
					((BIONLPTerm) x).setDescription("ENTITY");
			for (String type : triggerTypes)
				if (((BIONLPTerm) x).getType().toLowerCase().equals(type))
					((BIONLPTerm) x).setDescription("TRIGGER");
		}
		return x;
	}

	public static ArrayList<BIONLPAnnotation> parseFile(File f) {
		ArrayList<BIONLPAnnotation> annotations = new ArrayList<BIONLPAnnotation>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			String line = new String();
			while ((line = in.readLine()) != null) {
				if (line.trim().equals(""))
					continue;
				BIONLPAnnotation annotation = parseBioNLPString(line);
				annotations.add(annotation);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return annotations;
	}

	public static void main(String[] args) {
		entityTypes = EventElementsConstant.entityType.toLowerCase().split(" ");
		triggerTypes = EventElementsConstant.triggerType.toLowerCase().split(" ");

		File folder = new File(folderCorpora);
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File file : files)
				if (file.getAbsolutePath().endsWith(".ann")) {
					ArrayList<BIONLPAnnotation> annotations = parseFile(file);
					for (BIONLPAnnotation annotation : annotations)
						if (annotation instanceof BIONLPTerm) {
							String desc = ((BIONLPTerm) annotation).getDescription();
							if (desc != null && desc.equals("TRIGGER"))
								System.out.println(((BIONLPTerm) annotation).getText().toLowerCase() + "\t" + ((BIONLPTerm) annotation).getType());
						}
							
				}
		}
	}

}
