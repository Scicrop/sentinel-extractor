package com.scicrop.se.dataobjects;

public class ArgumentsHistory {

	private String user = null;
	private String outputFolder = null;
	private String clientUrl = null;
	
	
	
	public ArgumentsHistory(String user, String outputFolder, String clientUrl) {
		super();
		this.user = user;
		this.outputFolder = outputFolder;
		this.clientUrl = clientUrl;
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
