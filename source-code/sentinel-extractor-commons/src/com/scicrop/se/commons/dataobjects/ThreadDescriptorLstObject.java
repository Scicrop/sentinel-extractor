package com.scicrop.se.commons.dataobjects;

import java.util.List;

public class ThreadDescriptorLstObject {
	
	private List<ThreadDescriptorObject> threadDescriptorLst = null;
	


	public ThreadDescriptorLstObject(
			List<ThreadDescriptorObject> threadDescriptorLst,String jarPath,int udpPort) {
		super();
		this.threadDescriptorLst = threadDescriptorLst;
	}
	public List<ThreadDescriptorObject> getThreadDescriptorLst() {
		return threadDescriptorLst;
	}

	public void setThreadDescriptorLst(
			List<ThreadDescriptorObject> threadDescriptorLst) {
		this.threadDescriptorLst = threadDescriptorLst;
	}
	
	

}
