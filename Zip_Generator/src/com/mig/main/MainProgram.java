package com.mig.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.mig.util.InputTxtFileReader;

public class MainProgram {
	public static ResourceBundle rb = ResourceBundle.getBundle("connection");
	public static void main(String[] args) {
		
		execute();

	}
	private static void execute() {
		InputTxtFileReader txtFlReader = new InputTxtFileReader();
		ArrayList<HashMap<String, String>> txtMap = txtFlReader.readTxtFile(rb.getString("inputFilePath"));
		
		
	}

}
