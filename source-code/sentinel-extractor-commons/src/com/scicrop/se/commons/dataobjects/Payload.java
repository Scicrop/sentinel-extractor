package com.scicrop.se.commons.dataobjects;

import com.scicrop.se.commons.net.NetUtils;
import com.scicrop.se.commons.net.NetUtils.SentinelExtractorStatus;

public class Payload {

	private NetUtils.SentinelExtractorStatus sentinelExtractorStatus = null;
	private String description = null;
	private int pid = -1;
	private String confParam = null;
	private long date = -1l;
	
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
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getConfParam() {
		return confParam;
	}
	public void setConfParam(String confParam) {
		this.confParam = confParam;
	}
	
	public Payload(SentinelExtractorStatus sentinelExtractorStatus, String description, int pid, String confParam, long date) {
		super();
		this.sentinelExtractorStatus = sentinelExtractorStatus;
		this.description = description;
		this.pid = pid;
		this.confParam = confParam;
		this.date = date;
	}

	
	public String toString(){
		
		StringBuffer sb = new StringBuffer();
		sb.append(getSentinelExtractorStatus().name());
		sb.append(":");
		sb.append(getDescription());
		sb.append(":");
		sb.append(getPid());
		sb.append(":");
		sb.append(getConfParam());
		sb.append(":");
		sb.append(getDate());
		sb.append(":");
		return sb.toString();
	}
	
	
	
	
	
}
