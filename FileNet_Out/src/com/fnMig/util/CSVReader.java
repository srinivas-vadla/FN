package com.fnMig.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.fnMig.constants.ECMConstants;

public class CSVReader {
	public static Logger MAINPROGRAM_LOGGER = Logger.getLogger(CSVReader.class);
	private static ResourceBundle bundle = ResourceBundle.getBundle(ECMConstants.CONFIG_PROPFILE_NAME);
	public static ArrayList<HashMap<String, String>> getCSVData(String filePath) {
		BufferedReader br = null;
		ArrayList<HashMap<String, String>> csvData = new ArrayList<HashMap<String,String>>();
		try {
		br = new BufferedReader(new FileReader(filePath));
        ArrayList<String> headers = new ArrayList<String>();
        String line = "";
        boolean headerRow = true;
        while((line=br.readLine())!=null){
        	String str[] = line.split(",");
        	
        	HashMap<String,String> map = new HashMap<String, String>();
                   
            if(!headerRow){
            	 for(int i=0;i<str.length;i++){
                     map.put(headers.get(i), str[i]);
                 } 
            	csvData.add(map);
            }else{
            	for(int i=0;i<str.length;i++){
                 headers.add(i, str[i]);
              }
            	headerRow = false;
            }
            
        }
		}catch (Exception e) {
			e.printStackTrace();
		}
		return csvData;
	}
	public static void main(String[] args) {
		ArrayList<HashMap<String, String>> csvData = CSVReader.getCSVData(bundle.getString("input_propertytemplates"));
		for (Iterator iterator = csvData.iterator(); iterator.hasNext();) {
			HashMap<String, String> hashMap = (HashMap<String, String>) iterator.next();
			for (Entry<String, String> entry : hashMap.entrySet()) {
				System.out.println(entry.getKey()+":"+entry.getValue());
			}
		}

	}

}
