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

import com.scicrop.se.dataobjects.ArgumentsHistory;
import com.scicrop.se.dataobjects.EntryFileProperty;
import com.scicrop.se.utils.Commons;
import com.scicrop.se.utils.Constants;
import com.scicrop.se.utils.OpenDataHelper;
import com.scicrop.se.utils.OpenSearchHelper;
import com.scicrop.se.utils.SentinelRuntimeException;

public class Launch {

	public static void main(String[] args) {


		System.out.println("\n\nSentinel Extractor 0.1.2\nCommand Line Interface (CLI)\nhttps://github.com/Scicrop/sentinel-extractor\n\n");

		ArgumentsHistory aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile();

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

		hist = "";
		if(aHistory != null) hist =  "["+aHistory.getClientUrl()+"]";

		System.out.println("1) Open Search Query ");
		System.out.println("2) Open Data Query (by UUID)");
		System.out.println("3) Resume interrupted downloads");
		String searchType = keyboard.nextLine();

		switch (searchType) {
		case "1":

			System.out.println("Client url "+hist);

			clientUrl = keyboard.nextLine();

			if(clientUrl.equals("") && !hist.equals("")) clientUrl = aHistory.getClientUrl();

			Commons.getInstance().saveArgumentsHistory(user, outputFolder, clientUrl, sentinel, aHistory);

			Feed feed = null;
			try {
				System.out.println("Processing ...");
				feed = OpenSearchHelper.getInstance().getFeed(Constants.COPERNICUS_HOST, clientUrl, sentinel, "", user, password);

				QName trQn = new QName("http://a9.com/-/spec/opensearch/1.1/", "totalResults");
				QName ippQn = new QName("http://a9.com/-/spec/opensearch/1.1/", "itemsPerPage");
				int tr = Integer.parseInt(feed.getExtension(trQn).getText());
				int ipp = Integer.parseInt(feed.getExtension(ippQn).getText());
				int p = tr/ipp;

				System.out.println("Total Results: "+tr+" | Items per page: "+ipp+" | Pages: "+p);

				List<String> uuidLst = new ArrayList<String>();

				for(int item = 0; item < tr; item++){
					try{
						feed = OpenSearchHelper.getInstance().getFeed(Constants.COPERNICUS_HOST, clientUrl, sentinel, "&start="+item+"&rows=10", user, password);
						System.out.print("Paging results: \t "+item+"/"+tr+" - \t UUID collected: "+uuidLst.size()+"\r");

						List<Entry> entries = feed.getEntries();

						//int notUniqueCounter = 0;

						for (Entry entry : entries) {
							if(!uuidLst.contains(entry.getId().toString())) uuidLst.add(entry.getId().toString());
						}
					}catch (Exception e){

					}

				}

				System.out.println("\n\nUUID LIST: \n");

				for(int e = 0; e < uuidLst.size(); e++){
					System.out.println(e+")\t"+uuidLst.get(e));
				}

				for(int e = 0; e < uuidLst.size(); e++){
					try {
						OpenDataHelper.getInstance().getEdmByUUID(uuidLst.get(e), user, password, outputFolder, sentinel);
					} catch (SentinelRuntimeException se) {
						se.printStackTrace();
					}
				}


			} catch (IOException e1) {
				e1.printStackTrace();
			}

			break;

		case "2":

			System.out.println("UUID: "); //8d6ad85e-9914-4a57-99e7-820a8c8996a1
			String uuid = keyboard.nextLine();


			Commons.getInstance().saveArgumentsHistory(user, outputFolder, clientUrl, sentinel, aHistory);

			try {
				OpenDataHelper.getInstance().getEdmByUUID(uuid, user, password, outputFolder, sentinel);
			} catch (SentinelRuntimeException e) {
				e.printStackTrace();
			}



			break;

		case "3":
			Commons.getInstance().saveArgumentsHistory(user, outputFolder, clientUrl, sentinel, aHistory);
			try {

				String[] content = oFolder.list(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {

						boolean ret = false;
						if(name.lastIndexOf('.')>0)
						{
							// get last index for '.' char
							int lastIndex = name.lastIndexOf('.');

							// get extension
							String str = name.substring(lastIndex);

							// match path name extension
							if(str.equals(".properties"))
							{
								ret =  true;
							} 
						}
						return ret;
					}
				});

				boolean found = false;

				int incompleteDownloadsCount = 0;

				for (int i=0; i < content.length; i++) {

					EntryFileProperty value = Commons.getInstance().readEntryPropertyFile(new File(outputFolder + content[i]));

					File f = new File(outputFolder+ value.getName());

					if(f.length() < value.getSize()){

						incompleteDownloadsCount++;

						System.out.println("Resuming file ("+incompleteDownloadsCount+") "+value.getName()+ " ["+value.getUuid()+"]");
						OpenDataHelper.getInstance().getEdmByUUID(value.getUuid(), user, password, outputFolder, sentinel);

						found = true;
					}



				}


				if(!found){
					System.out.println("There are no files to be resumed in: "+outputFolder);
				}

			} catch (SentinelRuntimeException e) {
				e.printStackTrace();
			}

			break;

		default:

			break;
		}








	}

}
