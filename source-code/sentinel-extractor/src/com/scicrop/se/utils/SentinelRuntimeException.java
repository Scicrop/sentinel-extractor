package com.scicrop.se.utils;


public class SentinelRuntimeException extends Exception {

	private static final long serialVersionUID = -1156744939656104706L;

	public SentinelRuntimeException(Exception e){
		super(e);
	}

	public SentinelRuntimeException(String message) {
		super(message);
	}

	
	
}
