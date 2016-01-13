package com.scicrop.se.commons.net;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.dataobjects.SocketMessage;
import com.scicrop.se.commons.threads.LauncherExtProcessThread;
import com.scicrop.se.commons.utils.Constants;
import com.scicrop.se.commons.utils.LogHelper;

public class NetUtils {

	private NetUtils(){}

	private static NetUtils INSTANCE = null;

	private static Log log = LogFactory.getLog(NetUtils.class);
	
	public static NetUtils getInstance(){
		if(INSTANCE == null) INSTANCE = new NetUtils();
		return INSTANCE;
	}
	
	public Payload handleProtocol(String sentence){
		
		
		return parsePayload(sentence);
	}
	
	private Payload parsePayload(String sentence) {
		String[] sentenceSplit = sentence.split(":");
		
		Payload payload = new Payload(NetUtils.SentinelExtractorStatus.valueOf(sentenceSplit[0]), sentenceSplit[1], Integer.parseInt(sentenceSplit[2]), sentenceSplit[3].trim());
		payload.setDate(new Date());
		
		return payload;
	}

	private SocketMessage evaluateReceivedRequest(String receivedRequest, Payload status) {
		SocketMessage outWord = new SocketMessage();
		
		switch (receivedRequest) {
	
		case "status":
			
			outWord.setHeader("=");
			outWord.setPayload(status.getSentinelExtractorStatus().name());
			
			break;
		
		case "download_status":
			
			outWord.setHeader("<");
			//outWord.setPayload(status.getDescription());
			
			outWord.setPayload("oi");
			
			break;	
		
			
		default:
			
			outWord.setHeader("=");
			outWord.setPayload("request_not_defined");
			break;
		}
		
		return outWord;
	}

	private SocketMessage parseMessage(String inWord) {
		// ?:status:127.0.0.1:9001:confParam
		
		String[] part = inWord.split(":");
		
		SocketMessage sMessage = new SocketMessage(part[2], Integer.parseInt(part[3]), part[0], part[1], part[4]);
		
		return sMessage;
	}

	private SocketMessage evaluateReceivedAction(SocketMessage receivedAction) {
		SocketMessage outWord = new SocketMessage();
		
		switch (receivedAction.getPayload()) {
	
		case "kill":
			
			LogHelper.getInstance().handleVerboseLog(Constants.VERBOSE, Constants.LOG, log, 'i', "Killed by supervisor.");
			
			outWord.setHeader("!");
			outWord.setPayload("restart");
			
			System.exit(0);
			
			
			
			break;
			
		case "restart":
			
			Thread pThread = new LauncherExtProcessThread(new String[]{"java","-jar", Constants.JAR_PATH, receivedAction.getConfParam()});
			pThread.start(); 
			
			break;
		
		default:
			
			outWord.setHeader("=");
			outWord.setPayload("action_not_defined");
			break;
		}
		
		return outWord;
	}

	private SocketMessage evaluateReceivedStatus(SocketMessage receivedStatus, Payload status) {
		SocketMessage outWord = new SocketMessage();
		
		switch (receivedStatus.getPayload()) {
	
		case "STALLED":
			
			outWord.setHeader("!");
			outWord.setPayload("kill");
			
			break;
		
		case "DOWNLOADING":
			
			outWord.setHeader("?");
			outWord.setPayload("download_status");
			
			break;	
		
			
		default:
			
			outWord.setHeader("=");
			outWord.setPayload("status_not_defined|"+receivedStatus.getPayload());
			break;
		}
		
		return outWord;
	}

	public enum SentinelExtractorStatus {
		
		STARTING("Starting"), PROCESSING_QUERY("Processing query"), DOWNLOADING("Downloading"), STALLED("Stalled"), FORCE_STOP("Force Stop"), FINISHED("Finished"); 
		private String value = null; 
		private SentinelExtractorStatus(String value) { this.value = value; }

	}
	
	
}
