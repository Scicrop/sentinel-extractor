package com.scicrop.se.runtime;

import java.io.File;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import com.scicrop.se.commons.dataobjects.ArgumentsHistory;
import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.dataobjects.SupervisorXmlObject;
import com.scicrop.se.commons.dataobjects.ThreadDescriptorObject;
import com.scicrop.se.commons.threads.LauncherExtProcessThread;
import com.scicrop.se.commons.utils.Commons;
import com.scicrop.se.commons.utils.Constants;
import com.scicrop.se.commons.utils.LogHelper;
import com.scicrop.se.commons.utils.SentinelRuntimeException;
import com.scicrop.se.commons.utils.XmlUtils;
import com.scicrop.se.components.ActionBuilder;
import com.scicrop.se.net.SeUdpClient;
import com.scicrop.se.net.SeUdpServer;
import com.scicrop.se.threads.ActionBuilderThread;



public class Launch {

	public static Payload STATUS = null;
	public static String CONF_PARAM = null;
	public static String JAR_PATH = null;
	public static int UDP_SERVER_PORT = -1;
	public static String JAVA_HOME = null;
	
	public static void main(String[] args) throws UnknownHostException {

		if(Commons.getInstance().getJavaHomeVersion() >= Constants.JAVA_VERSION_COMPLIANCE){

			if(args!=null && args.length == 2){ //check if is non-interactive mode syntax

				System.out.println("Entering in a non-interative mode:");

				if(args[0].equalsIgnoreCase("d") || args[0].equalsIgnoreCase("s")){ //check non-interactive mode syntax

					if(args[0].equalsIgnoreCase("d")){ //check if is non-interactive mode DOWNLOADER

						System.out.println("Non-interative mode: DOWNLOADER");

						if(args[1] !=null && (new File(args[1].trim()).exists()) && (new File(args[1].trim()).isFile())) {

							CONF_PARAM = args[1].trim();

							ArgumentsHistory aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile(CONF_PARAM);

							String logPattern = CONF_PARAM.replaceAll("\\/", "_").replaceAll("\\.", "_");

							LogHelper.getInstance().setLogger(logPattern, aHistory.getLogFolder());

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

							SupervisorXmlObject t = null;
							try {
								t = XmlUtils.getInstance().xmlFile2Object(f);
								UDP_SERVER_PORT = t.getUdpPort();
								
								List<ThreadDescriptorObject> l = t.getThreadDescriptorLst();

								for (ThreadDescriptorObject threadDescriptorObject : l) {

									String confParam = threadDescriptorObject.getConfParam();

									File jarFile = new File(t.getJarPath());

									if(jarFile.exists() && jarFile.isFile()){

										JAR_PATH = t.getJarPath();
										try {
											JAVA_HOME = Commons.getInstance().getJavaHome();
										} catch (SentinelRuntimeException e) {
											System.out.println(e.getMessage());
										}
										Thread pThread = new LauncherExtProcessThread(new String[]{JAVA_HOME!=null ? JAVA_HOME+"java":"java","-jar", JAR_PATH, "d",confParam});
										pThread.start();

									}else{
										System.out.println("Jar file "+t.getJarPath()+" was not found.");
										System.exit(0);
									}
								}

								Thread procListenerThread = new SeUdpServer(t.getUdpPort());
								procListenerThread.start();
							} catch (SentinelRuntimeException e1) {
								
								System.err.println("Error trying to parse xml file ["+args[1]+"]. Detail: "+e1.getMessage());
							}
							
							
							
						}else{
							System.out.println("Invalid file: "+args[1]);
							System.exit(0);
						}

					}else{
						System.out.println("Invalid mode.");
						System.exit(0);
					}


				}else{
					System.out.println("Invalid mode: "+args[0]);
					System.exit(0);
				}

			}else if(args == null || args.length ==0){ //check if is interactive mode


				System.out.println("\n\n"+Constants.APP_NAME+" "+Constants.APP_VERSION+"\nCommand Line Interface (CLI)\nhttps://github.com/Scicrop/sentinel-extractor\n\n");


				
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


				hist = "";
				if(aHistory != null) hist =  "["+aHistory.getThreadCheckerSleep()+"]";
				System.out.println("How much time (milliseconds) the downloader should wait for before kill a stalled connection? "+hist);
				
				String millis = keyboard.nextLine();
				long threadCheckerSleep = Constants.DEFAULT_THREAD_CHECKER_SLEEP;
				
				if(millis.equals("") && !hist.equals("")) threadCheckerSleep = aHistory.getThreadCheckerSleep();
				else try {
					threadCheckerSleep = Long.parseLong(millis);
				} catch (NumberFormatException e) {
					System.out.println("Invalid time. Using default: "+Constants.DEFAULT_THREAD_CHECKER_SLEEP);
				}

				hist = "";
				if(aHistory != null) hist =  "["+aHistory.getDownloadTriesLimit()+"]";
				System.out.println("How much tries will try the download? "+hist);
				String sDownload = keyboard.nextLine();
				int downloadTriesLimit = Constants.DEFAULT_DOWNLOAD_TRIES_LIMIT;
				
				if(sDownload.equals("") && !hist.equals("")) downloadTriesLimit = aHistory.getDownloadTriesLimit();
				else try {
					downloadTriesLimit = Integer.parseInt(sDownload);
				} catch (NumberFormatException e) {
					System.out.println("Invalid try number. Using default: "+Constants.DEFAULT_DOWNLOAD_TRIES_LIMIT);
				}
				
				

				
				

//				System.out.println("UDP Port? [10020] ");
//				String sUdpPort = keyboard.nextLine();
//				int udpPort = 10020;
//				if(sDownload != null || !sDownload.equals("")) udpPort = Integer.parseInt(sUdpPort);
				
				
				System.out.println("1) Open Search Query ");
				System.out.println("2) Open Data Query (by UUID)");
				System.out.println("3) Resume interrupted downloads");
				String searchType = keyboard.nextLine();

				if(aHistory == null) aHistory = new ArgumentsHistory();
				
				aHistory.setUser(user);
				aHistory.setPassword(password);
				aHistory.setOutputFolder(outputFolder);
				aHistory.setDownloadTriesLimit(downloadTriesLimit);
				aHistory.setLog(false);
				aHistory.setVerbose(true);
				aHistory.setThreadCheckerSleep(threadCheckerSleep);
				aHistory.setSentinel(sentinel);
				aHistory.setLogFolder(null);
				
				ActionBuilder.getInstance().manualSwitcher(aHistory, searchType);



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
