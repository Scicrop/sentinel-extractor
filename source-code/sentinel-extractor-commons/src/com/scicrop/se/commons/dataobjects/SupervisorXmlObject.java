package com.scicrop.se.commons.dataobjects;

import java.util.List;

public class SupervisorXmlObject {
	private ThreadDescriptorLstObject threadDescriptorLstObject;
	private String jarPath;
	private int udpPort;
	
	
	public SupervisorXmlObject(){}
	public SupervisorXmlObject(ThreadDescriptorLstObject threadDescriptorLstObject, String jarPath, int udpPort) {
		super();
		this.threadDescriptorLstObject = threadDescriptorLstObject;
		this.jarPath = jarPath;
		this.udpPort = udpPort;
	}
	public ThreadDescriptorLstObject getThreadDescriptorLstObject() {
		return threadDescriptorLstObject;
	}
	public void setThreadDescriptorLstObject(
			ThreadDescriptorLstObject threadDescriptorLstObject) {
		this.threadDescriptorLstObject = threadDescriptorLstObject;
	}
	public String getJarPath() {
		return jarPath;
	}
	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}
	public int getUdpPort() {
		return udpPort;
	}
	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}
	public List<ThreadDescriptorObject> getThreadDescriptorLst() {
		List<ThreadDescriptorObject> ret = null;
		if(threadDescriptorLstObject != null) ret = threadDescriptorLstObject.getThreadDescriptorLst();
		return ret;
	}
	
	
}
