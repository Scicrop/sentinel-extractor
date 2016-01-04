package com.scicrop.se.tests;

import org.junit.Test;

public class TestOdataClient {
	
	

	
	
	@Test
	public void test(){
		String a = "https://scihub.copernicus.eu/dhus/search?q=1sdsdsdsdsd";
		System.out.println("=============> "+a.substring(43));
		System.out.println("=============> "+a.substring(0, 43));
	}
	

}
