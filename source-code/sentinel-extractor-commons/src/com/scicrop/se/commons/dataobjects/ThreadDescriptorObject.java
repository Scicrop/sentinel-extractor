package com.scicrop.se.commons.dataobjects;

public class ThreadDescriptorObject {

	private String confParam = null;

	public String getConfParam() {
		return confParam;
	}

	public void setConfParam(String confParam) {
		this.confParam = confParam;
	}

	public ThreadDescriptorObject(String confParam) {
		super();
		this.confParam = confParam;
	}
	
}
