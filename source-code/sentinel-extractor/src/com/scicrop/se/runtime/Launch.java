package com.scicrop.se.runtime;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import com.scicrop.se.commons.dataobjects.ArgumentsHistory;
import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.dataobjects.ThreadDescriptorLstObject;
import com.scicrop.se.commons.dataobjects.ThreadDescriptorObject;
import com.scicrop.se.commons.threads.LauncherExtProcessThread;
import com.scicrop.se.commons.utils.Commons;
import com.scicrop.se.commons.utils.Constants;
import com.scicrop.se.commons.utils.LogHelper;
import com.scicrop.se.commons.utils.XmlUtils;
import com.scicrop.se.components.ActionBuilder;
import com.scicrop.se.net.SeUdpClient;
import com.scicrop.se.net.SeUdpServer;
import com.scicrop.se.threads.ActionBuilderThread;



public class Launch {

	public static Payload STATUS = null;
	public static String CONF_PARAM = null;
	
	public static void main(String[] args) {

		
		if(args!=null && args.length == 2){ //check if is non-interactive mode syntax

			System.out.println("Entering in a non-interative mode:");
			
			if(args[0].equalsIgnoreCase("d") || args[0].equalsIgnoreCase("s")){ //check non-interactive mode syntax
				
				if(args[0].equalsIgnoreCase("d")){ //check if is non-interactive mode DOWNLOADER
					
					System.out.println("Non-interative mode: DOWNLOADER");
					
					if(args[1] !=null && (new File(args[1].trim()).exists()) && (new File(args[1].trim()).isFile())) {

						CONF_PARAM = args[1].trim();
								
						ArgumentsHistory aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile(CONF_PARAM);
						
						

						LogHelper.getInstance().setLogger(aHistory.getSocketPort());
						
							try {
								Thread t = new SeUdpClient(Integer.parseInt(aHistory.getSocketPort()));
								t.start();
							} catch (NumberFormatException e) {
								System.out.println("The socket port described in "+CONF_PARAM+" must be an integer.");
								System.exit(0);
							}
							
						
						Thread actionBuilderThread = new ActionBuilderThread(aHistory);
						actionBuilderThread.start();

					}			
					
				}else if(args[0].equalsIgnoreCase("s")){ //check if is non-interactive mode SUPERVISOR
					
					System.out.println("Non-interative mode: SUPERVISOR");
					
					File f = new File(args[1]);

					if(f.exists() && f.isFile()){
						
						CONF_PARAM = args[1];
						
						ThreadDescriptorLstObject t = XmlUtils.getInstance().threadDescLst(f);
						List<ThreadDescriptorObject> l = t.getThreadDescriptorLst();
						for (ThreadDescriptorObject threadDescriptorObject : l) {

							String confParam = threadDescriptorObject.getConfParam();
							ArgumentsHistory aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile(confParam);

							

							File jarFile = new File(Constants.JAR_PATH);

							if(jarFile.exists() && jarFile.isFile()){


								
								Thread pThread = new LauncherExtProcessThread(new String[]{"java","-jar", Constants.JAR_PATH, "d",confParam});
								pThread.start();

							}else{
								System.out.println("Jar file "+Constants.JAR_PATH+" was not found.");
								System.exit(0);
							}

							

						}
						
						Thread procListenerThread = new SeUdpServer(Constants.UDP_SERVER_PORT);
						procListenerThread.start();
					}

				}
				
				
			}
	
		}else if(args == null || args.length ==0){ //check if is interactive mode

			
			System.out.println("\n\nSentinel Extractor 0.2.1\nCommand Line Interface (CLI)\nhttps://github.com/Scicrop/sentinel-extractor\n\n");

			
			String clientUrl = null;
			File oFolder = null;
			Scanner keyboard = new Scanner(System.in);

			ArgumentsHistory aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile();
			
			String hist = "";

			if(aHistory != null) hist =  "["+aHistory.getUser()+"]";

			System.out.println("User: "+hist);
			String user = keyboard.nextLine();

			if(user.equals("") && !hist.equals("")) user = aHistory.getUser();		

			System.out.println("Password: ");
			String password = keyboard.nextLine();

			/* sentinel # */
			hist = "";
			if(aHistory != null) hist =  "["+aHistory.getSentinel()+"]";

			System.out.println("Sentinel# (1|2): "+hist);
			String sentinel = keyboard.nextLine();

			if(sentinel.equals("") && !hist.equals("")) sentinel = aHistory.getSentinel();

			if(!sentinel.equals("1") && !sentinel.equals("2")){
				System.out.println("Invalid sentinel number! (1|2)");
				System.exit(2);
			}



			/* --- */

			hist = "";
			if(aHistory != null) hist =  "["+aHistory.getOutputFolder()+"]";

			System.out.println("Output folder: "+hist);
			String outputFolder = keyboard.nextLine();

			if(outputFolder.equals("") && !hist.equals("")) outputFolder = aHistory.getOutputFolder();

			oFolder = new File(outputFolder);
			if(null == oFolder || !oFolder.exists() || !oFolder.isDirectory()) {
				System.err.println(outputFolder + " is not a valid output folder.");
				System.exit(1);
			}else{
				if(outputFolder.charAt(outputFolder.length()-1) != '/'){
					outputFolder = outputFolder + "/";
					System.out.println("Normalizing output folder.");
				}
				System.out.println("Checked Output folder: "+outputFolder+"\n\n");
			}



			System.out.println("1) Open Search Query ");
			System.out.println("2) Open Data Query (by UUID)");
			System.out.println("3) Resume interrupted downloads");
			String searchType = keyboard.nextLine();

			ActionBuilder.getInstance().manualSwitcher(aHistory, clientUrl, oFolder, user, password, sentinel, outputFolder, searchType);



		}else if(args!=null && (args.length == 1 || args.length > 3)){
			
			System.err.println("Invalid parameters.");
			
			if(args!=null && args.length > 0){
				for (String string : args) {
					System.out.println("args = "+string);
				}
			}
			
			System.exit(0);
			
		}

	}



}
