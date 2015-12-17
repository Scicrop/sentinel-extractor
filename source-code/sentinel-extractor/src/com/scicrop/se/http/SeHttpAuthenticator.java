package com.scicrop.se.http;

import java.net.Authenticator;
import java.net.PasswordAuthentication;



public class SeHttpAuthenticator extends Authenticator {

	private String user = null;
	private String password = null;
	
	public SeHttpAuthenticator(String user, String password){
		this.user = user;
		this.password = password;
	}
	
	public PasswordAuthentication getPasswordAuthentication() {
        return (new PasswordAuthentication(user, password.toCharArray()));
    }
}