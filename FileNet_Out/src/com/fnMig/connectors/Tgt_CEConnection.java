package com.fnMig.connectors;

import java.util.ResourceBundle;

import javax.security.auth.Subject;

import org.apache.log4j.Logger;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import com.fnMig.constants.ECMConstants;

public  class Tgt_CEConnection {

	private static Connection connection=null;

	private static UserContext userContext=null;

	private static Subject subject=null;

	private static ObjectStore objectStore=null;

	private static Domain domain=null;

	public static Logger connectionLogger = Logger.getLogger(Tgt_CEConnection.class);
	public static ResourceBundle tgtProps = ResourceBundle.getBundle(ECMConstants.CON_PROPFILE_NAME);
	public	static Connection get_Connection(){

		try{

			connection=Factory.Connection.getConnection(tgtProps.getString("tgt_uri"));

			userContext=UserContext.get();

			subject = UserContext.createSubject(connection, tgtProps.getString("tgt_username"), tgtProps.getString("tgt_password"), null);

			userContext.pushSubject(subject);



		}catch(Exception e){

			//e.printStackTrace();
			connectionLogger.error("Error from CEConnection() : "+e.getMessage(), e);
		}

		return connection;

	}
	public static Domain get_Domain(){

		if(connection == null){
			get_Connection();
		}
		domain=Factory.Domain.fetchInstance(connection,null,null);

		return domain;

	}

	public static ObjectStore get_ObjectStore(){
		if(objectStore==null){			
		
		/*if(connection == null){
			get_Connection();
		}*/
		if(domain == null){
			get_Domain();
		}
		objectStore = Factory.ObjectStore.fetchInstance(domain,tgtProps.getString("tgt_objectstorename"),null);
		}
		return objectStore;

	}

	public static void main(String[] args){
		System.out.println(get_ObjectStore());
	}
}