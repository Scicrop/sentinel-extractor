package com.scicrop.se.runtime;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import com.scicrop.se.commons.dataobjects.ArgumentsHistory;
import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.dataobjects.SupervisorXmlObject;
import com.scicrop.se.commons.dataobjects.ThreadDescriptorObject;
import com.scicrop.se.commons.threads.LauncherExtProcessThread;
import com.scicrop.se.commons.utils.Commons;
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
		
		if(Commons.getInstance().getJavaHomeVersion() >= 1.7d){

			if(args!=null && args.length == 2){ //check if is non-interactive mode syntax

				System.out.println("Entering in a non-interative mode:");

				if(args[0].equalsIgnoreCase("d") || args[0].equalsIgnoreCase("s")){ //check non-interactive mode syntax

					if(args[0].equalsIgnoreCase("d")){ //check if is non-interactive mode DOWNLOADER

						System.out.println("Non-interative mode: DOWNLOADER");

						if(args[1] !=null && (new File(args[1].trim()).exists()) && (new File(args[1].trim()).isFile())) {

							CONF_PARAM = args[1].trim();

							ArgumentsHistory aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile(CONF_PARAM);



							LogHelper.getInstance().setLogger(aHistory.getSocketPort(), aHistory.getLogFolder());

							try {
								Thread t = new SeUdpClient(Integer.parseInt(aHistory.getSocketPort()),aHistory);
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

							SupervisorXmlObject t = XmlUtils.getInstance().xmlFile2Object(f);
							List<ThreadDescriptorObject> l = t.getThreadDescriptorLst();

							for (ThreadDescriptorObject threadDescriptorObject : l) {

								String confParam = threadDescriptorObject.getConfParam();
								ArgumentsHistory aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile(confParam);

								File jarFile = new File(t.getJarPath());

								if(jarFile.exists() && jarFile.isFile()){

									Thread pThread = new LauncherExtProcessThread(new String[]{"java","-jar", t.getJarPath(), "d",confParam});
									pThread.start();

								}else{
									System.out.println("Jar file "+t.getJarPath()+" was not found.");
									System.exit(0);
								}



							}

							Thread procListenerThread = new SeUdpServer(t.getUdpPort());
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


				System.out.println("Enable verbose? [true]");
				String sVerbose = keyboard.nextLine();
				boolean verbose = true;
				if(sVerbose.equals("true") || sVerbose.equals("false")){
					verbose = Boolean.parseBoolean(sVerbose);
				}

				System.out.println("Enable log? [true]");
				String sLog = keyboard.nextLine();
				boolean log = true;
				if(sLog == null || sLog.equals("")) log = true;
				if(sLog.equals("true") || sLog.equals("false")){
					log = Boolean.parseBoolean(sLog);
				}

				System.out.println("Type the log folder: [/var/log/sentinel/]");
				String logFolder = keyboard.nextLine();
				if(logFolder.equals("") && !logFolder.equals("")) logFolder = "/var/log/sentinel/";


				System.out.println("How much time (millis) the supervisor will check the thread status: [1000]");
				String millis = keyboard.nextLine();
				long threadCheckerSleep = 1000l;
				if(millis != null || !millis.equals("")) threadCheckerSleep = Long.parseLong(millis);


				System.out.println("How much tries will try the download? [5] ");
				String sDownload = keyboard.nextLine();
				int downloadTriesLimit = 5;
				if(sDownload != null || !sDownload.equals("")) downloadTriesLimit = Integer.parseInt(sDownload);


				System.out.println("UDP Port? [10020] ");
				String sUdpPort = keyboard.nextLine();
				int udpPort = 10020;
				if(sDownload != null || !sDownload.equals("")) udpPort = Integer.parseInt(sUdpPort);

				ActionBuilder.getInstance().manualSwitcher(aHistory, clientUrl, oFolder, user, password, sentinel, outputFolder, searchType,verbose,log,logFolder,threadCheckerSleep,downloadTriesLimit);



			}else if(args!=null && (args.length == 1 || args.length > 3)){

				System.err.println("Invalid parameters.");

				if(args!=null && args.length > 0){
					for (String string : args) {
						System.out.println("args = "+string);
					}
				}

				System.exit(0);

			}
		}else{
			System.out.println("Required java version >= 1.7");
		}
	}



}
