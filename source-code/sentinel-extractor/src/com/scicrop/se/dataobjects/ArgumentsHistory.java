package com.scicrop.se.dataobjects;

public class ArgumentsHistory {

	private String user = null;
	private String outputFolder = null;
	private String clientUrl = null;
	private String sentinel = null;
	private String socketPort = null;
	
	
	public ArgumentsHistory(String user, String outputFolder, String sentinel, String clientUrl, String socketPort) {
		super();
		this.user = user;
		this.outputFolder = outputFolder;
		this.clientUrl = clientUrl;
		this.sentinel = sentinel;
		this.socketPort = socketPort;
	}
	
	
	
	public String getSocketPort() {
		return socketPort;
	}



	public void setSocketPort(String socketPort) {
		this.socketPort = socketPort;
	}



	public String getSentinel() {
		return sentinel;
	}



	public void setSentinel(String sentinel) {
		this.sentinel = sentinel;
	}



	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getOutputFolder() {
		return outputFolder;
	}
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	public String getClientUrl() {
		return clientUrl;
	}
	public void setClientUrl(String clientUrl) {
		this.clientUrl = clientUrl;
	}
	
	
	
}
