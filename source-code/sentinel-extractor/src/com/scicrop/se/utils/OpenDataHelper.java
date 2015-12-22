package com.scicrop.se.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;

public class OpenDataHelper {
	
	private OpenDataHelper(){}
	
	private static OpenDataHelper INSTANCE = null;
	
	public static OpenDataHelper getInstance(){
		if(INSTANCE == null) INSTANCE = new OpenDataHelper();
		return INSTANCE;
	}
	
	
	
	
	
	public void getEdmByUUID(String id, String user, String password) throws SentinelRuntimeException {

		
		InputStream content = null;
		Edm edm = null;
		ODataEntry entry = null;
		Long contentLength = 0l;
		String contentType = null;
		HashMap<String, Object> checksum = null;  
		String fileName = null;
		byte[] downloadBinary = null;
		
		try {
			content = Commons.getInstance().execute(Constants.COPERNICUS_ODATA_METALINK, Constants.APPLICATION_XML, Constants.HTTP_METHOD_GET, user, password);
			System.out.println("Open Data Metadata collected.");
			edm = EntityProvider.readMetadata(content, false);
			if(content !=null) content.close();
			entry = readEntry(edm, Constants.COPERNICUS_ODATA_ROOT, Constants.APPLICATION_XML, "Products", id, "?platformname=Sentinel-2", user, password);
			System.out.println("Open Data Entry collected.");
			
			Map<String, Object> propMap = entry.getProperties();
			
			Set<String> propMapKeySet = propMap.keySet();
			
			System.out.println("\n\n=========================================");

			contentLength = Long.parseLong(propMap.get("ContentLength").toString());
			contentType = propMap.get("ContentType").toString();
			
			checksum = (HashMap<String, Object>)propMap.get("Checksum");  
			
			fileName = propMap.get("Name")+".zip";
			
			System.out.println("Id: "+id);
			System.out.println("Checksum: "+checksum.get("Value"));
			System.out.println("Filename: "+fileName);
			System.out.println("ContentLength: "+contentLength);
			System.out.println("ContentType: "+contentType);
			
			System.out.println("=========================================\n\n");
			
			
			
			
		} catch (SentinelHttpConnectionException e) {
			e.printStackTrace();
		} catch (EntityProviderException e) {
			throw new SentinelRuntimeException(e);
		} catch (IOException e) {
			throw new SentinelRuntimeException(e);
		} catch (ODataException e) {
			throw new SentinelRuntimeException(e);
		}

		
		
		
		
			
		
		
		
		
		while(downloadBinary == null || downloadBinary.length < contentLength){
			
			downloadBinary = Commons.getInstance().getByteArrayFromUrlString("https://scihub.copernicus.eu/dhus/odata/v1/Products('"+id+"')/$value?platformname=Sentinel-2", "/tmp/"+fileName, contentLength, contentType, user, password);
			System.out.println("\n\nRetrying...\n\n");
		}

		
		
		
		
		
		
		
	}
	
	 public ODataEntry readEntry(Edm edm, String serviceUri, String contentType, String entitySetName, String keyValue, String params, String user, String password) throws IOException, ODataException, SentinelRuntimeException {

		    EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();

		    InputStream content = null;
			try {
				//content = Commons.getInstance().execute(serviceUri + entitySetName + "('"+keyValue+"')" + params, contentType, Constants.HTTP_METHOD_GET, user, password);
				//https://scihub.copernicus.eu/dhus/odata/v1/Products?$filter=Name%20eq%20%27S1A_IW_GRDH_1SDV_20151221T164750_20151221T164815_009143_00D274_014F%27
								
				content = Commons.getInstance().execute(serviceUri + entitySetName + "('"+keyValue+"')" + params, contentType, Constants.HTTP_METHOD_GET, user, password);
				
			} catch (SentinelHttpConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		    return EntityProvider.readEntry(contentType, entityContainer.getEntitySet(entitySetName), content, EntityProviderReadProperties.init().build());
	 }
	
	
}


