package com.scicrop.se.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.scicrop.se.commons.dataobjects.ArgumentsHistory;
import com.scicrop.se.commons.dataobjects.EntryFileProperty;
import com.scicrop.se.commons.utils.Commons;
import com.scicrop.se.commons.utils.Constants;
import com.scicrop.se.commons.utils.LogHelper;
import com.scicrop.se.commons.utils.SentinelHttpConnectionException;
import com.scicrop.se.commons.utils.SentinelRuntimeException;

public class OpenDataHelper {

	private OpenDataHelper(){}

	private static OpenDataHelper INSTANCE = null;
	
	private static Log log = LogFactory.getLog(OpenDataHelper.class);

	public static OpenDataHelper getInstance(){
		if(INSTANCE == null) INSTANCE = new OpenDataHelper();
		return INSTANCE;
	}





	public void getEdmByUUID(String id, ArgumentsHistory aHistory) throws SentinelRuntimeException {


		InputStream content = null;
		Edm edm = null;
		Long contentLength = 0l;
		String contentType = null;
		HashMap<String, Object> checksum = null;  
		String fileName = null;
		EntryFileProperty entryFp = null;
		String hexChecksum = null;
		ODataEntry entry = null;

		try {
			content = DownloadHelper.getInstance().execute(Constants.COPERNICUS_ODATA_METALINK, Constants.APPLICATION_XML, Constants.HTTP_METHOD_GET,  aHistory.getUser(), aHistory.getPassword());
			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "Open Data Metadata collected.");
			edm = EntityProvider.readMetadata(content, false);
			if(content !=null) content.close();
			entry = readEntry(edm, Constants.COPERNICUS_ODATA_ROOT, Constants.APPLICATION_XML, "Products", id, "?platformname=Sentinel-"+aHistory.getSentinel(),  aHistory.getUser(), aHistory.getPassword());
			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "Open Data Entry collected.");



			Map<String, Object> propMap = entry.getProperties();

			Set<String> propMapKeySet = propMap.keySet();

			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "=========================================");


			contentLength = Long.parseLong(propMap.get("ContentLength").toString());
			contentType = propMap.get("ContentType").toString();

			checksum = (HashMap<String, Object>)propMap.get("Checksum");  

			fileName = propMap.get("Name")+".zip";

			hexChecksum = checksum.get("Value").toString();

			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "Id: "+id);
			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "Checksum: "+hexChecksum);
			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "Filename: "+fileName);
			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "ContentLength: "+contentLength);
			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "ContentType: "+contentType);

			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i', "=========================================\n\n");

			Map<String,String> mapUUID = new HashMap<String,String>();
			mapUUID.put("action", "updateFilename");
			mapUUID.put("filename", fileName);
			mapUUID.put("user", aHistory.getUser());
			mapUUID.put("clienturl", aHistory.getClientUrl());
			
			
			Commons.getInstance().writeEntryFilePropertyFile(new EntryFileProperty(fileName, hexChecksum, id, contentLength), aHistory.getOutputFolder());

		} catch (SentinelHttpConnectionException e) {
			e.printStackTrace();
		} catch (EntityProviderException e) {
			throw new SentinelRuntimeException(e);
		} catch (IOException e) {
			throw new SentinelRuntimeException(e);
		} catch (ODataException e) {
			throw new SentinelRuntimeException(e);
		}







		int tries = 1;
		

		while((entryFp == null ||  entryFp.getSize() != contentLength)){
			LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i',"Try: "+tries);

			entryFp = DownloadHelper.getInstance().getEntryFilePropertyFromUrlString("https://scihub.copernicus.eu/dhus/odata/v1/Products('"+id+"')/$value?platformname=Sentinel-2", fileName, contentLength, contentType, aHistory);

			if(entryFp !=null && entryFp.getSize() == contentLength) break;
			else{

				try {
					LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i',"Try "+tries+" did not work. Please wait 30s...");
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(tries > aHistory.getDownloadTriesLimit() && (entryFp == null ||  entryFp.getSize() != contentLength)){
					LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i',"Skiping...");
					break;
				}
				tries ++;
			}

		}

		if(tries > aHistory.getDownloadTriesLimit()) LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i',"Resuming tries for file "+fileName+" did not work.");
		else{
			if(entryFp.getMd5Checksum().equalsIgnoreCase(hexChecksum)) LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i',"Filename: "+fileName+ " downloaded and checked "+hexChecksum);
			else LogHelper.getInstance().handleVerboseLog(aHistory.isVerbose(), aHistory.isLog(), log, 'i',"Filename: "+fileName+ " downloaded [INVALID CHECKSUM]");
		}


	}

	public ODataEntry readEntry(Edm edm, String serviceUri, String contentType, String entitySetName, String keyValue, String params, String user, String password) throws IOException, ODataException, SentinelRuntimeException {

		EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();

		InputStream content = null;

		ODataEntry ode = null;
		try {
			//content = Commons.getInstance().execute(serviceUri + entitySetName + "('"+keyValue+"')" + params, contentType, Constants.HTTP_METHOD_GET, user, password);
			//https://scihub.copernicus.eu/dhus/odata/v1/Products?$filter=Name%20eq%20%27S1A_IW_GRDH_1SDV_20151221T164750_20151221T164815_009143_00D274_014F%27

			content = DownloadHelper.getInstance().execute(serviceUri + entitySetName + "('"+keyValue+"')" + params, contentType, Constants.HTTP_METHOD_GET, user, password);
			ode = EntityProvider.readEntry(contentType, entityContainer.getEntitySet(entitySetName), content, EntityProviderReadProperties.init().build());




		} catch (SentinelHttpConnectionException e) {
			e.printStackTrace();
		}



		return ode;
	}


}


