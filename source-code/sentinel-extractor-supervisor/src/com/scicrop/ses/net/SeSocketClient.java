package com.scicrop.ses.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.scicrop.se.commons.dataobjects.SocketMessage;
import com.scicrop.se.commons.net.NetUtils;
import com.scicrop.ses.runtime.Launch;

public class SeSocketClient {
	
	private String inWord = null;
	private SocketMessage outWord = null;
	
	   public void listen(String serverName, int port){

		      try {

		         Socket client = new Socket(serverName, port);
		         OutputStream outToServer = client.getOutputStream();
		         DataOutputStream out = new DataOutputStream(outToServer);
		         outWord = new SocketMessage(client.getLocalAddress().getHostAddress(), client.getLocalPort(), "?", "status", Launch.CONF_PARAM);
		         out.writeUTF(outWord.toString());
		         System.out.println(outWord.toString());
		         InputStream inFromServer = client.getInputStream();
		         DataInputStream in = new DataInputStream(inFromServer);
		         inWord = in.readUTF();
		         outWord = NetUtils.getInstance().handleProtocol(inWord, null, client.getLocalAddress().getHostAddress(), client.getLocalPort(), Launch.CONF_PARAM);
		         System.out.println(inWord);
		         out.writeUTF(outWord.toString());
		         System.out.println(outWord.toString());
		         client.close();
		         
		      } catch(IOException e) {
		    	  
		         e.printStackTrace();
		         
		      }
		   
	   }

}
