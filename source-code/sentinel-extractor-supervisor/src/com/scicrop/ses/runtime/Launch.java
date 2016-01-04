package com.scicrop.ses.runtime;

import com.scicrop.ses.net.SeSocketClient;

public class Launch {

	public static void main(String[] args) {


		System.out.println("\n\nSentinel Extractor Supervisor 0.0.1\nCommand Line Interface (CLI)\nhttps://github.com/Scicrop/sentinel-extractor\n\n");
		
		SeSocketClient sesc = new SeSocketClient();
		sesc.listen("127.0.0.1", 9001);
		
	}

}
