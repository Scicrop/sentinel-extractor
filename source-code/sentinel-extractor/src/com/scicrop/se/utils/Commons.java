package com.scicrop.se.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;

import com.scicrop.se.dataobjects.ArgumentsHistory;
import com.scicrop.se.dataobjects.EntryFileProperty;
import com.scicrop.se.http.SeHttpAuthenticator;
import com.scicrop.se.threads.ThreadChecker;

public class Commons {

	private Commons(){}

	private static Commons INSTANCE = null;

	public static Commons getInstance(){
		if(INSTANCE == null) INSTANCE = new Commons();
		return INSTANCE;
	}


	public void writeEntryFilePropertyFile(EntryFileProperty entryFileData, String folder){





		Properties prop = new Properties();
		OutputStream output = null;

		try {

			String filePath = folder + entryFileData.getName() + ".properties";

			File propFile = new File(filePath);

			if(!propFile.exists()){
				output = new FileOutputStream(filePath);

				// set the properties value
				prop.setProperty("name", entryFileData.getName());
				prop.setProperty("checksum", entryFileData.getMd5Checksum());
				prop.setProperty("uuid", entryFileData.getUuid());
				prop.setProperty("size", String.valueOf(entryFileData.getSize()));

				// save properties to project root folder
				prop.store(output, null);
			}else return;

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public EntryFileProperty readEntryPropertyFile(File source){

		EntryFileProperty ret = null;

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(source);

			prop.load(input);

			ret = new EntryFileProperty(prop.getProperty("name"), prop.getProperty("checksum"), prop.getProperty("uuid"), Long.parseLong(prop.getProperty("size")));


		} catch (IOException ex) {
			ex.printStackTrace();
		}catch (NumberFormatException nfe){
			
			System.out.println("Error trying to convert "+prop.getProperty("size")+" to Long.");
			
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}



	public String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

	public EntryFileProperty getMd5ByteArrayFromUrlString(String urlStr, String outputFileNamePath, long completeFileSize, String contentType, String user, String password) throws SentinelRuntimeException{

		ThreadChecker tChecker = new ThreadChecker(outputFileNamePath, completeFileSize);

		EntryFileProperty ret = null;
		URL url = null;
		HttpURLConnection connection = null;
		FileOutputStream fos = null;
		InputStream in = null;
		OutputStream bout = null;
		byte[] data = null;
		int bytesread = 0;
		int bytesBuffered = 0;
		RandomAccessFile randomAccessfile = null;

		long downloadedFileSize = 0;

		try {

			Authenticator.setDefault(new SeHttpAuthenticator(user, password));
			url = new URL(urlStr);
			connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestProperty(Constants.HTTP_HEADER_ACCEPT, contentType);

			File outFile = new File(outputFileNamePath);
			if(outFile.exists()){

				downloadedFileSize = outFile.length();

				if(downloadedFileSize < completeFileSize){
					System.out.println("Incomplete download. Resuming.");

					connection.setRequestProperty("Range", "bytes=" + downloadedFileSize + "-");

					connection.connect();
					try {
						checkStatus(connection);
						if(!tChecker.isAlive()) tChecker.start();
					} catch (SentinelHttpConnectionException e) {
						System.err.println("====> "+e.getMessage());
						return null;
					}

					long contentLength = connection.getContentLength();
					if (contentLength < 1) contentLength = completeFileSize;


					randomAccessfile = new RandomAccessFile(outputFileNamePath, "rw");
					randomAccessfile.seek(downloadedFileSize);

					in = connection.getInputStream();

					long downloadDiff = completeFileSize - downloadedFileSize;
					if (downloadDiff > Constants.BUFFER_SIZE) data = new byte[Constants.BUFFER_SIZE];
					else data = new byte[(int) downloadDiff];

					while( (bytesread = in.read( data )) > -1 ) {

						randomAccessfile.write(data, 0, bytesread);
						downloadedFileSize += bytesread;
						formatDownloadedProgress(completeFileSize, downloadedFileSize);
					}


				}else{
					System.out.println("File already downloaded.");
				}
			}else{

				System.out.println("Starting download.");
				connection.connect();
				try {
					checkStatus(connection);
					if(!tChecker.isAlive()) tChecker.start();
				} catch (SentinelHttpConnectionException e) {
					System.err.println(e.getMessage());
					return null;
				}

				in = connection.getInputStream();
				fos = new FileOutputStream(outputFileNamePath);
				bout = new BufferedOutputStream(fos, Constants.BUFFER_SIZE);
				data = new byte[Constants.BUFFER_SIZE];

				while( (bytesread = in.read( data )) > -1 ) {
					bout.write( data , 0, bytesread );
					bytesBuffered += bytesread;
					downloadedFileSize += bytesread;

					formatDownloadedProgress(completeFileSize, downloadedFileSize);


					if (bytesBuffered > 1024 * 1024) { //flush after 1MB
						bytesBuffered = 0;
						bout.flush();
					}
				}			



			}





		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{

			if(bout !=null){
				try {bout.flush(); } catch (IOException e) { e.printStackTrace(); }
				try {bout.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if(fos !=null){
				try {fos.flush(); } catch (IOException e) { e.printStackTrace(); }
				try {fos.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if(in !=null) try { in.close(); } catch (IOException e) { e.printStackTrace(); }

			if(randomAccessfile != null) try { randomAccessfile.close(); } catch (IOException e) { e.printStackTrace(); } 

			if(connection != null) connection.disconnect();

			if(tChecker.isAlive()) tChecker.forceStop();
		}


		ret = new EntryFileProperty(outputFileNamePath, bytesToHex(md5CheksumFromFilePath(outputFileNamePath)), null, downloadedFileSize);

		return ret;

	}






	public byte[] md5CheksumFromFilePath(String source){

		File fSource = new File(source);
		long size = fSource.length();
		byte[] byteArrayChecksum = null;
		MessageDigest md = null;
		InputStream is = null;
		try {
			md = MessageDigest.getInstance("MD5");
			is = new FileInputStream(fSource);

			byte[] dataBytes = new byte[1024];

			int nread = 0; 
			while ((nread = is.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			};
			byteArrayChecksum = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		System.out.println("MD5 checksum: "+bytesToHex(byteArrayChecksum) + " ["+size+"]");

		return byteArrayChecksum;
	}


	private void formatDownloadedProgress(long completeFileSize, long downloadedFileSize) {
		DecimalFormat dfa = new DecimalFormat("000.0");
		DecimalFormat dfb = new DecimalFormat("###,###,###,###");
		double currentProgress;
		String formatedProgress;
		currentProgress = ((((double)downloadedFileSize) * 100) / ((double)completeFileSize));
		formatedProgress = dfa.format(currentProgress)+"% "+dfb.format(downloadedFileSize) + " bytes";
		System.out.print(formatedProgress+"\r");
	}


	private void checkStatus(HttpURLConnection connection) throws SentinelRuntimeException, SentinelHttpConnectionException {
		HttpStatusCodes httpStatusCode = null;
		try {
			httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		} catch (IOException e) {
			throw new SentinelRuntimeException(e);
		}
		if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599) {
			throw new SentinelHttpConnectionException("Http Connection failed with status " + httpStatusCode.getStatusCode() + " " + httpStatusCode.toString() + " "+connection.getURL().toString());
		}else System.out.println("HTTP Response Code: "+httpStatusCode);

	}





	private HttpURLConnection initializeConnection(String absolutUri, String contentType, String httpMethod, String user, String password) throws SentinelRuntimeException {

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
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(absolutUri);
			connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestMethod(httpMethod);
			connection.setRequestProperty(Constants.HTTP_HEADER_ACCEPT, contentType);
			if(Constants.HTTP_METHOD_POST.equals(httpMethod) || Constants.HTTP_METHOD_PUT.equals(httpMethod)) {
				connection.setDoOutput(true);
				connection.setRequestProperty(Constants.HTTP_HEADER_CONTENT_TYPE, contentType);
			}
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");

		} catch (MalformedURLException e) {
			throw new SentinelRuntimeException(e);
		} catch (IOException e) {
			throw new SentinelRuntimeException(e);
		} finally {
			if(connection != null) connection.disconnect();
		}


		return connection;
	}


	public InputStream execute(String relativeUri, String contentType, String httpMethod, String user, String password) throws SentinelRuntimeException, SentinelHttpConnectionException  {
		HttpURLConnection connection = null;
		InputStream content = null;
		try {
			connection = initializeConnection(relativeUri, contentType, httpMethod, user, password);
			connection.connect();
			checkStatus(connection);
			content = connection.getInputStream();

		} catch (IOException e) {
			throw new SentinelRuntimeException(e);
		} 

		return content;
	}

	public byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}


	public void saveArgumentsHistory(String user, String outputFolder, String clientUrl, String sentinel, ArgumentsHistory oldAhistory) {

		try {
			if(oldAhistory != null){

				if(user == null) user = oldAhistory.getUser();
				if(outputFolder == null) outputFolder = oldAhistory.getOutputFolder();
				if(clientUrl == null) clientUrl = oldAhistory.getClientUrl();
				if(sentinel == null) sentinel = oldAhistory.getSentinel();

			}else{
				if(user == null) user = "";
				if(outputFolder == null) outputFolder = "";
				if(clientUrl == null) clientUrl = "";
				if(sentinel == null) sentinel = "";
			}
			ArgumentsHistory aHistory = new ArgumentsHistory(user, outputFolder, sentinel, clientUrl, null);
			writeArgumentsHistoryPropertyFile(aHistory);
		} catch (NullPointerException e) {
			System.out.println("Impossible to write ArgumentsHistory property file.");
			e.printStackTrace();
		}
		
		
	}


	private void writeArgumentsHistoryPropertyFile(ArgumentsHistory aHistory) {
		String userDir = System.getProperty("user.dir") + "/";
		Properties prop = new Properties();
		OutputStream output = null;

		try {

			String filePath = userDir + ".ahistory" + ".properties";

			File propFile = new File(filePath);

			if(propFile.exists()) propFile.delete();
			output = new FileOutputStream(filePath);

			// set the properties value
			prop.setProperty("user", aHistory.getUser());
			prop.setProperty("outputfolder", aHistory.getOutputFolder());
			prop.setProperty("clienturl", aHistory.getClientUrl());
			prop.setProperty("sentinel", aHistory.getSentinel());

			prop.store(output, null);


		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}


	public ArgumentsHistory readArgumentsHistoryPropertyFile() {
		
		String userDir = System.getProperty("user.dir") + "/";
		String filePath = userDir + ".ahistory" + ".properties";
		
		return readArgumentsHistoryPropertyFile(filePath);
	}

	public ArgumentsHistory readArgumentsHistoryPropertyFile(String filePath) {
		ArgumentsHistory ret = null;

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(filePath);

			prop.load(input);

			ret = new ArgumentsHistory(prop.getProperty("user"), prop.getProperty("outputfolder"), prop.getProperty("sentinel"), prop.getProperty("clienturl"), prop.getProperty("socketport"));


		} catch (FileNotFoundException ex) {

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
}
