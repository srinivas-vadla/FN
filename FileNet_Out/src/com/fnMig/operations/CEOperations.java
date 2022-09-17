package com.fnMig.operations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.property.Properties;
import com.filenet.api.util.Id;
import com.fnMig.MainProgram;
import com.fnMig.connectors.Tgt_CEConnection;
import com.fnMig.connectors.Src_CEConnection;
import com.fnMig.constants.ECMConstants;

public class CEOperations {
	
public static ResourceBundle bundle = ResourceBundle.getBundle(ECMConstants.CONFIG_PROPFILE_NAME);
	public static Logger CEOPERATIONS_LOGGER = Logger.getLogger(Tgt_CEConnection.class);
	@SuppressWarnings("deprecation")
	public static void doCEOperations(
			ArrayList<HashMap<String, String>> csvData,
			Set<String> headers) {
		CEOPERATIONS_LOGGER.info("Entered into CEOperations. \n no.of records we found in CSV : "+csvData.size());
		
		try {
			String  className = "";
			Long docCECommitStartTime = System.currentTimeMillis();
			String successCSVFilepath = bundle.getString("successCSVFilepath");
			String failureCSVFilepath = bundle.getString("failureCSVFilepath");
			HashMap<String, Integer> propAndDataType = null;//getPorpAndDataType(className);
			
			int successCount=0;
			int failureCount=0;
			for (Iterator<HashMap<String, String>> iterator = csvData.iterator(); iterator.hasNext();) {
				String csvText = "";
						try {
							HashMap<String, String> hashMap = (HashMap<String, String>) iterator.next();
					        String cName = hashMap.get("Doc_Class");
					        if(!className.equalsIgnoreCase(cName)) {	
					        	className = cName;
								propAndDataType = getPorpAndDataType(className);								
					        }
							Document document = Factory.Document.createInstance(Tgt_CEConnection.get_ObjectStore(), className);
							csvText = "";
							Folder folder = null;
							String[] result = hashMap.values().toArray(new String[0]);
							
							for (String string : result) {
								csvText+=string+",";
							}			
							for (Map.Entry<String, String> entry : hashMap.entrySet())
							{
								
								Properties props = document.getProperties();
							    String propName = entry.getKey();//headers.get(entry.getKey()-1);
							    String value = entry.getValue() != null ? entry.getValue().replaceAll("#COMMA#", ",") : null;
							    if(propName.equalsIgnoreCase("FilePath")){
							    	if(value != null && value.length() > 0) {
							    		ContentElementList tgt_ContentElements = Factory.ContentTransfer.createList();
							    		if(Id.isId(value)) {
							    			Document srcDoc = Factory.Document.fetchInstance(Src_CEConnection.get_ObjectStore(), new Id(value), null);
									    	ContentElementList src_ContentElements = srcDoc.get_ContentElements();
											 Iterator<ContentTransfer> itrCt = src_ContentElements.iterator();
											 while (itrCt.hasNext()) {
												 ContentTransfer ct = itrCt.next();
												 String content_Name = ct.get_RetrievalName();
												 InputStream stream = ct.accessContentStream();
												 ContentTransfer ctObject = Factory.ContentTransfer.createInstance();
												 ctObject.setCaptureSource(stream);
												 ctObject.set_RetrievalName(ct.get_RetrievalName());
												 ctObject.set_ContentType(ct.get_ContentType());
												 tgt_ContentElements.add(ctObject);
												 
											}
							    		}else {
							    			// First, add a ContentTransfer object.
											
											  ContentTransfer ctObject = Factory.ContentTransfer.createInstance();
											  FileInputStream fileIS = new FileInputStream(value);
											  ctObject.setCaptureSource(fileIS);
											  ctObject.set_RetrievalName(value.substring(value.lastIndexOf("\\")+1));
											  // Add ContentTransfer object to list. 
											  tgt_ContentElements.add(ctObject);											 
							    		}
							    		 document.set_ContentElements(tgt_ContentElements);
							    	}
							    }else if(propName.equalsIgnoreCase("Folder Filed In")){
							    	folder = getFolder(value);
							    }else if(Arrays.asList(bundle.getString("skipValueFromCECommital").split(",")).contains(propName)){
							    	// do nothing.
							    }else {
							    	if(value!=null || value.length()>0){	
//							    		System.out.println(propName+" Value : "+value+" dt "+porpAndDataType.get(propName));
							    		if(propName.equalsIgnoreCase("DocumentTitle")){
							    			insertIntoProps(props, propName, value,8);
							    		}else if(props.isPropertyPresent(propName)) {
							    			insertIntoProps(props, propName, value,propAndDataType.get(propName));
							    		}
//							    		}
							    	}
							    }
							 
							}
							
							// Check in the document.
							document.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
							document.save(RefreshMode.REFRESH);
							// File the document.
//						    Folder folder = Factory.Folder.getInstance(CEConnection.get_ObjectStore(), ClassNames.FOLDER,
//						    		folderName );
							if(folder != null) {
						    ReferentialContainmentRelationship rcr = folder.file(document,
						            AutoUniqueName.AUTO_UNIQUE, null,
						            DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
						    rcr.save(RefreshMode.NO_REFRESH);
							}
						    successCount++;
						    CEOPERATIONS_LOGGER.info("Success Record No :"+successCount+" Doc GUID : "+document.get_Id());
						    
						    writeCSV(successCSVFilepath,csvText+"success,"+document.get_Id()+"\r\n",headers);
						} catch (EngineRuntimeException e) {	
							failureCount++;
//							e.printStackTrace();
							CEOPERATIONS_LOGGER.error("doCeOperations() :: EngineRuntimeException :: "+e.getMessage(),e);
							writeCSV(failureCSVFilepath,csvText+e.getMessage().replaceAll(",", " ")+"\r\n",headers);
						}catch (Exception e) {		
							failureCount++;
//							e.printStackTrace();
							CEOPERATIONS_LOGGER.error("Error from doCeOperations :: "+e.getMessage(),e);
							writeCSV(failureCSVFilepath,csvText+e.getMessage().replaceAll(",", " ")+"\r\n",headers);
						}
						
			        }
			Long docCECommitEndTime = System.currentTimeMillis();
			Long milliseconds = docCECommitEndTime - docCECommitStartTime;
			
			int seconds = (int) (milliseconds / 1000) % 60 ;
	        int minutes = (int) ((milliseconds / (1000*60)) % 60);
	        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
	        
	        CEOPERATIONS_LOGGER.info("Total time commiting documents to filenet : "+hours+" hr "+minutes+" min "+seconds+" sec\n"+csvData.size()+" Documents");
	        CEOPERATIONS_LOGGER.info("Successfullrecords Count : "+successCount);
	        CEOPERATIONS_LOGGER.info("failureCount Count : "+failureCount);
		} catch (Exception e) {
//			e.printStackTrace();
			CEOPERATIONS_LOGGER.error("doCEOperations() final error : "+e.getMessage(),e);
		}
	}
	public static HashMap<String, Integer>getPorpAndDataType(String clsName) {
		Long datatypeTime = System.currentTimeMillis();
		CEOPERATIONS_LOGGER.info("Entered into getting Property Datatypes");
		// TODO Auto-generated method stub

		 HashMap<String, Integer> map=null;
	
	
	ClassDefinition cls= Factory.ClassDefinition.fetchInstance(Tgt_CEConnection.get_ObjectStore(), clsName, null);

	PropertyDefinitionList get_PropertyDefinitions = cls.get_PropertyDefinitions();

	Iterator<PropertyDefinition> iterator = get_PropertyDefinitions.iterator();
	map =new HashMap<String, Integer>();	
	while (iterator.hasNext()) {
		PropertyDefinition object =  iterator.next();		
		
		if(!object.get_IsSystemOwned()&& !object.get_IsHidden()&& !object.get_Name().equals("Publication Source") && !object.get_Name().equals("Publications") && !object.get_Name().equals("CmFederatedLockStatus") ){
//			String get_DisplayName = object.get_SymbolicName();
//			int value = object.get_DataType().getValue();
		
//		System.out.println(value+get_DisplayName);
		
		map.put(object.get_SymbolicName(), object.get_DataType().getValue());
		}	
		
		
		}
	Long endOdDatatypetime = System.currentTimeMillis();
	Long milliseconds = endOdDatatypetime - datatypeTime;
	
	int seconds = (int) (milliseconds / 1000) % 60 ;
    int minutes = (int) ((milliseconds / (1000*60)) % 60);
    int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
    CEOPERATIONS_LOGGER.info(hours+" hr "+minutes+" min "+seconds+" sec\t End of getting Property Datatypes");
	return map;
}

	private static void insertIntoProps(Properties props,String propName, String value,
			int dataType)throws Exception {
			
		switch (dataType) {
		case TypeID.STRING_AS_INT:
			props.putValue(propName, value);
			break;
		case TypeID.BOOLEAN_AS_INT:
			
			props.putValue(propName, Boolean.parseBoolean(value));
			break;
		case TypeID.DATE_AS_INT:
			//6/6/2017  11:37:00 AM
			SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			try {
					props.putValue(propName, sd.parse(value));
				} catch (ParseException e) {
					SimpleDateFormat sd1 = new SimpleDateFormat("MM/dd/yyyy HH:mm");
					Date dt1 =sd1.parse(value);
					String newDate = sd.format(dt1);
					props.putValue(propName, sd.parse(newDate));
//					e.printStackTrace();
				}
			break;
		case TypeID.LONG_AS_INT:
			props.putValue(propName, Integer.parseInt(value));
			break;
		case TypeID.DOUBLE_AS_INT:
			props.putValue(propName, Float.parseFloat(value));
			break;
		case TypeID.OBJECT_AS_INT:
			props.putValue(propName, value);
			break;
		default:
			System.out.println("Default Switch : propertyName : "+propName+"; Value : "+value);
			break;
		}
		
	}


	private static void writeCSV(String FILENAME, String data, Set<String> headers){
		CEOPERATIONS_LOGGER.info("into writeCSV : "+FILENAME);
// 		System.out.println("data : "+data);
		BufferedWriter bw = null;
		FileWriter fw = null;
		String csvText ="";
		try {
			
			File file = new File(FILENAME);//"../Success/myfile.csv");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
				String[] result = headers.toArray(new String[0]);
				
				for (String string : result) {
					csvText+=string+",";
				}			
				csvText+="Remarks,GUID\r\n"+data;
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
				CEOPERATIONS_LOGGER.error("writeCSV() error : "+ex.getMessage(), ex);

			}
		}
	}
	public static Folder getFolder(String folderPath){
		com.filenet.api.core.Folder subFolder = null;
		try{
			Folder folder = Factory.Folder.fetchInstance(Tgt_CEConnection.get_ObjectStore(),"/"+folderPath, null);
			return folder;
			}catch (EngineRuntimeException e) {
				
				if(e.getExceptionCode().getErrorId().equalsIgnoreCase("FNRCE0051")){
					Folder rootFolder = Tgt_CEConnection.get_ObjectStore().get_RootFolder();
					String[] folders = folderPath.split("/");
					String existingFolderPath = "";
					for(int i=0;i<folders.length;i++){
						
						try {
//							System.out.println("1 ->"+folders[i]+" - "+rootFolder.get_FolderName());
							existingFolderPath+="/"+folders[i];

							Folder folder = Factory.Folder.fetchInstance(Tgt_CEConnection.get_ObjectStore(),"/"+existingFolderPath, null);
							rootFolder = folder;
						} catch (Exception e1) {
							
//								System.out.println("2 ->"+folders[i]);
								subFolder= rootFolder.createSubFolder(folders[i]);
								subFolder.save(RefreshMode.REFRESH);
//							System.out.println(subFolder.get_PathName());
								rootFolder = subFolder;						
						
						}
					}	
					
				}
			
			return subFolder;		
			}
			
	}
public static void main(String[] args) {
	String str = "{10048982-0000-C014-8D45-89EE2326E313123}";
//	Id id = new Id();
	System.out.println(Id.isId(str));
}	
}
