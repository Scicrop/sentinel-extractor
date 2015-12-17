package com.scicrop.se.tests;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.junit.Test;

import com.scicrop.se.http.SeHttpAuthenticator;

public class TestOdataClient {
	
	public static final String HTTP_METHOD_PUT = "PUT";
	  public static final String HTTP_METHOD_POST = "POST";
	  public static final String HTTP_METHOD_GET = "GET";
	  private static final String HTTP_METHOD_DELETE = "DELETE";

	  public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	  public static final String HTTP_HEADER_ACCEPT = "Accept";

	  public static final String APPLICATION_JSON = "application/json";
	  public static final String APPLICATION_XML = "application/xml";
	  public static final String APPLICATION_ATOM_XML = "application/atom+xml";
	  public static final String APPLICATION_FORM = "application/x-www-form-urlencoded";
	  public static final String METADATA = "$metadata";
	  public static final String SEPARATOR = "/";

	  public static final boolean PRINT_RAW_CONTENT = true;

	  
	  public static String user = "guest";
	  public static String password = "guest";

	@Test
	public void testAbdera() throws IOException {
		//	Authenticator.setDefault(new SeHttpAuthenticator("guest", "guest"));

		try {
			getEdm();
		} catch (EntityProviderException | EdmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Abdera abdera = new Abdera();



		AbderaClient client = new AbderaClient(abdera);

		client.usePreemptiveAuthentication(true);


		try {
			client.addCredentials("https://scihub.copernicus.eu",  null, null,  new UsernamePasswordCredentials( user,password));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AbderaClient.registerTrustManager();

		ClientResponse resp = client.get("https://scihub.copernicus.eu/dhus/search?q=footprint:%22Intersects(POLYGON((-4.53%2029.85,26.75%2029.85,26.75%2046.80,-4.53%2046.80,-4.53%2029.85)))%22&platformname=Sentinel-2");
		if (resp.getType() == ResponseType.SUCCESS) {
			Document<Feed> doc = resp.getDocument();

			Feed feed = doc.getRoot();
			List<Entry> entries = feed.getEntries();
			for (Entry entry : entries) {
				List<Link> links = entry.getLinks();
				String url = "https://scihub.copernicus.eu/dhus/odata/v1/Products('"+entry.getId().toString()+"')?platformname=Sentinel-2";
				System.out.println("==== "+url);
				//				try {
				//					//getOdataProduct(url);
				//				} catch (EntityProviderException | EdmException e) {
				//					// TODO Auto-generated catch block
				//					e.printStackTrace();
				//				}


			}


		} else {
			System.out.println("error");
		}	

	}

	@Test
	public void getEdm() throws IOException, EntityProviderException, EdmException, KeyManagementException, NoSuchAlgorithmException {

		//Authenticator.setDefault(new SeHttpAuthenticator("guest", "guest"));

		//URL url=new URL("https://scihub.copernicus.eu/dhus/odata/v1/$metadata?platformname=Sentinel-2");
		
		InputStream content = execute("https://scihub.copernicus.eu/dhus/odata/v1/$metadata", APPLICATION_XML, HTTP_METHOD_GET);
		//Edm edm =  EntityProvider.readMetadata(content, false);

//		HttpURLConnection conn=(HttpURLConnection) url.openConnection();
//
//
//		//         long completeFileSize = conn.getContentLength();
//		//
//		//         System.out.println("===> "+completeFileSize);
//		//         
//		//         java.io.BufferedInputStream in = new java.io.BufferedInputStream(conn.getInputStream());
//		//         java.io.FileOutputStream fos = new java.io.FileOutputStream("/tmp/package.zip");
//		//         java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
//		//         byte[] data = new byte[1024];
//		//         long downloadedFileSize = 0;
//		//         int x = 0;
//		//         while ((x = in.read(data, 0, 1024)) >= 0) {
//		//             downloadedFileSize += x;
//		//
//		//
//		//             bout.write(data, 0, x);
//		//             System.out.println(downloadedFileSize);
//		//         }
//		//         bout.close();
//		//         in.close();
//
//
//
//		conn.setRequestMethod("GET");
//		conn.setRequestProperty(HttpHeaders.ACCEPT,HttpContentType.APPLICATION_XML);
//		conn.connect();
//		InputStream content=conn.getInputStream();
//		Edm edm = EntityProvider.readMetadata(content, false);
		//List<EdmEntitySet> eSets = edm.getEntitySets();
		//System.out.println("---- "+eSets.size());




	}


	private InputStream execute(String relativeUri, String contentType, String httpMethod) throws KeyManagementException, NoSuchAlgorithmException  {
		HttpURLConnection connection = null;
		InputStream content = null;
		try {
			connection = initializeConnection(relativeUri, contentType, httpMethod);
			connection.connect();
			//checkStatus(connection);

			content = connection.getInputStream();
			content = logRawContent(httpMethod + " request:\n  ", content, "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return content;
	}

	private HttpURLConnection connect(String relativeUri, String contentType, String httpMethod) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

		connection.connect();
		checkStatus(connection);

		return connection;
	}

	private HttpURLConnection initializeConnection(String absolutUri, String contentType, String httpMethod) throws MalformedURLException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
//		// Create a trust manager that does not validate certificate chains
//        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            }
//        };
// 
//        // Install the all-trusting trust manager
//        SSLContext sc = SSLContext.getInstance("SSL");
//        sc.init(null, trustAllCerts, new java.security.SecureRandom());
//        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
// 
//        // Create all-trusting host name verifier
//        HostnameVerifier allHostsValid = new HostnameVerifier() {
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        };
// 
//        // Install the all-trusting host verifier
//        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		
		Authenticator.setDefault(new SeHttpAuthenticator(user, password));
		URL url = new URL(absolutUri);
		HttpURLConnection connection = (HttpsURLConnection) url.openConnection();

		connection.setRequestMethod(httpMethod);
		connection.setRequestProperty(HTTP_HEADER_ACCEPT, contentType);
		if(HTTP_METHOD_POST.equals(httpMethod) || HTTP_METHOD_PUT.equals(httpMethod)) {
			connection.setDoOutput(true);
			connection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, contentType);
		}
		
		
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
		

		return connection;
	}

	private HttpStatusCodes checkStatus(HttpURLConnection connection) throws IOException {
		HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599) {
			throw new RuntimeException("Http Connection failed with status " + httpStatusCode.getStatusCode() + " " + httpStatusCode.toString());
		}
		return httpStatusCode;
	}

	private InputStream logRawContent(String prefix, InputStream content, String postfix) throws IOException {
		if(PRINT_RAW_CONTENT) {
			byte[] buffer = streamToArray(content);
			content.close();

			print(prefix + new String(buffer) + postfix);

			return new ByteArrayInputStream(buffer);
		}
		return content;
	}

	private byte[] streamToArray(InputStream stream) throws IOException {
		byte[] result = new byte[0];
		byte[] tmp = new byte[8192];
		int readCount = stream.read(tmp);
		while(readCount >= 0) {
			byte[] innerTmp = new byte[result.length + readCount];
			System.arraycopy(result, 0, innerTmp, 0, result.length);
			System.arraycopy(tmp, 0, innerTmp, result.length, readCount);
			result = innerTmp;
			readCount = stream.read(tmp);
		}
		return result;
	}

	private static void print(String content) {
		System.out.println(content);
	}

	

	

	

}
