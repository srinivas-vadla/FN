package com.mig.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class InputTxtFileReader {
	public static Logger MAINPROGRAM_LOGGER = Logger.getLogger(InputTxtFileReader.class);
	public static ResourceBundle rb = ResourceBundle.getBundle("connection");
	public ArrayList<HashMap<String, String>> readTxtFile(String filePath) {
		MAINPROGRAM_LOGGER.info("processCSV() :: Processing :: "+filePath);
		System.out.println("read txt file :"+filePath);
		Long startTime = System.currentTimeMillis();

		BufferedReader br = null;
		ArrayList<HashMap<String, String>> textData = new ArrayList<HashMap<String,String>>();
		try {
		br = new BufferedReader(new FileReader(filePath));
	    ArrayList<String> headers = new ArrayList<String>();
	    String line = "";
	    boolean headerRow = true;
	    while((line=br.readLine())!=null){
	    	HashMap<String, String> map = new HashMap<String, String>();
	    	String str[] = line.split(rb.getString("propertySplit"));	
	    	if(str.length>4) {
		    	map.put("operationType", str[0]);
		    	map.put("Doc_Class", str[1]);
		    	map.put("objectstore", str[2]);
		    	map.put("GUID", str[3]);
	    	}
	    	for (String propData : str) {
				String[] propKeyVal = propData.split(rb.getString("propValueSplit"));
				if(propKeyVal.length > 1) {
					if(propKeyVal[0].equalsIgnoreCase("Content")) {
						if(propKeyVal.length >=5) {
						map.put(propKeyVal[0], propKeyVal[3]+propKeyVal[4]);
						}else {
							map.put(propKeyVal[0], propKeyVal[3]);
						}
					}else {
						map.put(propKeyVal[0], propKeyVal[1]);
					}
				}
			}
	    	textData.add(map);
	    }

	    MAINPROGRAM_LOGGER.info("CSV Data size : "+textData.size());
		}catch (FileNotFoundException e) {
	        e.printStackTrace();
	        MAINPROGRAM_LOGGER.error("processCSV() FileNotFound : "+e.getMessage(), e);
	    } catch (IOException e) {
	        e.printStackTrace();
	        MAINPROGRAM_LOGGER.error("processCSV() IOException : "+e.getMessage(), e);
	    }catch (Exception e) {
	        e.printStackTrace();
	    	MAINPROGRAM_LOGGER.error("processCSV() Error : "+e.getMessage(), e);
	    } finally {
	        if (br != null) {
	            try {
	                br.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    Long endTime = System.currentTimeMillis();
	    
	    Long milliseconds = endTime-startTime;
	   
	    int seconds = (int) (milliseconds / 1000) % 60 ;
	    int minutes = (int) ((milliseconds / (1000*60)) % 60);
	    int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
	    
	    MAINPROGRAM_LOGGER.info("Total time taken for processing : "+filePath+"\n"+hours+" hr "+minutes+" min "+seconds+" sec");
		
	    MAINPROGRAM_LOGGER.info("\n*********************************\n");
	    return textData;
	}
	
	public static void main(String[] args) {
		InputTxtFileReader r = new  InputTxtFileReader();
		String filePath = "D:\\DP\\New migration\\SampleLoad_2.txt";
		ArrayList<HashMap<String, String>> readCSV = r.readTxtFile(filePath);
		System.out.println(readCSV.size());
		HashMap<String,String> subList = readCSV.get(0);
		for (Entry<String, String> entry : subList.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    System.out.println(key+" - "+value);
		}
	}
}
