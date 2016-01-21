package com.scicrop.se.threads;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.net.NetUtils.SentinelExtractorStatus;
import com.scicrop.se.commons.threads.LauncherExtProcessThread;
import com.scicrop.se.runtime.Launch;

public class SupervisorThreadChecker extends Thread {
	
	private Map<String, Payload> clientsMap = null;
	private String jarPath = null;

	public SupervisorThreadChecker(Map<String, Payload> clientsMap, String jarPath){
		this.clientsMap = clientsMap;
		this.jarPath = jarPath;
	}
	
	public void run(){
		
		System.out.println("Supervisor checking CLIENTS MAP with "+ clientsMap.size()+" items.");
		
		Set<String> set = clientsMap.keySet();
		Iterator<String> iter = set.iterator();
		while(iter.hasNext()){
			String element = iter.next();
			
			Payload payload = clientsMap.get(element);
			Date now = new Date();
			long duration = now.getTime() - payload.getDate();
			System.out.println("Supervisor checking process "+element+" ["+String.valueOf(duration)+"] "+payload.getSentinelExtractorStatus());
			//duration > Constants.THREAD_CHECKER_SLEEP && 
			if(payload.getSentinelExtractorStatus() == SentinelExtractorStatus.STALLED){
				System.out.println("Time to restart...: "+element);
				Thread pThread = new LauncherExtProcessThread(new String[]{Launch.JAVA_HOME!=null ? Launch.JAVA_HOME+"java":"java","-jar", jarPath, "d", element});
				pThread.start();
			}
		}
	}
	
}
