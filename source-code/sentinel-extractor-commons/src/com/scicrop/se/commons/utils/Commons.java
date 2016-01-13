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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
			ArgumentsHistory aHistory = new ArgumentsHistory(user, outputFolder, sentinel, clientUrl, null, null);
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

			ret = new ArgumentsHistory(prop.getProperty("user"), prop.getProperty("outputfolder"), prop.getProperty("sentinel"), prop.getProperty("clienturl"), prop.getProperty("socketport"), prop.getProperty("password"));


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
