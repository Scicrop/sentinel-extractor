package com.scicrop.se.threads;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.scicrop.se.commons.dataobjects.Payload;
import com.scicrop.se.commons.net.NetUtils;
import com.scicrop.se.commons.net.NetUtils.SentinelExtractorStatus;
import com.scicrop.se.commons.utils.Commons;
import com.scicrop.se.runtime.Launch;
import com.scicrop.se.utils.DownloadHelper;

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
		Launch.STATUS = new Payload(NetUtils.SentinelExtractorStatus.FORCE_STOP, null);
		System.out.println("ThreadChecker status:    "+Launch.STATUS.getSentinelExtractorStatus()+"                       ");
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
				
				String statusDescription = outputFileNamePath+" ("+DownloadHelper.getInstance().formatDownloadedProgress(len, lenT1)+")";

				if(lenT1 > lenT0){
					Launch.STATUS = new Payload(NetUtils.SentinelExtractorStatus.DOWNLOADING, statusDescription);
					System.out.print("ThreadChecker status:             "+Launch.STATUS.getSentinelExtractorStatus()+"          \r");
				}
				else if(len == lenT1 || len == lenT0){
					Launch.STATUS = new Payload(NetUtils.SentinelExtractorStatus.FINISHED, statusDescription);
					System.out.println("ThreadChecker status: "+Launch.STATUS.getSentinelExtractorStatus());
				}
				else if(lenT0 == lenT1){
					Launch.STATUS = new Payload(NetUtils.SentinelExtractorStatus.STALLED, statusDescription);
					System.out.println("ThreadChecker status: "+Launch.STATUS.getSentinelExtractorStatus()+"         ");
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
