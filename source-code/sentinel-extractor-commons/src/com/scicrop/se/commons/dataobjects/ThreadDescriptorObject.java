package com.scicrop.se.commons.dataobjects;

public class ThreadDescriptorObject {

	private String confParam = null;
	private String jarPath = null;

	public String getConfParam() {
		return confParam;
	}

	public void setConfParam(String confParam) {
		this.confParam = confParam;
	}

	public ThreadDescriptorObject(String confParam, String jarPath) {
		super();
		this.confParam = confParam;
		this.jarPath = jarPath;
	}

	public String getJarPath() {
		return jarPath;
	}

	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}
	
	
	
}
