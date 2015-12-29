package com.scicrop.se.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ThreadChecker extends Thread {


	private String outputFileNamePath = null;
	private long len = -1l;
	private boolean forceStop = false;


	public ThreadChecker(String outputFileNamePath, long len){

		this.outputFileNamePath = outputFileNamePath;
		this.len = len;

	}

	public void forceStop(){
		forceStop = true;
		System.out.println("ThreadChecker status:    FORCE STOP                       ");
	}

	public void run(){


		RandomAccessFile raf = null;



		long lenT0 = -1l;
		long lenT1 = -1l;


		while(lenT1 < len && !forceStop){

			try {

				raf = new RandomAccessFile(outputFileNamePath, "r");
				lenT0 = raf.length();

				Thread.sleep(60000);

				lenT1 = raf.length();

				if(lenT1 > lenT0) System.out.print("ThreadChecker status:             DOWNLOADING\r");
				else if(len == lenT1 || len == lenT0) System.out.println("ThreadChecker status: FINISHED");
				else if(lenT0 == lenT1){
					System.out.println("ThreadChecker status: STALLED         ");
					System.exit(1);
				} 

			} catch (FileNotFoundException e) {
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(raf != null)
					try {
						raf.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}

		}
		System.out.println("ThreadChecker status: END OF ThreadChecker [FORCE STOP = "+forceStop+"]" );



	}

}
