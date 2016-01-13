package com.scicrop.se.runtime;

import java.io.File;
import java.util.Scanner;

import com.scicrop.se.commons.dataobjects.ArgumentsHistory;
import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.utils.Commons;
import com.scicrop.se.commons.utils.LogHelper;
import com.scicrop.se.components.ActionBuilder;
import com.scicrop.se.net.SeUdpClient;
import com.scicrop.se.threads.ActionBuilderThread;



public class Launch {

	public static Payload STATUS = null;
	public static String CONF_PARAM = null;
	
	public static void main(String[] args) {


		System.out.println("\n\nSentinel Extractor 0.2.0\nCommand Line Interface (CLI)\nhttps://github.com/Scicrop/sentinel-extractor\n\n");

		ArgumentsHistory aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile();

		if(args != null && args.length > 0 && args[0] !=null && (new File(args[0].trim()).exists()) && (new File(args[0].trim()).isFile())) {

			aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile(args[0].trim());
			
			CONF_PARAM = args[0].trim();

			LogHelper.getInstance().setLogger(aHistory.getSocketPort());
			

				Thread t = new SeUdpClient(9001);
				t.start();


			
			Thread actionBuilderThread = new ActionBuilderThread(aHistory);
			actionBuilderThread.start();

		}else{

			String clientUrl = null;
			File oFolder = null;
			Scanner keyboard = new Scanner(System.in);

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



		}




	}



}
