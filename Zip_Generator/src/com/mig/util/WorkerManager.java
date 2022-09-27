package com.mig.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.mig.fileoperations.GenerateOutputs;

import java.util.ResourceBundle;

public class WorkerManager implements Runnable {
	public static Logger resultLog = Logger.getLogger("reportsLogger");
	static final Logger debugLog = Logger.getLogger("debugLogger");
	public static ResourceBundle rb = ResourceBundle.getBundle("connection");
	public static int totalNoOfRecords = 0; 
	public static List<Integer> successRecords = new ArrayList<Integer>();
	public static List<Integer> failureRecords = new ArrayList<Integer>();
	List<HashMap<String, String>> csvSubList;
	File srcFile = null;
	public static String successFile = rb.getString("successCSVFilepath").replace("tmpstmp", String.valueOf(System.currentTimeMillis()));
	public static String failureFile = rb.getString("failureCSVFilepath").replace("tmpstmp", String.valueOf(System.currentTimeMillis()));
	public WorkerManager(List<HashMap<String, String>> subList, File file) {
		csvSubList = subList;
		srcFile = file;
	}	
	public void run() {
            debugLog.info(Thread.currentThread().getName()+" Started. Total No of records to process : "+csvSubList.size() );
            Worker worker = new Worker();
            boolean isFailuresFound = worker.execute(csvSubList, successFile, failureFile);
            if(isFailuresFound) {
            	srcFile.renameTo(new File(rb.getString("failureFolderPath")+srcFile.getName()));
            }else {
            	srcFile.renameTo(new File(rb.getString("processedFolderPath")+srcFile.getName()));	
            }
    }
	public static void main(String[] args) {
//		Scanner sc= new Scanner(System.in); //System.in is a standard input stream.
//		System.out.print("How Many Batches you want to Run:  ");
		
		String filePath  = rb.getString("inputFilePath");
		int batchSize= Integer.parseInt(rb.getString("batchSize"));
		File fileFolderPath = new File(filePath);
		File[] filesList = fileFolderPath.listFiles();
		for (File file : filesList) {
			System.out.println("No of Batches Configured : "+batchSize);
			
			InputTxtFileReader csvR = new InputTxtFileReader();
			ArrayList<HashMap<String, String>> csvData = csvR.readTxtFile(file.getAbsolutePath());
			 int noOfRecords = csvData.size();
			 totalNoOfRecords = totalNoOfRecords+noOfRecords;
			 if(noOfRecords > 0) {
				 GenerateOutputs go = new GenerateOutputs();
				 HashMap<String, String> hashMap = csvData.get(0);
				  go.writeCSV(successFile, "", String.join(",", hashMap.keySet()));
				  go.writeCSV(failureFile, "", String.join(",", hashMap.keySet()));
			 }
			System.out.println("Total No of Records : "+noOfRecords);
			int splitSize =  (noOfRecords/batchSize)+1;
//			System.out.println("splitsize : "+splitSize);

			  Thread[] batches = new Thread [batchSize];
			  int starter = 0;
		        for(int b =0; b < batches.length; b++){
		        	int splitter = starter+splitSize;
		        	if(splitter >=noOfRecords) {
		        		splitter = noOfRecords ;
		        	}
		        	debugLog.info(starter+" - "+ splitter);
		        	List<HashMap<String, String>> subList = csvData.subList(starter, splitter);
		            batches[b] = new Thread(new WorkerManager(subList,file));
		            batches[b].setName("Batch No:"+(b+1));
		            batches[b].start();
		            starter = splitter;
					
		        }
		}
		
	        
	        
	}

}

