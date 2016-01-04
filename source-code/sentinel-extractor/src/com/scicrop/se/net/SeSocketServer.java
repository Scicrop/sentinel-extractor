package com.scicrop.se.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.scicrop.se.commons.net.NetUtils;
import com.scicrop.se.runtime.Launch;

public class SeSocketServer extends Thread {
	
	   private ServerSocket serverSocket = null;
	   
	   private String inWord = null;
	   private String outWord = null;
	   
	   public SeSocketServer(int port) throws IOException {
	      serverSocket = new ServerSocket(port);
	     // serverSocket.setSoTimeout(10000);
	   }
	   
	   
	   
	   public void run()
	   {
	      while(true)
	      {
	         try
	         {
	           
	            Socket server = serverSocket.accept();
	            System.out.println("Supervisor: " + server.getRemoteSocketAddress() + "\n");
	            DataInputStream in = new DataInputStream(server.getInputStream());
	            inWord = in.readUTF();
	            outWord = NetUtils.getInstance().handleProtocol(inWord, Launch.STATUS);
	            System.out.println(inWord);
	            DataOutputStream out = new DataOutputStream(server.getOutputStream());
	            out.writeUTF(outWord);
	            inWord = in.readUTF();
	            System.out.println(inWord);
	            outWord = NetUtils.getInstance().handleProtocol(inWord, Launch.STATUS);
	            server.close();
	         }catch(SocketTimeoutException s) {
	            System.out.println("Socket timed out!");
	            break;
	         }catch(IOException e) {
	            e.printStackTrace();
	            break;
	         }
	      }
	   }

}
