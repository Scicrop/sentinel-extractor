package com.scicrop.se.commons.utils;

public class Constants {
	public static final String HTTP_METHOD_PUT = "PUT";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_DELETE = "DELETE";

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
	public static final String LOG_FOLDER = "/tmp/";
	
	public static final boolean LOG = true;
	public static final boolean VERBOSE = false;
	public static final long THREAD_CHECKER_SLEEP = 15000;
	public static final int SOCKET_TIMEOUT = 5000;
	public static final int UDP_SERVER_PORT = 9001;

	public static String user = "guest";
	public static String password = "guest";
	
	public static String JAR_PATH = "/tmp/se.jar";
}
