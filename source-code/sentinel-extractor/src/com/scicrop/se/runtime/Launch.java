package com.scicrop.se.runtime;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

import com.scicrop.se.components.ActionBuilder;
import com.scicrop.se.dataobjects.ArgumentsHistory;
import com.scicrop.se.dataobjects.EntryFileProperty;
import com.scicrop.se.net.SeSocketServer;
import com.scicrop.se.utils.Commons;
import com.scicrop.se.utils.Constants;
import com.scicrop.se.utils.OpenDataHelper;
import com.scicrop.se.utils.OpenSearchHelper;
import com.scicrop.se.utils.SentinelRuntimeException;

public class Launch {

	public static void main(String[] args) {


		System.out.println("\n\nSentinel Extractor 0.1.3\nCommand Line Interface (CLI)\nhttps://github.com/Scicrop/sentinel-extractor\n\n");

		ArgumentsHistory aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile();

		if(args != null && args[0] !=null && (new File(args[0].trim()).exists()) && (new File(args[0].trim()).isFile())) {

			aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile(args[0].trim());

			try {
				Thread t = new SeSocketServer(Integer.parseInt(aHistory.getSocketPort()));
				t.start();
			}catch(IOException e) {
				e.printStackTrace();
			}
			
			ActionBuilder.getInstance().autoSearchDownload(aHistory);

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
