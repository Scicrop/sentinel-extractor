package com.scicrop.se.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.net.NetUtils.SentinelExtractorStatus;
import com.scicrop.se.commons.utils.Constants;
import com.scicrop.se.commons.utils.LogHelper;
import com.scicrop.se.runtime.Launch;

public class SeUdpClient extends Thread {


	private int port = -1;

	private static Log log = LogFactory.getLog(SeUdpClient.class);

	public SeUdpClient(int port) {
		this.port = port;


	}

	public void run(){

		while(true){


			
			sendMsgToUdpServer();
			
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				LogHelper.getInstance().handleVerboseLog(Constants.VERBOSE, Constants.LOG, log, 'e', e.getMessage());
			}
		}

	}

	public void sendMsgToUdpServer() {
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
			byte[] sendData = new byte[1024];

			String sentence = new Payload(SentinelExtractorStatus.STARTING, "", -1, Launch.CONF_PARAM, new Date().getTime()).toString();
			if(Launch.STATUS != null) sentence = Launch.STATUS.toString();
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			clientSocket.send(sendPacket);

			
		} catch (SocketException e) {
			LogHelper.getInstance().handleVerboseLog(Constants.VERBOSE, Constants.LOG, log, 'e', e.getMessage());
		} catch (UnknownHostException e) {
			LogHelper.getInstance().handleVerboseLog(Constants.VERBOSE, Constants.LOG, log, 'e', e.getMessage());
		} catch (IOException e) {
			LogHelper.getInstance().handleVerboseLog(Constants.VERBOSE, Constants.LOG, log, 'e', e.getMessage());
		}finally{
			clientSocket.close();
		}
	}

}
