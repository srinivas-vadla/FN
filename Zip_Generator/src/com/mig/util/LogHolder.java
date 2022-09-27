package com.mig.util;

import java.util.ArrayList;

public class LogHolder {
private static String resultInfo;
private static ArrayList<String> closedThreads = new ArrayList<String>();
public static String getResultInfo() {
	return resultInfo;
}

public static void setResultInfo(String resultInfo) {
	LogHolder.resultInfo = resultInfo;
}

public static ArrayList<String> getClosedThreads() {
	return closedThreads;
}

public static void addToClosedThreads(String closedThreads) {
	
	LogHolder.closedThreads.add(closedThreads);
}
}
