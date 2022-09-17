package com.fn.admin.dao;

import java.util.ResourceBundle;

import com.filenet.api.util.Id;
import com.fnMig.constants.ECMConstants;

public class PropTemplates {
	private static ResourceBundle bundle = ResourceBundle.getBundle(ECMConstants.CONFIG_PROPFILE_NAME);
	 private  String docClass;
	 private  String propSymbolicName;
	 private  String propDisplayName;
	 private  String propDescription;
	 private  String dataType;
	 private String cardinality;
	 private  int length;
	 private boolean isRequired_on_Class;
	 private Id requiredClassID;
	 public String getCardinality() {
		return cardinality;
	}
	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	public boolean getIsRequired_on_Class() {
		return isRequired_on_Class;
	}
	public void setIsRequired_on_Class(boolean isRequired_on_Class) {
		this.isRequired_on_Class = isRequired_on_Class;
	}
	public String getDocClass() {
		return docClass;
	}
	public void setDocClass(String docClass) {
		this.docClass = docClass;
	}
	public String getPropSymbolicName() {
		return propSymbolicName;
	}
	public void setPropSymbolicName(String propSymbolicName) {
		this.propSymbolicName = propSymbolicName;
	}
	public String getPropDisplayName() {
		return propDisplayName;
	}
	public void setPropDisplayName(String propDisplayName) {
		this.propDisplayName = propDisplayName;
	}
	public String getPropDescription() {
		return propDescription;
	}
	public void setPropDescription(String propDescription) {
		this.propDescription = propDescription;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	 public Id getRequiredClassID() {
		return requiredClassID;
	}
	public void setRequiredClassID(Id requiredClassID) {
		this.requiredClassID = requiredClassID;
	}
	public void setRequired_on_Class(boolean isRequired_on_Class) {
		this.isRequired_on_Class = isRequired_on_Class;
	}
	public void setProperty(String propName, String val) {
//		 Doc_Class	Prop_DisplayName	Prop_SymbolicName	Prop_Description	Datatype	Cardinality	isRequired_on_Class	length RequiredClassID
		 if(propName.equalsIgnoreCase(bundle.getString("Doc_Class"))) {
			 setDocClass(val);
		 }else if(propName.equalsIgnoreCase(bundle.getString("Prop_DisplayName"))) {
			 setPropDisplayName(val);
		 }if(propName.equalsIgnoreCase(bundle.getString("Prop_SymbolicName"))) {
			 setPropSymbolicName(val);
		 }if(propName.equalsIgnoreCase(bundle.getString("Prop_Description"))) {
			 setPropDescription(val);
		 }if(propName.equalsIgnoreCase(bundle.getString("Datatype"))) {
			 setDataType(val);
		 }if(propName.equalsIgnoreCase(bundle.getString("Cardinality"))) {
			 setCardinality(val);
		 }if(propName.equalsIgnoreCase(bundle.getString("isRequired_on_Class"))) {
			 boolean boolVal = false;
			 try {
			 boolVal = Boolean.parseBoolean(val);
			 }catch (Exception e) {
				 System.out.println("Can not convert value to boolean :"+val);
			}
			 setIsRequired_on_Class(boolVal);
		 }if(propName.equalsIgnoreCase(bundle.getString("length"))) {
			 int i= 0;
			 try {
				 i = Integer.parseInt(val);
			 }catch (Exception e) {
				System.out.println("Can not Convert to int:"+val);
			}
			 setLength(i);
		 }if(propName.equalsIgnoreCase(bundle.getString("RequiredClassID"))) {
			 Id id = null;
			 try {
				 id = new Id(val);
			 }catch (Exception e) {
				 System.out.println("Con not convert to ID:"+val);
			}
			 setRequiredClassID(id);
		 }
	 }
@Override
public String toString() {
	return docClass+","+propDisplayName+","+propSymbolicName+","+propDescription+","+dataType+","+cardinality+","+isRequired_on_Class+","+length+","+requiredClassID;
}
}
