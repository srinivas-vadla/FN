package com.mig.util;

import java.io.File;
//We need the library below to write the final 
//PDF file which has our image converted to PDF
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
public class TiffToPDF {
	static Logger debugLog = Logger.getLogger("debugLogger");
public static void tiffToPdf(){
  try{
      //Read the Tiff File
      RandomAccessFileOrArray myTiffFile=new RandomAccessFileOrArray("C:\\Users\\svadl\\Downloads\\file_example_TIFF_1MB.tiff");
      int numberOfPages=TiffImage.getNumberOfPages(myTiffFile);
      System.out.println("Number of Images in Tiff File :" + numberOfPages);
      if(numberOfPages == 1) {
	      Document TifftoPDF=new Document();
	      PdfWriter instance = PdfWriter.getInstance(TifftoPDF, new FileOutputStream("C:\\Users\\svadl\\Downloads\\tiff2Pdf.pdf"));
//	      PdfWriter.
	      TifftoPDF.open();
	     
//	      for(int i=1;i<=numberOfPages;i++){
	          Image tempImage=TiffImage.getTiffImage(myTiffFile, 1);
	          TifftoPDF.add(tempImage);
//	      }
	      TifftoPDF.close();
	      
      }else {
    	  System.out.println("multi tiff");
      }
      System.out.println("Tiff to PDF Conversion in Java Completed");
  }
  catch (Exception e){
	  debugLog.error(e.getMessage(), e);
  }      
  }
public void convertToPDFandZip(File file, String pdfFileName, ZipOutputStream zos ) {
	try {
		debugLog.info("convertToPDFandZip");
		// Read the Tiff File
		RandomAccessFileOrArray myTiffFile = new RandomAccessFileOrArray(file.getAbsolutePath());
		int numberOfPages = TiffImage.getNumberOfPages(myTiffFile);
		debugLog.info("Number of Images in Tiff File :" + numberOfPages);
//		String pdfFileName = file.getName();
//		pdfFileName = pdfFileName.substring(0,pdfFileName.lastIndexOf("."));
		if (numberOfPages == 1) {
			
			zos.putNextEntry(new ZipEntry(pdfFileName+".pdf"));
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, zos);
			writer.setCloseStream(false);
			document.open();
			Image tempImage = TiffImage.getTiffImage(myTiffFile, 1);
			document.add(tempImage);
			document.close();
			 zos.closeEntry();
		} else {
			System.out.println("multi tiff");
		} 
		System.out.println("Tiff to PDF Conversion in Java Completed");
	}
	  catch (Exception e){
		  debugLog.error(e.getMessage(), e);
		  e.printStackTrace();
	  } 
}    

}