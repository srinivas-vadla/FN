package com.mig.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.mig.fileoperations.GenerateOutputs;

public class Worker extends Thread {
//	public static Logger debugLog = Logger.getLogger(Worker.class);
	static final Logger debugLog = Logger.getLogger("debugLogger");
	static final Logger resultLog = Logger.getLogger("reportsLogger");
	static int successCount = 0;
	static int failureCount = 0;
	public static ResourceBundle rb = ResourceBundle.getBundle("connection");
public boolean execute(List<HashMap<String, String>> processRecord, String successFile,String failureFile) {
	
	List<Integer> failureCountList  = new ArrayList<Integer>(); 
	int fCount=1;
//	debugLog.info(Thread.currentThread().getName()+":os : "+os.get_DisplayName());
	for (HashMap<String, String> hashMap : processRecord) {
		
		String fileName = "test";
			try {
				System.out.println("hashMap.get(\"Content\"):: "+hashMap.get("Content"));
				File file = new File(hashMap.get("Content"));
				if(file.exists()) {
				fileName=file.getName();
				}
				debugLog.info(Thread.currentThread().getName()+" - document ID :");
				String zipFolderPath = rb.getString("zipFolderPath");
				String[] zipFileCombo = rb.getString("zip_filenameCombination").split(",");
				String zipFilename = "";
				for(String str : zipFileCombo) {
					System.out.println(str+" -contains :"+hashMap.containsKey(str));
					if(hashMap.containsKey(str)) {
						
						zipFilename+= "_"+hashMap.get(str);
					}
				}
				zipFilename = zipFilename.substring(1);
				zipFilename = zipFilename.replace("{", "");
				zipFilename = zipFilename.replace("}", "");
				System.out.println("zip file name :"+zipFolderPath+zipFilename+".zip");
				  File zipFile = new File(zipFolderPath+zipFilename+".zip");  
				  FileOutputStream fos  = new FileOutputStream(zipFile);
				  ZipOutputStream zos = new ZipOutputStream(fos);
				 ZippingMultipleFiles zm = new ZippingMultipleFiles();
				 if(file.exists()) {String extension = "";
				 int i = fileName.lastIndexOf('.');
				 if (i > 0) {
				     extension = fileName.substring(i+1);
				 }
				 System.out.println("Extension : "+extension);
				 if(extension.equalsIgnoreCase("tiff") || extension.equalsIgnoreCase("tif")) {
					 System.out.println("tiff file found");
					 TiffToPDF ttp = new TiffToPDF();
					 ttp.convertToPDFandZip(file,zipFilename,zos);
					 
				 }else {
					 InputStream stream = new FileInputStream(file);
					 zm.zipFile(stream,zipFilename+getFileExtension(file), zos);						 						 
				 }
				 }
				 XMLGenerator xmlGen = new XMLGenerator();
				 InputStream xmlStream = xmlGen.getXMLStream(hashMap);
				 System.out.println("xml file name :"+zipFilename+".xml");
				 zm.zipFile(xmlStream,zipFilename+".xml", zos);
				 
//				zm.zipXMLFile(doc.getProperties());
			  zos.close();  
			  GenerateOutputs go = new GenerateOutputs();
			  
			  go.writeCSV(successFile, String.join(",",hashMap.values())+",success\r\n", String.join(",", hashMap.keySet()));
			  successCount = successCount +1;
//			  new SynchronizedCounter().successIncrement();
			  String res =Thread.currentThread().getName()+" - Total No of Records:"+WorkerManager.totalNoOfRecords+" :"
				  		+ " Total Success Count : "+successCount+" : Total Failure Count : "+failureCount;
			  System.out.println(res);
			  resultLog.info(res);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				debugLog.error(e.getMessage(), e);
				failureCountList.add(fCount);
				fCount = fCount+1;
				failureCount = failureCount+1;
				String res =Thread.currentThread().getName()+"Total No of Records:"+WorkerManager.totalNoOfRecords+" :"
				  		+ " Total Success Count : "+successCount+" : Total Failure Count : "+failureCount;
				System.out.println(res);
			  	resultLog.info(res);			  
				
				 GenerateOutputs go = new GenerateOutputs();				  
				 go.writeCSV(failureFile,String.join(",",hashMap.values())+","+e.getMessage().replaceAll(",", " ")+"\r\n",String.join(",", hashMap.keySet()));
			} 
			
	}
	return !failureCountList.isEmpty();
}

private String getFileExtension(File file) {
    String name = file.getName();
    int lastIndexOf = name.lastIndexOf(".");
    if (lastIndexOf == -1) {
        return ""; // empty extension
    }
    return name.substring(lastIndexOf);
}
private static ArrayList<HashMap<String, String>> processCSV(String filePath) {
	debugLog.info("processCSV() :: Processing :: "+filePath);
	Long startTime = System.currentTimeMillis();

	BufferedReader br = null;
	ArrayList<HashMap<String, String>> csvData = new ArrayList<HashMap<String,String>>();
	try {
	br = new BufferedReader(new FileReader(filePath));
    ArrayList<String> headers = new ArrayList<String>();
    String line = "";
    boolean headerRow = true;
    while((line=br.readLine())!=null){
    	String str[] = line.split(",");
    	
    	HashMap<String, String> map = new HashMap<String, String>();
        for(int i=0;i<str.length;i++){
            map.put(headers.get(i), str[i]);         
        }
     
        if(!headerRow){
        	csvData.add(map);
        }else{
        	for(int i=0;i<str.length;i++){
             headers.add(i, str[i]);
          }
        	headerRow = false;
        }
        
    }
	/*
	 * if(isHeadersValid(headers,className)){ CEOperations.doCEOperations(csvData,
	 * headers, className, folderName); }else{
	 * MAINPROGRAM_LOGGER.info("Please check log for mismatched Headers."); }
	 */

    debugLog.info("CSV Data size : "+csvData.size());
	}catch (FileNotFoundException e) {
//        e.printStackTrace();
        debugLog.error("processCSV() FileNotFound : "+e.getMessage(), e);
    } catch (IOException e) {
//        e.printStackTrace();
        debugLog.error("processCSV() IOException : "+e.getMessage(), e);
    }catch (Exception e) {
//        e.printStackTrace();
    	debugLog.error("processCSV() Error : "+e.getMessage(), e);
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
    
    debugLog.info("Total time taken for processing : "+filePath+"\n"+hours+" hr "+minutes+" min "+seconds+" sec");
	
    debugLog.info("\n*********************************\n");
    return csvData;
}

}
