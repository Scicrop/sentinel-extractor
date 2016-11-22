package com.scicrop.se.commons.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scicrop.se.commons.dataobjects.Payload;

public class NetUtils {

	private NetUtils(){}

	private static NetUtils INSTANCE = null;

	public static NetUtils getInstance(){
		if(INSTANCE == null) INSTANCE = new NetUtils();
		return INSTANCE;
	}

	public Payload handleProtocol(String sentence){


		return parsePayload(sentence);
	}

	private Payload parsePayload(String sentence) {
		String[] sentenceSplit = sentence.split(":");

		Payload payload = null; 
		if(sentenceSplit != null && sentenceSplit.length == 6){
			payload	=new Payload(NetUtils.SentinelExtractorStatus.valueOf(sentenceSplit[0]), sentenceSplit[1], Integer.parseInt(sentenceSplit[2]), sentenceSplit[3].trim(), Long.parseLong(sentenceSplit[4]));
		}
		return payload;
	}

	public enum SentinelExtractorStatus {

		STARTING("Starting"), PROCESSING_QUERY("Processing query"), DOWNLOADING("Downloading"), STALLED("Stalled"), FORCE_STOP("Force Stop"), FINISHED("Finished"); 
		private String value = null; 
		private SentinelExtractorStatus(String value) { this.value = value; }

	}


}
