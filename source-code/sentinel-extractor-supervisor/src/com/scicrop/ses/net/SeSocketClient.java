package com.scicrop.ses.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.scicrop.se.commons.net.NetUtils;

public class SeSocketClient {
	
	private String inWord = null;
	private String outWord = null;
	
	   public void listen(String serverName, int port){

		      try {
		         System.out.println("Connecting to " + serverName + " on port " + port);
		         Socket client = new Socket(serverName, port);
		         System.out.println("Just connected to " + client.getRemoteSocketAddress());
		         OutputStream outToServer = client.getOutputStream();
		         DataOutputStream out = new DataOutputStream(outToServer);
		         out.writeUTF("?:status");
		         InputStream inFromServer = client.getInputStream();
		         DataInputStream in = new DataInputStream(inFromServer);
		         inWord = in.readUTF();
		         outWord = NetUtils.getInstance().handleProtocol(inWord, null);
		         System.out.println(inWord);
		         out.writeUTF(outWord);
		         System.out.println(outWord);
		         client.close();
		      }catch(IOException e)
		      {
		         e.printStackTrace();
		      }
		   
	   }

}
