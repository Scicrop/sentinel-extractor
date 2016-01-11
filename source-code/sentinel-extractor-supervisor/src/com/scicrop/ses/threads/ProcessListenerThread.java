package com.scicrop.ses.threads;

import com.scicrop.ses.net.SeSocketClient;

public class ProcessListenerThread extends Thread {

	
	private int port = -1;
	
	public ProcessListenerThread(int port){
		this.port = port;
	}
	
	
	public void run(){
		
		while(true){
			
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			SeSocketClient sesc = new SeSocketClient();
			sesc.listen("127.0.0.1", port);
		}
		
	}
}
