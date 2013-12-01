/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.util.classifier;

import de.bwaldvogel.liblinear.Predict;
import de.bwaldvogel.liblinear.Train;

public class LiblinearTraining {
	public static String fileTraining = "models/TriggerDetection/TriggerDetection.training";
	public static String fileTesting = "models/TriggerDetection/TriggerDetection.development";
	public static String fileModel = "models/TriggerDetection/test.model";

	public static void main(String[] args) throws Exception {
		new Train().main(new String[] { "-s", "7", fileTraining, fileModel });
		new Predict().main(new String[] { "-b", "1", fileTesting, fileModel, "SEdev.output" });
	}

}
