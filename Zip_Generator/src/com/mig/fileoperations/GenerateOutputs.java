package com.mig.fileoperations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class GenerateOutputs {
	public static Logger logger = Logger.getLogger(GenerateOutputs.class);
	
	public void writeCSV(String FILENAME, String data, String headers){
		logger.info("into writeCSV : "+FILENAME);
// 		System.out.println("data : "+data);
		BufferedWriter bw = null;
		FileWriter fw = null;
		String csvText ="";
		try {
			
			File file = new File(FILENAME);//"../Success/myfile.csv");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
				/*
				 * String[] result = headers.toArray(new String[0]);
				 * 
				 * for (String string : result) { csvText+=string+","; }
				 */		
				csvText+=headers+",Remarks\r\n"+data;
				data=csvText;
				
			}

			// true = append file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

			bw.write(data);

			//System.out.println("Appended to CSV.");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {
//				ex.printStackTrace();
				logger.error("writeCSV() error : "+ex.getMessage(), ex);

			}
		}
	}
}
