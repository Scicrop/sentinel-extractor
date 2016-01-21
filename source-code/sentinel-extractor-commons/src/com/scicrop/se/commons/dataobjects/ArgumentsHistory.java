package com.scicrop.se.commons.dataobjects;

public class ArgumentsHistory {

	private String user = null;
	private String outputFolder = null;
	private String clientUrl = null;
	private String sentinel = null;
	private String socketPort = null;
	private String password = null;
	private boolean verbose = false;
	private boolean log = false;
	private String logFolder = null;
	private long threadCheckerSleep = 1000l;
	private int downloadTriesLimit = 5;
	
	
	public ArgumentsHistory(String user, String outputFolder, String sentinel, String clientUrl, String socketPort, String password, boolean verbose, boolean log, String logFolder,long threadCheckerSleep,int downloadTriesLimit) {
		super();
		this.user = user;
		this.outputFolder = outputFolder;
		this.clientUrl = clientUrl;
		this.sentinel = sentinel;
		this.socketPort = socketPort;
		this.password = password;
		this.verbose = verbose;
		this.log = log;
		this.logFolder = logFolder;
		this.threadCheckerSleep=threadCheckerSleep;
		this.downloadTriesLimit=downloadTriesLimit;
	}
	
	
	
	public ArgumentsHistory() {
		// TODO Auto-generated constructor stub
	}



	public int getDownloadTriesLimit() {
		return downloadTriesLimit;
	}



	public void setDownloadTriesLimit(int downloadTriesLimit) {
		this.downloadTriesLimit = downloadTriesLimit;
	}



	public long getThreadCheckerSleep() {
		return threadCheckerSleep;
	}



	public void setThreadCheckerSleep(long threadCheckerSleep) {
		this.threadCheckerSleep = threadCheckerSleep;
	}



	public String getLogFolder() {
		return logFolder;
	}



	public void setLogFolder(String logFolder) {
		this.logFolder = logFolder;
	}



	public boolean isVerbose() {
		return verbose;
	}



	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}



	public boolean isLog() {
		return log;
	}



	public void setLog(boolean log) {
		this.log = log;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
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
