package com.scicrop.se.utils;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scicrop.se.commons.dataobjects.ArgumentsHistory;
import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.net.NetUtils;
import com.scicrop.se.commons.utils.SentinelRuntimeException;
import com.scicrop.se.components.ActionBuilder;
import com.scicrop.se.runtime.Launch;

public class OpenSearchHelper {

	private OpenSearchHelper(){}

	private static Log log = LogFactory.getLog(ActionBuilder.class);

	private static OpenSearchHelper INSTANCE = null;

	public static OpenSearchHelper getInstance(){
		if(INSTANCE == null) INSTANCE = new OpenSearchHelper();
		return INSTANCE;
	}

	public Feed getFeed(String host, String compl, ArgumentsHistory aHistory) throws SentinelRuntimeException {
		Feed ret = null;
		try {
			Launch.STATUS = new Payload(NetUtils.SentinelExtractorStatus.PROCESSING_QUERY, null, -1, Launch.CONF_PARAM, new Date().getTime());

			Abdera abdera = new Abdera();

			AbderaClient client = new AbderaClient(abdera);
			client.usePreemptiveAuthentication(true);


			client.addCredentials(host,  null, null,  new UsernamePasswordCredentials(aHistory.getUser(), aHistory.getPassword()));


			AbderaClient.registerTrustManager();


			//https://scihub.copernicus.eu/dhus/search?q=footprint:%22Intersects(POLYGON((-4.53%2029.85,26.75%2029.85,26.75%2046.80,-4.53%2046.80,-4.53%2029.85)))%22&platformname=Sentinel-2


			int end = 43;

			String clientUrl = aHistory.getClientUrl();

			clientUrl = clientUrl + aHistory.getSentinel()+compl;

			String init = clientUrl.substring(0, end);
			String last  = clientUrl.substring(end);



			try {
				clientUrl = init + URLEncoder.encode(last, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new SentinelRuntimeException(e);
			}



			ClientResponse resp = client.get(clientUrl);
						
			if (resp.getType() == ResponseType.SUCCESS) {
				Document<Feed> doc = resp.getDocument(); 
				ret = doc.getRoot();			
			} else {
				throw new SentinelRuntimeException("Wrong response from an OpenSearch query: "+resp.getType()+" ["+resp.getStatus()+"]");
			}	

		} catch (URISyntaxException e) {
			throw new SentinelRuntimeException(e);
		}

		return ret;

	}

}
