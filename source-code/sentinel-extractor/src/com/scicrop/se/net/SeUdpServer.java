package com.scicrop.se.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.net.NetUtils;
import com.scicrop.se.threads.SupervisorThreadChecker;

public class SeUdpServer extends Thread{


	private int port = -1;



	public SeUdpServer(int port) {
		this.port = port;


	}

	public void run() {
		Map<String, Payload> clientsMap = new HashMap<String, Payload>();
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(port);
			byte[] receiveData = new byte[1024];

			while(true)                {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String sentence = new String( receivePacket.getData());

				System.out.println("RECEIVED: " + sentence);
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();

				try{
					Payload payload = NetUtils.getInstance().handleProtocol(sentence);
					clientsMap.put(payload.getConfParam(), payload);
					Thread t = new SupervisorThreadChecker(clientsMap);
					t.start();
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
