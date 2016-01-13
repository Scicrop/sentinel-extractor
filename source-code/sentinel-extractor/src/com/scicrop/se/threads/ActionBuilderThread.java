package com.scicrop.se.threads;

import com.scicrop.se.commons.dataobjects.ArgumentsHistory;
import com.scicrop.se.components.ActionBuilder;

public class ActionBuilderThread extends Thread {
	
	private ArgumentsHistory aHistory = null;
	
	public ActionBuilderThread(ArgumentsHistory aHistory){
		this.aHistory = aHistory;
	}

	public void run(){
		ActionBuilder.getInstance().autoSearchDownload(aHistory);
	}
	
}
