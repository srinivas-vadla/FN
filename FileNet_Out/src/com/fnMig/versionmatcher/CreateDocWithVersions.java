package com.fnMig.versionmatcher;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.collection.VersionableSet;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.core.Versionable;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.util.Id;
import com.fnMig.connectors.Tgt_CEConnection;
import com.fnMig.connectors.Src_CEConnection;

public class CreateDocWithVersions {
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
	public static void getDocVersions(String vsid, ObjectStore srcOS) {
		Document doc = Factory.Document.fetchInstance(srcOS, new Id(vsid), null);	
		System.out.println(doc.getClassName());
		
		  Map<Integer, Document> docVersions = new HashMap<Integer, Document>();
		  VersionableSet versions = doc.get_Versions();
		  
		  int i=1; 
		  for (Iterator<Versionable> iterator = versions.iterator(); iterator.hasNext();) { 
			  Document d = (Document)iterator.next();
			  docVersions.put(i, d); 
			  i= i+1; 
		  }
		  Document newDoc = Factory.Document.createInstance(Tgt_CEConnection.get_ObjectStore(), doc.getClassName());
		  newDoc.save(RefreshMode.REFRESH);
//		  System.out.println(newDoc.get_Id());
		createdocWithVersions(Tgt_CEConnection.get_ObjectStore(),docVersions, newDoc);
		
	}


public static void createdocWithVersions(ObjectStore get_ObjectStore, Map<Integer, Document> docVersions, Document newDoc) {
//	Document newDoc = Factory.Document.createInstance(get_ObjectStore, docClassName);

   Id id = newDoc.get_Id();
   ArrayList<String> customPropNames = getCustomPropNames(newDoc.getClassName());
	System.out.println("no of versions : "+docVersions.size());
	for(int i=docVersions.size(); i >=1 ;i--) {
		System.out.println("i Val: "+i);
		Document docV = docVersions.get(i);	
		Properties properties = docV.getProperties();
		System.out.println( "version : "+docV.get_MajorVersionNumber());
		VersionSeries verSeries = newDoc.get_VersionSeries();
		Versionable version = verSeries.get_CurrentVersion();
		Document reservation = null;
		if(version == null) {
			// Check out the Document object and save it.
			if(!newDoc.get_IsReserved()) {
				newDoc.checkout(ReservationType.OBJECT_STORE_DEFAULT, null, null, null);
				newDoc.save(RefreshMode.REFRESH);
				System.out.println("checked out : isReserved : "+ newDoc.get_IsReserved());
			}
			// Get the reservation object from the Document object.
			 reservation = (Document) newDoc.get_Reservation();
			 if(!reservation.get_IsReserved()) {
				 newDoc = Factory.Document.getInstance(get_ObjectStore,newDoc.getClassName(), id);
				 newDoc.checkout(ReservationType.OBJECT_STORE_DEFAULT, null, null, null);
					newDoc.save(RefreshMode.REFRESH);
					VersionSeries verSeries1 = newDoc.get_VersionSeries();
					Versionable version2 = verSeries1.get_CurrentVersion();
//					System.out.println(version2.get_Reservation());
					System.out.println("checked out and isReserved : "+ newDoc.get_IsReserved());
//					reservation = (Document) newDoc.get_Reservation();
					reservation = (Document)version2.get_Reservation();
			 }
		}else {
		System.out.println("Status of current version: " + version.get_VersionStatus().toString() +
		   "\n Number of current version: " + version.get_MajorVersionNumber() +"."+ version.get_MinorVersionNumber() );
		// Check out the VersionSeries object and save it.
		verSeries.checkout(ReservationType.OBJECT_STORE_DEFAULT, null, null, null);
		verSeries.save(RefreshMode.REFRESH);
		 reservation = (Document) verSeries.get_Reservation();
		}
		
		copyProperties(docV.getProperties(),reservation.getProperties(),customPropNames);
		
		// Get the reservation object from the VersionSeries object.
		ContentElementList ctList = docV.get_ContentElements(); 
		Iterator<ContentTransfer> itr = ctList.iterator();
		while (itr.hasNext()) {
			ContentTransfer ct = itr.next();
		  InputStream iStream = ct.accessContentStream();
		 
		  try {
			   //Create a ContentTransfer object
			   ContentTransfer ctObject = Factory.ContentTransfer.createInstance();
			   
			   ContentElementList contentList = Factory.ContentTransfer.createList();
			   ctObject.setCaptureSource(iStream);
			   System.out.println(ct.get_ContentType());
			   ctObject.set_ContentType(ct.get_ContentType());
			   ctObject.set_RetrievalName(ct.get_RetrievalName());
			   
			   // Add ContentTransfer object to list and set on reservation
			   contentList.add(ctObject);
			   reservation.set_ContentElements(contentList);
			}
			catch (Exception e)
			{
			   System.out.println(e.getMessage() );
			}
		// Check in reservation object as major version.
		  reservation.checkin(null, CheckinType.MAJOR_VERSION);
		  
		  reservation.save(RefreshMode.REFRESH);	
		  id = reservation.get_Id();
		  } 
		// Print information about new version 2 of 2.
		version = verSeries.get_CurrentVersion();
		
		if(version != null) {
		  System.out.println("Status of current version: " + version.get_VersionStatus().toString() +
		     "\n Number of current version: " + version.get_MajorVersionNumber() +"."+ version.get_MinorVersionNumber() );
		}
//		newDoc.save(RefreshMode.REFRESH);
		System.out.println(id.toString());
		}
	}
private static void copyProperties(Properties srcProperties, Properties destProperties,ArrayList<String> propNames) {
	Iterator<Property> iterator = srcProperties.iterator();
	while (iterator.hasNext()) {
		Property property = iterator.next();
		String propertyName = property.getPropertyName();
		if(propNames.contains(propertyName)) {
			if(destProperties.isPropertyPresent(propertyName)) {
				System.out.println("setting property value for :"+property.getPropertyName());
				destProperties.putObjectValue(propertyName, property.getObjectValue());
			}
		} 
		
	}
	
}
public static ArrayList<String> getCustomPropNames(String className) {
	ArrayList<String> propNames = new ArrayList<String>();
	ClassDescription cd = Factory.ClassDescription.fetchInstance(Tgt_CEConnection.get_ObjectStore(), className, null);
	com.filenet.api.property.Properties properties = cd.getProperties();
	Set<String> props = new HashSet<String>(); 
	PropertyDescriptionList pl = cd.get_PropertyDescriptions();

	Iterator<PropertyDescription> itr = pl.iterator();
	while (itr.hasNext()) {
		PropertyDescription propertDesc =  itr.next();
		String str = propertDesc.get_SymbolicName(); 
				
		if(!propertDesc.get_IsSystemOwned()&& !propertDesc.get_IsHidden()&& !propertDesc.get_Name().equals("Publication Source") && !propertDesc.get_Name().equals("Publications") && !propertDesc.get_Name().equals("CmFederatedLockStatus") ){
			propNames.add(str);
		}	
		
	}
	return propNames;
}
public static void main(String[] args) {
	 getDocVersions("{909F6781-0000-C910-BB89-84D1CAC89315}",Src_CEConnection.get_ObjectStore());
}
}
