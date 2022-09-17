package com.fn.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.LocalizedString;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyDefinitionFloat64;
import com.filenet.api.admin.PropertyDefinitionInteger32;
import com.filenet.api.admin.PropertyDefinitionObject;
import com.filenet.api.admin.PropertyDefinitionString;
import com.filenet.api.admin.PropertyTemplate;
import com.filenet.api.admin.PropertyTemplateInteger32;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.constants.Cardinality;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Factory;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.fn.admin.dao.PropTemplates;
import com.fn.admin.util.CreatePropertyCSVReader;
import com.fnMig.connectors.Src_CEConnection;
import com.fnMig.constants.ECMConstants;

public class CreatePropertyTemplates {
	private static ResourceBundle bundle = ResourceBundle.getBundle(ECMConstants.CONFIG_PROPFILE_NAME);
	private static String strDatatype = bundle.getString("StringDatatype");
	private static String intDatatype = bundle.getString("IntegerDatatype");
	private static String boolDatatype = bundle.getString("BooleanDatatype");
	private static String objDatatype = bundle.getString("ObjectDatatype");
	private static String datetimeDatatype = bundle.getString("DateTimeDatatype");
	private static String floatDatatype = bundle.getString("floatDatatype");
	private static String binaryDatatype = bundle.getString("binaryDatatype");
	private static String cardinality_Single = bundle.getString("Cardinality_Sigle");
	private static String cardinality_List = bundle.getString("Cardinality_List");
	private static ArrayList<String> propList = new ArrayList<String>();
	public static void main(String[] args) {
		try {
			HashMap<String, ArrayList<PropTemplates>> csvData = CreatePropertyCSVReader.getCSVData(bundle.getString("input_propertytemplates"));
			if(!csvData.isEmpty()) {
				run(csvData);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void run(HashMap<String, ArrayList<PropTemplates>> csvData) {
		Src_CEConnection ceCon = new Src_CEConnection();
		ObjectStore os = ceCon.get_ObjectStore();
		CreatePropertyTemplates  pdc = new CreatePropertyTemplates();
		
		  for (Entry<String, ArrayList<PropTemplates>> entry : csvData.entrySet()) {
			  ClassDefinition classDef = null;
			  PropertyDefinitionList propDefs = null;
			  String documentClass = entry.getKey();
			  ArrayList<PropTemplates> arList = entry.getValue();
			  System.out.println(documentClass+":"+arList.size());
			  try {
				  classDef = Factory.ClassDefinition.fetchInstance(os, documentClass, null);
				   propDefs = classDef.get_PropertyDefinitions();
				   getPropertiesNames(propDefs);
			  }catch(EngineRuntimeException e) {
				  System.out.println(e.getMessage());
			  }
			  if(classDef == null) {
				  System.out.println("class doesnt exist");
			  }
			  for (Iterator iterator = arList.iterator(); iterator.hasNext();) {
				  
					PropTemplates propData = (PropTemplates) iterator.next();
					PropertyTemplate propTemplate = pdc.fetchPropTemplate(os, propData.getPropSymbolicName());
					
     				if (propTemplate == null) {
				  		 propTemplate = pdc.createProperty(os, propData);
//				  		propTemplate.set_Settability(PropertySettability.READ_WRITE);
     				} else {
     					 System.out.println("property already Available: "+propData.getPropSymbolicName());
					}
     				if(classDef!=null && propDefs !=null && propTemplate!= null) {
     					if(propData.getDataType().equalsIgnoreCase(strDatatype)) {
     						PropertyDefinitionString newPropDef = (PropertyDefinitionString) propTemplate.createClassProperty();
     						newPropDef.set_IsValueRequired(propData.getIsRequired_on_Class());
     						newPropDef.set_MaximumLengthString(propData.getLength());
     						if(!propList.contains(propData.getPropSymbolicName())) {
     							propDefs.add(newPropDef);     							
     						}
     						
     					}else if(propData.getDataType().equalsIgnoreCase(intDatatype)) {
     						PropertyDefinitionInteger32 newPropDef = (PropertyDefinitionInteger32) propTemplate.createClassProperty();
     						newPropDef.set_IsValueRequired(propData.getIsRequired_on_Class());
     						newPropDef.set_PropertyMaximumInteger32(propData.getLength());
     						if(!propList.contains(propData.getPropSymbolicName())) {
     							propDefs.add(newPropDef);     							
     						}
     						
     					}else if(propData.getDataType().equalsIgnoreCase(floatDatatype)) {
     						PropertyDefinitionFloat64 newPropDef = (PropertyDefinitionFloat64) propTemplate.createClassProperty();
     						newPropDef.set_IsValueRequired(propData.getIsRequired_on_Class());
     						newPropDef.set_PropertyMaximumFloat64(propData.getFloatLength());
     						if(!propList.contains(propData.getPropSymbolicName())) {
     							propDefs.add(newPropDef);     							
     						}
     						
     					}else if(propData.getDataType().equalsIgnoreCase(objDatatype)) {
     						PropertyDefinitionObject newPropDef = (PropertyDefinitionObject) propTemplate.createClassProperty();
     						newPropDef.set_IsValueRequired(propData.getIsRequired_on_Class());
     						newPropDef.set_RequiredClassId(propData.getRequiredClassID());
     						if(!propList.contains(propData.getPropSymbolicName())) {
     							propDefs.add(newPropDef);     							
     						}
     						
     					}else {
     						PropertyDefinition newPropDef = propTemplate.createClassProperty();
     						newPropDef.set_IsValueRequired(propData.getIsRequired_on_Class());
     						System.out.println(propData.getPropSymbolicName()+" available: "+propList.contains(propData.getPropSymbolicName()));
     						if(!propList.contains(propData.getPropSymbolicName())) {
     							propDefs.add(newPropDef);     							
     						}
     					}
						}
				}
			  if(classDef !=null) {
				  classDef.save(RefreshMode.NO_REFRESH);
			  }
	 
		  }
	}
	
	private static void getPropertiesNames(PropertyDefinitionList propDefs) {
		for (Iterator iterator = propDefs.iterator(); iterator.hasNext();) {
			PropertyDefinition object = (PropertyDefinition) iterator.next();
			propList.add(object.get_SymbolicName());			
		}
		
	}
	public PropertyTemplate fetchPropTemplate(ObjectStore os, String propSymbolicName) {
		SearchSQL sqlObject = new SearchSQL("SELECT This FROM PropertyTemplate WHERE (SymbolicName = '"+propSymbolicName+"')");        
		PropertyTemplate propTemplate = null;	    
		SearchScope searchScope = new SearchScope(os);            
		IndependentObjectSet fetchObjects = searchScope.fetchObjects(sqlObject, null, null,  true);
		for (Iterator iterator = fetchObjects.iterator(); iterator.hasNext();) {
			IndependentObject type = (IndependentObject) iterator.next();
			propTemplate = (PropertyTemplate) type;			
		}
		return propTemplate;
	}
	public PropertyTemplate createProperty(ObjectStore os, PropTemplates propData)
	{
		
		String dataType = propData.getDataType();
		String cardinality = propData.getCardinality();
	    PropertyTemplate newPropTemplate = null;
	    try {
			if(dataType.equalsIgnoreCase(strDatatype)) {
				newPropTemplate = Factory.PropertyTemplateString.createInstance(os);	    	
			}else if(dataType.equalsIgnoreCase(intDatatype)) {
				newPropTemplate = Factory.PropertyTemplateInteger32.createInstance(os);	    	
			}else if(dataType.equalsIgnoreCase(boolDatatype)) {
				newPropTemplate = Factory.PropertyTemplateBoolean.createInstance(os);	    	
			}else if(dataType.equalsIgnoreCase(objDatatype)) {
				newPropTemplate = Factory.PropertyTemplateObject.createInstance(os);	    	
			}else if(dataType.equalsIgnoreCase(datetimeDatatype)) {
				newPropTemplate = Factory.PropertyTemplateDateTime.createInstance(os);	    	
			}else if(dataType.equalsIgnoreCase(floatDatatype)) {
				newPropTemplate = Factory.PropertyTemplateFloat64.createInstance(os);	    	
			}else if(dataType.equalsIgnoreCase(binaryDatatype)) {
				newPropTemplate = Factory.PropertyTemplateBinary.createInstance(os);	    	
			}
			if(cardinality.equalsIgnoreCase(cardinality_Single)) {
				newPropTemplate.set_Cardinality (Cardinality.SINGLE);	    	
			}else if(cardinality.equalsIgnoreCase(cardinality_List)) {
				newPropTemplate.set_Cardinality (Cardinality.LIST);
			}
			newPropTemplate.set_SymbolicName(propData.getPropSymbolicName());
			
			// Set required properties to locale-specific string.
			LocalizedString locStr1 = getLocalizedString(propData.getPropDisplayName(), os.get_LocaleName() );
			// Create LocalizedString collection.
			newPropTemplate.set_DisplayNames (Factory.LocalizedString.createList() );
			newPropTemplate.get_DisplayNames().add(locStr1);

			LocalizedString locStr2 = getLocalizedString(propData.getPropDescription(),os.get_LocaleName());
			newPropTemplate.set_DescriptiveTexts(Factory.LocalizedString.createList() );
			newPropTemplate.get_DescriptiveTexts().add(locStr2);

			// Save property template to the server.
			newPropTemplate.save(RefreshMode.REFRESH);
			System.out.println("propertyTemplate created:"+newPropTemplate.get_DisplayName()+" - "+newPropTemplate.get_Id());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return newPropTemplate;
	    // Create property definition from property template.
		
//		PropertyDefinitionObject newPropDef = (PropertyDefinitionObject) newPropTemplate.createClassProperty();
//		  PropertyDefinitionString y = null;
		 
	    
		/*
		 * // Set RequiredClass property to Email Items. //
		 * newPropDef.set_RequiredClassId(new
		 * Id("{BFA64F40-5C45-45B1-B540-B5BA3CA08AAB}") );
		 * 
		 * // Get PropertyDefinitions property from the property cache.
		 * PropertyDefinitionList propDefs = classDef.get_PropertyDefinitions();
		 * 
		 * // Add new property definition to class definition. propDefs.add(newPropDef);
		 * 
		 * classDef.save(RefreshMode.REFRESH);
		 */
	}
	
	public void createIntegerProperty(ObjectStore os, String propSymbolicName, String propDisplayName, String propDescription, String dataType, String cardinality)
	{
	    PropertyTemplateInteger32 newPropTemplate = Factory.PropertyTemplateInteger32.createInstance(os);
	    if(cardinality.equalsIgnoreCase("SINGLE")) {
	    	newPropTemplate.set_Cardinality (Cardinality.SINGLE);	    	
	    }else if(cardinality.equalsIgnoreCase("List")) {
	    	newPropTemplate.set_Cardinality (Cardinality.LIST);
	    }
	    newPropTemplate.set_SymbolicName(propSymbolicName);
	    
	    // Set required properties to locale-specific string.
	    LocalizedString locStr1 = getLocalizedString(propDisplayName, os.get_LocaleName() );
	    // Create LocalizedString collection.
	    newPropTemplate.set_DisplayNames (Factory.LocalizedString.createList() );
	    newPropTemplate.get_DisplayNames().add(locStr1);

	    LocalizedString locStr2 = getLocalizedString(propDescription,os.get_LocaleName());
	    newPropTemplate.set_DescriptiveTexts(Factory.LocalizedString.createList() );
	    newPropTemplate.get_DescriptiveTexts().add(locStr2);

	    // Save property template to the server.
	    newPropTemplate.save(RefreshMode.REFRESH);
	}

	private LocalizedString getLocalizedString(String text, String locale)
	{
	    LocalizedString locStr = Factory.LocalizedString.createInstance ();
	    locStr.set_LocalizedText(text);
	    locStr.set_LocaleName (locale);
	    return locStr;
	}

	
}
