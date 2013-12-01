/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.evex.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EnjuParser {

	public static void main(String args[]) {
		try {
			Process p = Runtime.getRuntime().exec("3rdTools/enju-win/enju.bat");
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				line = reader.readLine();
			}

		} catch (IOException e1) {
		} catch (InterruptedException e2) {
		}

		System.out.println("Done");
	}
}
