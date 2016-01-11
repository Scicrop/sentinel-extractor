package com.scicrop.se.commons.utils;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;



public class LogHelper {

	private LogHelper(){}

	private static LogHelper INSTANCE = null;

	public static LogHelper getInstance(){
		if(INSTANCE == null) INSTANCE = new LogHelper();
		return INSTANCE;
	}


	public void setLogger(String logNamePattern){

		String logPath = Constants.LOG_FOLDER + Constants.APP_NAME+"_"+logNamePattern+".log";

		Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(Level.INFO);
		PatternLayout layout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n");
		rootLogger.addAppender(new ConsoleAppender(layout));
		try {

			RollingFileAppender fileAppender = new RollingFileAppender(layout, logPath);
			rootLogger.addAppender(fileAppender);
		} catch (IOException e) {
			System.err.println("Failed to find/access "+logPath+" !");
			System.exit(1);
		}
	}

	public void handleVerboseLog(boolean isVerbose, boolean isLog, Log log, char type, String data){

		if(isLog){
			logData(data, type, log);
		}
		if(isVerbose){
			verbose(data, type);
		}

	}

	public void logData(String data, char type, Log log){



		switch (type) {
		case 'i':
			log.info(data);
			break;

		case 'w':
			log.warn(data);
			break;

		case 'e':
			log.error(data);
			break;

		default:
			log.info(data);
			break;
		}


	}

	public void verbose(String data, char type){
		switch (type) {


		case 'e':
			System.err.println(data);
			break;

		default:
			System.out.println(data);
			break;
		}

	}

}
