package com.fnMig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fnMig.connectors.Tgt_CEConnection;
import com.fnMig.constants.ECMConstants;
import com.fnMig.operations.CEOperations;
import com.fnMig.util.CSVReader;

public class MainProgram {
	private static ResourceBundle bundle = ResourceBundle.getBundle(ECMConstants.CONFIG_PROPFILE_NAME);
	public static Logger MAINPROGRAM_LOGGER = Logger.getLogger(Tgt_CEConnection.class);
	private static void execute() {
		String filePath = bundle.getString("inputCSVFilePath");
		MAINPROGRAM_LOGGER.info("processCSV() :: Processing :: "+filePath);
		Long startTime = System.currentTimeMillis();
		try {
		
        ArrayList<HashMap<String, String>> csvData = CSVReader.getCSVData(bundle.getString("inputCSVFilePath"));
        
        if(csvData.size() > 0) {
        	HashMap<String, String> hashMap = csvData.get(0);
        	Set<String> headers = hashMap.keySet();
        CEOperations.doCEOperations(csvData, headers);
        }else {
        	MAINPROGRAM_LOGGER.info("There is no data in Input File");
        }

        MAINPROGRAM_LOGGER.info("CSV Data size : "+csvData.size());
		}catch (Exception e) {
//            e.printStackTrace();
        	MAINPROGRAM_LOGGER.error("execute() Error : "+e.getMessage(), e);
        } 
        Long endTime = System.currentTimeMillis();
        
        Long milliseconds = endTime-startTime;
       
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
        
        MAINPROGRAM_LOGGER.info("Total time taken for processing : "+filePath+"\n"+hours+" hr "+minutes+" min "+seconds+" sec");
		
        MAINPROGRAM_LOGGER.info("\n*********************************\n");
	}
	

	public static void main(String[] args) {
		execute();
	}
}
