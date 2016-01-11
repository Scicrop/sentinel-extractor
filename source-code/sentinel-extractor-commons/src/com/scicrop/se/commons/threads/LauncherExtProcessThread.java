package com.scicrop.se.commons.threads;

import java.io.IOException;

public class LauncherExtProcessThread extends Thread {
	
	private String[] execParams = null;
	
	public LauncherExtProcessThread(String[] execParams){
		//"java","-jar", jarPath, confParam
		this.execParams = execParams;
	}
	
	public void run(){
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(execParams);
			Process p = processBuilder.start();
			System.out.println("Process started with parameters from: "+execParams[execParams.length-1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
