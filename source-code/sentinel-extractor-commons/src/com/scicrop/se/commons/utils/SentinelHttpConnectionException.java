package com.scicrop.se.commons.utils;

import java.io.IOException;

public class SentinelHttpConnectionException extends Exception {

	private static final long serialVersionUID = -1156744939656104706L;

	public SentinelHttpConnectionException(IOException e){
		super(e);
	}

	public SentinelHttpConnectionException(String message) {
		super(message);
	}
	
}
