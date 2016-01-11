package com.scicrop.ses.runtime;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import com.scicrop.se.commons.dataobjects.ArgumentsHistory;
import com.scicrop.se.commons.dataobjects.ThreadDescriptorLstObject;
import com.scicrop.se.commons.dataobjects.ThreadDescriptorObject;
import com.scicrop.se.commons.threads.LauncherExtProcessThread;
import com.scicrop.se.commons.utils.Commons;
import com.scicrop.se.commons.utils.Constants;
import com.scicrop.se.commons.utils.XmlUtils;
import com.scicrop.ses.threads.ProcessListenerThread;

public class Launch {
	
	public static String CONF_PARAM = null;

	public static void main(String[] args) {


		System.out.println("\n\nSentinel Extractor Supervisor 0.0.1\nCommand Line Interface (CLI)\nhttps://github.com/Scicrop/sentinel-extractor\n\n");

		if(null != args && args.length == 1){
			File f = new File(args[0]);

			if(f.exists() && f.isFile()){
				
				CONF_PARAM = args[0];
				
				ThreadDescriptorLstObject t = XmlUtils.getInstance().threadDescLst(f);
				List<ThreadDescriptorObject> l = t.getThreadDescriptorLst();
				for (ThreadDescriptorObject threadDescriptorObject : l) {

					String confParam = threadDescriptorObject.getConfParam();
					ArgumentsHistory aHistory = Commons.getInstance().readArgumentsHistoryPropertyFile(confParam);

					

					File jarFile = new File(Constants.JAR_PATH);

					if(jarFile.exists() && jarFile.isFile()){

						Thread pThread = new LauncherExtProcessThread(new String[]{"java","-jar", Constants.JAR_PATH, confParam});
						pThread.start();

					}else{
						System.out.println("Jar file "+Constants.JAR_PATH+" was not found.");
						System.exit(0);
					}

					ProcessListenerThread procListenerThread = new ProcessListenerThread(Integer.parseInt(aHistory.getSocketPort()));
					procListenerThread.start();

				}
			}
		}else{

			System.out.println(		"\n\n"
					+ 	"***************************************\n"
					+ 	"No configuration file was supplied.\n"
					+ 	"***************************************");

		}


	}

}
