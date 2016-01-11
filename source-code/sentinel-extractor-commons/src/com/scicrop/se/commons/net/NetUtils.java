package com.scicrop.se.commons.net;

import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.dataobjects.SocketMessage;
import com.scicrop.se.commons.threads.LauncherExtProcessThread;
import com.scicrop.se.commons.utils.Constants;

public class NetUtils {

	private NetUtils(){}

	private static NetUtils INSTANCE = null;

	public static NetUtils getInstance(){
		if(INSTANCE == null) INSTANCE = new NetUtils();
		return INSTANCE;
	}
	
	public SocketMessage handleProtocol(String inWord, Payload status, String host, int port, String confParam){
		SocketMessage outWord = new SocketMessage();
		
		
		
		SocketMessage sMessage = parseMessage(inWord);
		
		switch (sMessage.getHeader()) {
		case "?":
			
			outWord = evaluateReceivedRequest(sMessage.getPayload(), status);
			
			break;

		case "=":
			
			outWord = evaluateReceivedStatus(sMessage.getPayload());
			
			break;	
			
		case "!":
			
			outWord = evaluateReceivedAction(sMessage);
			
			break;	
			
			
		default:
			
			outWord.setHeader("=");
			outWord.setPayload("header_not_defined");
			
			break;
		}
		
		outWord.setHost(host);
		outWord.setPort(port);
		
		return outWord;
	}
	
	private SocketMessage evaluateReceivedRequest(String receivedRequest, Payload status) {
		SocketMessage outWord = new SocketMessage();
		
		switch (receivedRequest) {
	
		case "status":
			
			outWord.setHeader("=");
			outWord.setPayload(status.getSentinelExtractorStatus().name());
			
			break;
		
		case "download_status":
			
			outWord.setHeader("=");
			outWord.setPayload(status.getDescription());
			
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
			
			System.out.println("Killed by supervisor.");
			System.exit(0);
			
			outWord.setHeader("!");
			outWord.setPayload("restart");
			
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

	private SocketMessage evaluateReceivedStatus(String receivedStatus) {
		SocketMessage outWord = new SocketMessage();
		
		switch (receivedStatus) {
	
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
			outWord.setPayload("status_not_defined");
			break;
		}
		
		return outWord;
	}

	public enum SentinelExtractorStatus {
		
		PROCESSING_QUERY("Processing query"), DOWNLOADING("Downloading"), STALLED("Stalled"), FORCE_STOP("Force Stop"), FINISHED("Finished"); 
		private String value = null; 
		private SentinelExtractorStatus(String value) { this.value = value; }

	}
	
	
}
