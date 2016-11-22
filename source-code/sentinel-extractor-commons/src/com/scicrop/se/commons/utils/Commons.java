package com.scicrop.se.commons.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;

import com.scicrop.se.commons.dataobjects.ArgumentsHistory;
import com.scicrop.se.commons.dataobjects.EntryFileProperty;

public class Commons {

	private Commons(){}

	private static Commons INSTANCE = null;

	public static Commons getInstance(){
		if(INSTANCE == null) INSTANCE = new Commons();
		return INSTANCE;
	}

	private String transformParameters(Map<String,String> map){
		String ret = "";
		if(map!= null && map.size() > 0){
			ret = "";
			for (Map.Entry<String, String> entry : map.entrySet())
			{
				ret += entry.getKey() + "=" + entry.getValue() + "&";
			}
			ret = ret.substring(0,ret.length()-1); 
		}
		return ret;
	}
	
	// HTTP GET request
	public void sendGet(String url, Map<String,String> map) throws Exception {

		if(map != null && map.size() > 0){
			url += transformParameters(map);
		}
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod(Constants.HTTP_METHOD_GET);

		//add request header
		con.setRequestProperty("User-Agent", Constants.USER_AGENT);

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
	}
	
	// HTTP POST request
	public void sendPost(String url, Map<String,String> map) throws IOException  {
		URL uurl = new URL(url);
	    URLConnection conn = uurl.openConnection();
	    conn.setDoOutput(true);
	    
	    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

	    writer.write(Commons.getInstance().transformParameters(map));
	    writer.flush();
	    String line;
	    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    while ((line = reader.readLine()) != null) {
	      System.out.println(line);
	    }
	    writer.close();
	    reader.close();
	}
	
	public String getIpByUrl() throws SentinelRuntimeException{
		String ret= "";
		try {
			URL whatismyip = new URL(Constants.MYIP_URL);
		    BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		    ret = in.readLine();
		} catch (Exception e) {
			throw new SentinelRuntimeException("Cannot reach "+Constants.MYIP_URL+".");
		}
	    
	    return ret;
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

			System.out.println("Error trying to convert "+prop.getProperty("size")+" to Long. ["+source+"]");

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





	public void writeArgumentsHistoryPropertyFile(ArgumentsHistory aHistory) {

		/**
		 * user=guest
			password=guest
			outputfolder=/tmp 
			sentinel=1 
			clienturl=https\://scihub.copernicus.eu/dhus/search?q\=( footprint\:"Intersects(POLYGON((-76.3414718174573 -32.856332574865085,-30.509495780872072 -32.856332574865085,-30.509495780872072 3.9403723089632052,-76.3414718174573 3.9403723089632052,-76.3414718174573 -32.856332574865085)))" ) AND ( beginPosition\:[2015-09-01T00\:00\:00.000Z TO 2015-12-30T23\:59\:59.999Z] AND endPosition\:[2015-09-01T00\:00\:00.000Z TO 2015-12-30T23\:59\:59.999Z] ) AND (platformname\:Sentinel-1 AND producttype\:SLC AND sensoroperationalmode\:IW) 
			socketport=9001
			verbose=no
			log=yes
			logfolder= /tmp/
			threadcheckersleep=60000
			downloadtrieslimit=100
		 */


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
			prop.setProperty("clienturl", null == aHistory.getClientUrl() ? "" : aHistory.getClientUrl() );
			prop.setProperty("sentinel", aHistory.getSentinel());

			prop.setProperty("threadcheckersleep", String.valueOf(aHistory.getThreadCheckerSleep()));
			prop.setProperty("downloadtrieslimit", String.valueOf(aHistory.getDownloadTriesLimit()));

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

	public double getJavaHomeVersion () {
		String version = System.getProperty("java.version");
		int pos = version.indexOf('.');
		pos = version.indexOf('.', pos+1);
		return Double.parseDouble (version.substring (0, pos));
	}
	public String getJavaHome() throws SentinelRuntimeException {
		String ret = null;
		ret = System.getenv("JAVA_HOME");
		if(ret != null){
			File f = new File(ret);

			if(f != null && f.exists() && f.isDirectory()){
				if(ret.charAt(ret.length()-1) != '/') ret = ret + "/";
			} else throw new SentinelRuntimeException("JAVA_HOME is not a valid directory: "+ret);
		} else throw new SentinelRuntimeException("JAVA_HOME is not defined.");


		return ret;
	}

	public ArgumentsHistory readArgumentsHistoryPropertyFile(String filePath) {
		ArgumentsHistory ret = null;

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(filePath);

			prop.load(input);

			long threadcheckersleep = 1000l;
			int downloadtrieslimit = 5;

			try{
				threadcheckersleep = Long.parseLong(prop.getProperty("threadcheckersleep"));
			}catch(NumberFormatException nfe){
				System.out.println("Failed trying to read threadcheckersleep property from: "+filePath);
			}

			try{
				downloadtrieslimit = Integer.parseInt(prop.getProperty("downloadtrieslimit"));
			}catch(NumberFormatException nfe){

				System.out.println("Failed trying to read downloadtrieslimit property from: "+filePath);
			}

			ret = new ArgumentsHistory(prop.getProperty("user"), 
					prop.getProperty("outputfolder"), 
					prop.getProperty("sentinel"), 
					prop.getProperty("clienturl"), 
					prop.getProperty("socketport"),
					prop.getProperty("password"), 
					Boolean.valueOf(null == prop.getProperty("verbose") ? "no" : prop.getProperty("verbose").trim()), 
					Boolean.valueOf(null == prop.getProperty("log") ? "yes" : prop.getProperty("log").trim()), 
					prop.getProperty("logfolder"),
					threadcheckersleep,
					downloadtrieslimit
					);

 
		} catch (FileNotFoundException ex) {
			
		} catch (NullPointerException ex) {
			
			System.err.println("Invalid "+filePath+" file. Delete or fix it and try again.");
			ex.printStackTrace();
			System.exit(-1);
			
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
