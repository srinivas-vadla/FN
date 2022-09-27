package com.mig.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class XMLGenerator {
	static ResourceBundle rb = ResourceBundle.getBundle("connection");
public  InputStream getXMLStream(HashMap<String, String> hashMap) {
	System.out.println("Inside XML creation");
	File log= new File(rb.getString("xmlTemplate"));
	String[] xml_Placeholders = rb.getString("xml_Placeholders").split(",");
	//file reading
	try {
	FileReader fr = new FileReader(log);
	String s;
	    BufferedReader br = new BufferedReader(fr);
	    StringBuilder sb = new StringBuilder();
	    while ((s = br.readLine()) != null) {
//	    	String loanNumber = props.getStringValue("loanNumber");
	    	for(String placeHolder : xml_Placeholders) {
	    		if(s.contains("#"+placeHolder+"#")) {
	    			if(placeHolder.equalsIgnoreCase("ID")) {
	    				String id = hashMap.get("GUID");
	    				id = id.substring(1, id.length()-1);
	    				s= s.replace("#"+placeHolder+"#", id);
	    				
	    			}else {
	    				if(s.contains(rb.getString("filename_wordphrase")) && placeHolder.equalsIgnoreCase(rb.getString("filename_docType"))) {
	    					String replacedString = hashMap.get(placeHolder).replaceAll("[^a-zA-Z0-9]", "_");
	    					s= s.replace("#"+placeHolder+"#", replacedString);
	    				}else {
	    					s= s.replace("#"+placeHolder+"#", hashMap.get(placeHolder));
		    			}
	    			}
	    		}
	    	}
//	    	System.out.println(s);
//	        String replace = s.replace("#loanNumber#", loanNumber);
	        sb.append(s+"\n");
	        // do something with the resulting line
	    }
	    Stream<String> lines = br.lines();
	    InputStream targetStream = new ByteArrayInputStream(sb.toString().getBytes());
	    return targetStream;
	}catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

}
