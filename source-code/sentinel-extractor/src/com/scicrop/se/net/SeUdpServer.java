package com.scicrop.se.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.net.NetUtils;
import com.scicrop.se.commons.utils.Commons;
import com.scicrop.se.commons.utils.Constants;
import com.scicrop.se.runtime.Launch;
import com.scicrop.se.threads.SupervisorThreadChecker;

public class SeUdpServer extends Thread{


	private int port = -1;
	private String notifierUrl = null;
	public SeUdpServer(int port, String notifierUrl) {
		this.port = port;
		this.notifierUrl = notifierUrl;
	}

	public void run() {
		Map<String, Payload> clientsMap = new HashMap<String, Payload>();
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(port);
			byte[] receiveData = new byte[1024];
			int counter = 0;
			long epochT0 = new Date().getTime();
			while(serverSocket.isBound()) {

				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String sentence = new String( receivePacket.getData());

				System.out.println("RECEIVED: " + sentence);

				try{
					long epochT1 = new Date().getTime();
					Payload payload = NetUtils.getInstance().handleProtocol(sentence);
					clientsMap.put(payload.getConfParam(), payload);
					if(epochT1 - epochT0 > 10000) { 
						
						Thread t = new SupervisorThreadChecker(clientsMap, Launch.JAR_PATH);
						t.start();
						
						epochT0 = epochT1;
						System.out.println("SupervisorThreadChecker: "+counter++);
					}
					
					//Map<String, String> map = new HashMap<String, String>();
					//map.put("payload", URLEncoder.encode(sentence, Constants.UTF8));
					//Commons.getInstance().sendPost(notifierUrl, map);
					
				}catch(Exception e){
					e.printStackTrace();
				}
				for (int i = 0; i < receiveData.length; i++) {
					receiveData[i] = 0;
				}

				receivePacket = null;
				sentence = null;
			}
		} catch (SocketException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}

	}

}
