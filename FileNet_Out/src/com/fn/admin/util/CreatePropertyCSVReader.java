package com.fn.admin.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.fn.admin.dao.PropTemplates;
import com.fnMig.constants.ECMConstants;

public class CreatePropertyCSVReader {
	public static Logger MAINPROGRAM_LOGGER = Logger.getLogger(CreatePropertyCSVReader.class);
	private static ResourceBundle bundle = ResourceBundle.getBundle(ECMConstants.CONFIG_PROPFILE_NAME);
	public static HashMap<String, ArrayList<PropTemplates>> getCSVData(String filePath) {
		BufferedReader br = null;
		HashMap<String, ArrayList<PropTemplates>> propCSVData = new HashMap<String, ArrayList<PropTemplates>>();
		try {
		br = new BufferedReader(new FileReader(filePath));
        ArrayList<String> headers = new ArrayList<String>();
        String line = "";
        boolean headerRow = true;
        while((line=br.readLine())!=null){
        	String str[] = line.split(",");
        	PropTemplates propTemps = new PropTemplates();
                   
            if(!headerRow){
            	 for(int i=0;i<str.length;i++){
                     propTemps.setProperty(headers.get(i), str[i]);
                 } 
            	 if(propCSVData.containsKey(propTemps.getDocClass())) {
            		ArrayList<PropTemplates> propsList = propCSVData.get(propTemps.getDocClass());
            		propsList.add(propTemps);
            		propCSVData.put(propTemps.getDocClass(), propsList);
            	 }else {
            		 ArrayList<PropTemplates> propsList = new ArrayList<PropTemplates>();
             		propsList.add(propTemps);
             		propCSVData.put(propTemps.getDocClass(), propsList);
            	 }
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
		return propCSVData;
	}
	public static void main(String[] args) {
//		Doc_Class	Prop_DisplayName	Prop_SymbolicName	Prop_Description	Datatype	Cardinality	isRequired_on_Class	length

		HashMap<String, ArrayList<PropTemplates>> csvData = CreatePropertyCSVReader.getCSVData(bundle.getString("input_propertytemplates"));
		ArrayList<PropTemplates> list = csvData.get("b");
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			PropTemplates propTemplates = (PropTemplates) iterator.next();
			System.out.println(propTemplates.toString());
		}
		System.out.println(list.size());
		
		/*
		 * for (Iterator iterator = csvData.iterator(); iterator.hasNext();) {
		 * HashMap<String, String> hashMap = (HashMap<String, String>) iterator.next();
		 * for (Entry<String, String> entry : hashMap.entrySet()) {
		 * System.out.println(entry.getKey()+":"+entry.getValue()); } }
		 */

	}

}
