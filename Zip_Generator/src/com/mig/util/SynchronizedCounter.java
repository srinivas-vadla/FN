package com.mig.util;

public class SynchronizedCounter {
    private int successCount = 0;
    private int failCount = 0;
    // Synchronized Method 
    public synchronized void successIncrement() {
        successCount = successCount + 1;
    }
    
 // Synchronized Method 
    public synchronized void failureIncrement() {
    	failCount = failCount + 1;
    }

    public int getSuccessCount() {
        return successCount;
    }
    
    public int getFailCount() {
        return failCount;
    }
}
