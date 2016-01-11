package com.scicrop.se.commons.dataobjects;

public class SocketMessage {

	
	private String host = null;
	private int port = -1;
	private String header =  null;
	private String payload = null;
	private String confParam = null;
	
	
	
	public SocketMessage(String host, int port, String header, String payload, String confParam) {
		super();
		this.host = host;
		this.port = port;
		this.header = header;
		this.payload = payload;
		this.confParam = confParam;
	}
	
	public SocketMessage() {
		// TODO Auto-generated constructor stub
	}

	public String getConfParam() {
		return confParam;
	}

	public void setConfParam(String confParam) {
		this.confParam = confParam;
	}

	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(getHeader()+":"+getPayload()+":"+getHost()+":"+getPort()+":"+getConfParam());
		return sb.toString();
	}
}
