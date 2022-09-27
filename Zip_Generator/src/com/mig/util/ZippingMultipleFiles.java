package com.mig.util;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;  
public class ZippingMultipleFiles   
{  
	static final Logger debugLog = Logger.getLogger("debugLogger");
  public static void main(String[] args)   
  {  
    try   
    {  
      //Source files  
    String fileName1 = "C:\\Users\\svadl\\Downloads\\2341305_Anti Bribery and Corruption Training - Part 2_Completion_Certificate.pdf";  
      String fileName2 = "C:\\Users\\Anubhav\\Desktop\\file2.txt";  
      //Zipped file  
      String zipFilename = "C:\\Users\\svadl\\OneDrive\\Desktop\\Zipper\\Allfiles.zip";  
      File zipFile = new File(zipFilename);  
      FileOutputStream fos  = new FileOutputStream(zipFile);              
      ZipOutputStream zos = new ZipOutputStream(fos);  
      File file = new File(fileName1);  
      FileInputStream fis = new FileInputStream(file);
      ZippingMultipleFiles zm = new ZippingMultipleFiles();
      zm.zipFile(fis,"file1", zos);  
//      zipFile(fis,"file2", zos);  
      zos.close();  
//      System.out.println("completed");
      
    }   
    catch (Exception e)   
    {  
    	debugLog.error(e.getMessage(), e); 
    }  
  }  
  
  // Method to zip file  
  public void zipFile(InputStream fis1,String fileName, ZipOutputStream zos) throws IOException  
  {  
    final int BUFFER = 1024;  
    BufferedInputStream bis = null;  
    try  
    {  
//      File file = new File("C:\\Users\\svadl\\Downloads\\2341305_Anti Bribery and Corruption Training - Part 2_Completion_Certificate.pdf");  
//      FileInputStream fis = new FileInputStream(file);  
      bis = new BufferedInputStream(fis1, BUFFER);            
  
      // ZipEntry --- Here file name can be created using the source file  
      ZipEntry zipEntry = new ZipEntry(fileName);          
      zos.putNextEntry(zipEntry);  
      byte data[] = new byte[BUFFER];  
      int count;  
      while((count = bis.read(data, 0, BUFFER)) != -1)   
      {  
        zos.write(data, 0, count);  
      }    
      // close entry every time  
      zos.closeEntry();  
    }   
    finally  
    {  
      try   
      {  
        bis.close();  
      }   
      catch (IOException e)   
      {  
    	  debugLog.error(e.getMessage(), e); 
      }    
    }  
  }

 
}  