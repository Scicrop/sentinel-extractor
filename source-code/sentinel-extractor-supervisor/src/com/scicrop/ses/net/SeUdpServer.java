package com.scicrop.ses.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.net.NetUtils;
import com.scicrop.ses.threads.ThreadChecker;

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
			byte[] sendData = new byte[1024];
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
					Thread t = new ThreadChecker(clientsMap);
					t.start();
				}catch(Exception e){
					e.printStackTrace();
				}
				//				String capitalizedSentence = sentence.toUpperCase();
				//				sendData = capitalizedSentence.getBytes();
				//				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				//				serverSocket.send(sendPacket);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
