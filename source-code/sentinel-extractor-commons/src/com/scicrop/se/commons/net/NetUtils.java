package com.scicrop.se.commons.net;

public class NetUtils {

	private NetUtils(){}

	private static NetUtils INSTANCE = null;

	public static NetUtils getInstance(){
		if(INSTANCE == null) INSTANCE = new NetUtils();
		return INSTANCE;
	}
	
	public String handleProtocol(String inWord, SentinelExtractorStatus status){
		String outWord = null;
		
		String[] in = inWord.split(":");
		
		switch (in[0]) {
		case "?":
			
			outWord = "=:"+status;
			
			break;

		case "=":
			
			outWord = evaluateReceivedStatus(in[1]);
			
			break;	
			
		case "!":
			
			outWord = evaluateReceivedAction(in[1]);
			
			break;	
			
			
		default:
			
			outWord = "=:fine";
			
			break;
		}
		
		return outWord;
	}
	
	private String evaluateReceivedAction(String receivedAction) {
		String outWord = null;
		
		switch (receivedAction) {
	
		case "kill":
			
			System.out.println("Killed by supervisor.");
			System.exit(0);
			
			break;
		
		default:
			
			outWord = "=:fine";
			break;
		}
		
		return outWord;
	}

	private String evaluateReceivedStatus(String receivedStatus) {
		String outWord = null;
		
		switch (receivedStatus) {
	
		case "DOWNLOADING":
			
			outWord = "!:kill";
			
			break;
		
		default:
			
			outWord = "=:fine";
			break;
		}
		
		return outWord;
	}

	public enum SentinelExtractorStatus {
		
		DOWNLOADING("Downloading"), STALLED("Stalled"), FORCE_STOP("Force Stop"), FINISHED("Finished"); 
		private String value = null; 
		private SentinelExtractorStatus(String value) { this.value = value; }

	}
	
	
}
