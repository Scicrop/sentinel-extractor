package com.scicrop.se.components;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scicrop.se.commons.dataobjects.ArgumentsHistory;
import com.scicrop.se.commons.dataobjects.EntryFileProperty;
import com.scicrop.se.commons.utils.Commons;
import com.scicrop.se.commons.utils.Constants;
import com.scicrop.se.commons.utils.LogHelper;
import com.scicrop.se.commons.utils.SentinelRuntimeException;
import com.scicrop.se.utils.OpenDataHelper;
import com.scicrop.se.utils.OpenSearchHelper;

public class ActionBuilder {

	private ActionBuilder(){}

	private static ActionBuilder INSTANCE = null;

	private static Log log = LogFactory.getLog(ActionBuilder.class);

	public static ActionBuilder getInstance(){
		if(INSTANCE == null) INSTANCE = new ActionBuilder();
		return INSTANCE;
	}

	public void manualSwitcher(ArgumentsHistory aHistory, String searchType) {

		Scanner keyboard = new Scanner(System.in);
		String clientUrl = null;

		String hist = null;

		switch (searchType) {
		case "1":

			hist = "";
			if(aHistory != null && aHistory.getClientUrl() != null) hist =  "["+aHistory.getClientUrl()+"]";
			System.out.println("Client url: "+hist);

			clientUrl = keyboard.nextLine();

			if(clientUrl.equals("") && !hist.equals("")) clientUrl = aHistory.getClientUrl();

			aHistory.setClientUrl(clientUrl);
			
			Commons.getInstance().writeArgumentsHistoryPropertyFile(aHistory);

			processClientUrl(aHistory);

			break;

		case "2":

			System.out.println("UUID: "); //8d6ad85e-9914-4a57-99e7-820a8c8996a1
			String uuid = keyboard.nextLine();


			Commons.getInstance().writeArgumentsHistoryPropertyFile(aHistory);

			try {
				OpenDataHelper.getInstance().getEdmByUUID(uuid, aHistory);
			} catch (SentinelRuntimeException e) {
				e.printStackTrace();
			}



			break;

		case "3":
			Commons.getInstance().writeArgumentsHistoryPropertyFile(aHistory);
			try {

				File oFolder = new File(aHistory.getOutputFolder());
				
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

					if(!content[i].equals(".ahistory.properties")){

						EntryFileProperty value = Commons.getInstance().readEntryPropertyFile(new File(aHistory.getOutputFolder() + content[i]));

						File f = new File(aHistory.getOutputFolder()+ value.getName());

						if(f.length() < value.getSize()){

							incompleteDownloadsCount++;

							System.out.println("Resuming file ("+incompleteDownloadsCount+") "+value.getName()+ " ["+value.getUuid()+"]");
							OpenDataHelper.getInstance().getEdmByUUID(value.getUuid(), aHistory);

							found = true;
						}
					}else System.out.println("Skipping file history properties file.");


				}


				if(!found){
					System.out.println("There are no files to be resumed in: "+aHistory.getOutputFolder());
				}

			} catch (SentinelRuntimeException e) {
				e.printStackTrace();
			}

			break;

		default:

			break;
		}
		
		keyboard.close();
	}

	private void processClientUrl(ArgumentsHistory aHistory) {
		Feed feed = null;
		try {
			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "Processing ...");
			feed = OpenSearchHelper.getInstance().getFeed(Constants.COPERNICUS_HOST, "", aHistory);
			
			String ip = Commons.getInstance().getIpByScicropUrl();
			QName trQn = new QName("http://a9.com/-/spec/opensearch/1.1/", "totalResults");
			QName ippQn = new QName("http://a9.com/-/spec/opensearch/1.1/", "itemsPerPage");
			int tr = Integer.parseInt(feed.getExtension(trQn).getText());
			int ipp = Integer.parseInt(feed.getExtension(ippQn).getText());
			int p = tr/ipp;

			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "Total Results: "+tr+" | Items per page: "+ipp+" | Pages: "+p);

			List<String> uuidLst = new ArrayList<String>();

			for(int item = 0; item < tr; item++){
				try{
					feed = OpenSearchHelper.getInstance().getFeed(Constants.COPERNICUS_HOST, "&start="+item+"&rows=10",  aHistory);
					System.out.print("Paging results: \t "+item+"/"+tr+" - \t UUID collected: "+uuidLst.size()+"\r");

					LogHelper.getInstance().handleVerboseLog(false, aHistory.isLog(), log, 'i', "Paging results: \t "+item+1+"/"+tr+" - \t UUID collected: "+uuidLst.size()+"\r");

					List<Entry> entries = feed.getEntries();

					//int notUniqueCounter = 0;

					for (Entry entry : entries) {
						if(!uuidLst.contains(entry.getId().toString())) uuidLst.add(entry.getId().toString());
					}
				}catch (Exception e){

				}

			}

			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "UUID LIST: \n");

			String allUuids = "";
			
			for(int e = 0; e < uuidLst.size(); e++){
				allUuids += uuidLst.get(e).toString() + ",";
				LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', e+")\t"+uuidLst.get(e));
			}
			Map<String,String> mapUUID = new HashMap<String,String>();
			allUuids = allUuids.substring(0,allUuids.length()-2);
			mapUUID.put("action", "saveInfo");
			mapUUID.put("uuid", allUuids);
			mapUUID.put("ip", ip);
			mapUUID.put("user", aHistory.getUser());

			try {
				Commons.getInstance().sendPost("https://scicrop.com/sentinel-extractor/feedback.php", mapUUID);
			} catch (Exception e1) {					
				LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'e', e1+")\t"+"Error trying send all uuids as feedback to scicrop server");
			}
			
			for(int e = 0; e < uuidLst.size(); e++){
				try {
					OpenDataHelper.getInstance().getEdmByUUID(uuidLst.get(e), aHistory);
				} catch (SentinelRuntimeException se) {
					LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'e', e+")\t"+uuidLst.get(e));
				}
			}

			
		} catch (SentinelRuntimeException e1) {
			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'e', e1.getMessage());
		} 
	}

	public void autoSearchDownload(ArgumentsHistory aHistory){
		processClientUrl(aHistory);
	}

}
