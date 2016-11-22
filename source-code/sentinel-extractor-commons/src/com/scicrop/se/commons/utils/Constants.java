package com.scicrop.se.commons.utils;

public class Constants {
	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_DELETE = "DELETE";
	public static final String USER_AGENT = "Mozilla/5.0";
	
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HTTP_HEADER_ACCEPT = "Accept";

	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	public static final String APPLICATION_ATOM_XML = "application/atom+xml";
	public static final String APPLICATION_FORM = "application/x-www-form-urlencoded";
	public static final String METADATA = "$metadata";
	public static final String SEPARATOR = "/";
	public static final String COPERNICUS_HOST = "https://scihub.copernicus.eu";
	public static final String COPERNICUS_ODATA_METALINK = "https://scihub.copernicus.eu/dhus/odata/v1/$metadata";
	public static final String COPERNICUS_ODATA_ROOT = "https://scihub.copernicus.eu/dhus/odata/v1/";

	public static final int BUFFER_SIZE = 8192;
	
	public static final String APP_NAME = "sentinel-extractor";
	public static final String APP_VERSION = "0.3.1";
	public static final double JAVA_VERSION_COMPLIANCE = 1.7d;
	public static final long DEFAULT_THREAD_CHECKER_SLEEP = 30000l;
	public static final int DEFAULT_DOWNLOAD_TRIES_LIMIT = 5;
	public static final String MYIP_URL = "";
	
	public static String user = "guest";
	public static String password = "guest";
	public static String UTF8 = "UTF-8";
	

}
