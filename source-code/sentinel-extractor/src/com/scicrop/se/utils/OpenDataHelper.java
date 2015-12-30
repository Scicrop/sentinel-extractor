package com.scicrop.se.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.scicrop.se.dataobjects.EntryFileProperty;

public class OpenDataHelper {

	private OpenDataHelper(){}

	private static OpenDataHelper INSTANCE = null;

	public static OpenDataHelper getInstance(){
		if(INSTANCE == null) INSTANCE = new OpenDataHelper();
		return INSTANCE;
	}





	public void getEdmByUUID(String id, String user, String password, String outputFolder, String sentinel) throws SentinelRuntimeException {


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
			content = Commons.getInstance().execute(Constants.COPERNICUS_ODATA_METALINK, Constants.APPLICATION_XML, Constants.HTTP_METHOD_GET, user, password);
			System.out.println("Open Data Metadata collected.");
			edm = EntityProvider.readMetadata(content, false);
			if(content !=null) content.close();
			entry = readEntry(edm, Constants.COPERNICUS_ODATA_ROOT, Constants.APPLICATION_XML, "Products", id, "?platformname=Sentinel-"+sentinel, user, password);
			System.out.println("Open Data Entry collected.");



			Map<String, Object> propMap = entry.getProperties();

			Set<String> propMapKeySet = propMap.keySet();

			System.out.println("\n\n=========================================");


			contentLength = Long.parseLong(propMap.get("ContentLength").toString());
			contentType = propMap.get("ContentType").toString();

			checksum = (HashMap<String, Object>)propMap.get("Checksum");  

			fileName = propMap.get("Name")+".zip";

			hexChecksum = checksum.get("Value").toString();

			System.out.println("Id: "+id);
			System.out.println("Checksum: "+hexChecksum);
			System.out.println("Filename: "+fileName);
			System.out.println("ContentLength: "+contentLength);
			System.out.println("ContentType: "+contentType);

			System.out.println("=========================================\n\n");

			Commons.getInstance().writeEntryFilePropertyFile(new EntryFileProperty(fileName, hexChecksum, id, contentLength), outputFolder);

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
		int tryLimit = 3;

		while((entryFp == null ||  entryFp.getSize() != contentLength)){
			System.out.println("\n\nTry: "+tries);

			entryFp = Commons.getInstance().getMd5ByteArrayFromUrlString("https://scihub.copernicus.eu/dhus/odata/v1/Products('"+id+"')/$value?platformname=Sentinel-2", outputFolder+fileName, contentLength, contentType, user, password);

			if(entryFp !=null && entryFp.getSize() == contentLength) break;
			else{

				try {
					System.out.println("\n\nTry "+tries+" did not work. Please wait 30s...");
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(tries > tryLimit && (entryFp == null ||  entryFp.getSize() != contentLength)){
					System.out.println("Skiping...");
					break;
				}
				tries ++;
			}

		}

		if(tries > tryLimit) System.out.println("Resuming tries for file "+fileName+" did not work.");
		else{
			if(entryFp.getMd5Checksum().equalsIgnoreCase(hexChecksum)) System.out.println("Filename: "+fileName+ " downloaded and checked "+hexChecksum);
			else System.out.println("Filename: "+fileName+ " downloaded [INVALID CHECKSUM]");
		}


	}

	public ODataEntry readEntry(Edm edm, String serviceUri, String contentType, String entitySetName, String keyValue, String params, String user, String password) throws IOException, ODataException, SentinelRuntimeException {

		EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();

		InputStream content = null;
		InputStream copy = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ODataEntry ode = null;
		try {
			//content = Commons.getInstance().execute(serviceUri + entitySetName + "('"+keyValue+"')" + params, contentType, Constants.HTTP_METHOD_GET, user, password);
			//https://scihub.copernicus.eu/dhus/odata/v1/Products?$filter=Name%20eq%20%27S1A_IW_GRDH_1SDV_20151221T164750_20151221T164815_009143_00D274_014F%27

			content = Commons.getInstance().execute(serviceUri + entitySetName + "('"+keyValue+"')" + params, contentType, Constants.HTTP_METHOD_GET, user, password);
			ode = EntityProvider.readEntry(contentType, entityContainer.getEntitySet(entitySetName), content, EntityProviderReadProperties.init().build());




		} catch (SentinelHttpConnectionException e) {
			e.printStackTrace();
		}



		return ode;
	}


}


