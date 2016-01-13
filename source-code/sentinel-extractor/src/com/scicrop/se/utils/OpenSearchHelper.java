package com.scicrop.se.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.net.NetUtils;
import com.scicrop.se.runtime.Launch;

public class OpenSearchHelper {
	
	private OpenSearchHelper(){}
	
	private static OpenSearchHelper INSTANCE = null;
	
	public static OpenSearchHelper getInstance(){
		if(INSTANCE == null) INSTANCE = new OpenSearchHelper();
		return INSTANCE;
	}
	
	public Feed getFeed(String host, String clientUrl, String sentinel, String compl, String user, String password) throws IOException {

		Launch.STATUS = new Payload(NetUtils.SentinelExtractorStatus.PROCESSING_QUERY, null, -1, Launch.CONF_PARAM);

		Abdera abdera = new Abdera();
		Feed ret = null;
		AbderaClient client = new AbderaClient(abdera);
		client.usePreemptiveAuthentication(true);

		try {
			client.addCredentials(host,  null, null,  new UsernamePasswordCredentials(user, password));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		AbderaClient.registerTrustManager();

		
		//https://scihub.copernicus.eu/dhus/search?q=footprint:%22Intersects(POLYGON((-4.53%2029.85,26.75%2029.85,26.75%2046.80,-4.53%2046.80,-4.53%2029.85)))%22&platformname=Sentinel-2
		
		
		int end = 43;
		
		clientUrl= clientUrl + sentinel+compl;
		
		String init = clientUrl.substring(0, end);
		String last  = clientUrl.substring(end);
		
		
		
		clientUrl = init + URLEncoder.encode(last, "UTF-8");
		
		
		
		ClientResponse resp = client.get(clientUrl);
		if (resp.getType() == ResponseType.SUCCESS) {
			Document<Feed> doc = resp.getDocument();
			ret = doc.getRoot();			
		} else {
			System.out.println("error");
		}	
		
		
		
		return ret;

	}

}
