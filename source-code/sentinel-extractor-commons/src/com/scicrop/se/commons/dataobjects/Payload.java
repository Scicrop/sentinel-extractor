package com.scicrop.se.commons.dataobjects;

import com.scicrop.se.commons.net.NetUtils;
import com.scicrop.se.commons.net.NetUtils.SentinelExtractorStatus;

public class Payload {

	private NetUtils.SentinelExtractorStatus sentinelExtractorStatus = null;
	private String description = null;
	
	public NetUtils.SentinelExtractorStatus getSentinelExtractorStatus() {
		return sentinelExtractorStatus;
	}
	public void setSentinelExtractorStatus(NetUtils.SentinelExtractorStatus sentinelExtractorStatus) {
		this.sentinelExtractorStatus = sentinelExtractorStatus;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Payload(SentinelExtractorStatus sentinelExtractorStatus,
			String description) {
		super();
		this.sentinelExtractorStatus = sentinelExtractorStatus;
		this.description = description;
	}
	
	
	
	
	
}
