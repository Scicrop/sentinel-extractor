package com.scicrop.se.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scicrop.se.commons.dataobjects.SocketMessage;
import com.scicrop.se.commons.net.NetUtils;
import com.scicrop.se.commons.utils.Constants;
import com.scicrop.se.commons.utils.LogHelper;
import com.scicrop.se.runtime.Launch;

public class SeSocketServer extends Thread {
	
	   private ServerSocket serverSocket = null;
	   
	   private String inWord = null;
	   private SocketMessage outWord = null;
	   
	   private static Log log = LogFactory.getLog(SeSocketServer.class);
	   
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
	            LogHelper.getInstance().handleVerboseLog(Constants.VERBOSE, Constants.LOG, log, 'i', "Supervisor: " + server.getRemoteSocketAddress() + "\n");
	 
	            DataInputStream in = new DataInputStream(server.getInputStream());
	            inWord = in.readUTF();
	            outWord = NetUtils.getInstance().handleProtocol(inWord, Launch.STATUS, server.getLocalAddress().getHostAddress(), server.getLocalPort(), Launch.CONF_PARAM);
	            LogHelper.getInstance().handleVerboseLog(Constants.VERBOSE, Constants.LOG, log, 'i', inWord);
	            DataOutputStream out = new DataOutputStream(server.getOutputStream());
	            out.writeUTF(outWord.toString());
	            inWord = in.readUTF();
	            LogHelper.getInstance().handleVerboseLog(Constants.VERBOSE, Constants.LOG, log, 'i', inWord);
	            outWord = NetUtils.getInstance().handleProtocol(inWord, Launch.STATUS, server.getLocalAddress().getHostAddress(), server.getLocalPort(), Launch.CONF_PARAM);
	            server.close();
	            
	         }catch(SocketTimeoutException s) {
	        	LogHelper.getInstance().handleVerboseLog(Constants.VERBOSE, Constants.LOG, log, 'e', "Socket timed out!");
	            break;
	         }catch(IOException e) {
	            e.printStackTrace();
	            break;
	         }
	      }
	   }

}
