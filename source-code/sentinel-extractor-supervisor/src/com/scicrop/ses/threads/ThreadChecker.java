package com.scicrop.ses.threads;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.net.NetUtils.SentinelExtractorStatus;
import com.scicrop.se.commons.threads.LauncherExtProcessThread;
import com.scicrop.se.commons.utils.Constants;

public class ThreadChecker extends Thread {
	
	private Map<String, Payload> clientsMap = null;

	public ThreadChecker(Map<String, Payload> clientsMap){
		this.clientsMap = clientsMap;
	}
	
	public void run(){
		
		System.out.println("Supervisor checking CLIENTS MAP with "+ clientsMap.size()+" items.");
		
		Set<String> set = clientsMap.keySet();
		Iterator<String> iter = set.iterator();
		while(iter.hasNext()){
			String element = iter.next();
			
			Payload payload = clientsMap.get(element);
			Date now = new Date();
			long duration = now.getTime() - payload.getDate().getTime();
			System.out.println("Supervisor checking process "+element+" ["+String.valueOf(duration)+"] "+payload.getSentinelExtractorStatus());
			if(duration > Constants.THREAD_CHECKER_SLEEP && payload.getSentinelExtractorStatus() == SentinelExtractorStatus.STALLED){
				Thread pThread = new LauncherExtProcessThread(new String[]{"java","-jar", Constants.JAR_PATH, payload.getConfParam(), "&"});
				pThread.start();
			}
		}
	}
	
}
