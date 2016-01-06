package com.scicrop.ses.runtime;

import java.io.File;
import java.util.List;

import com.scicrop.se.commons.dataobjects.ThreadDescriptorLstObject;
import com.scicrop.se.commons.dataobjects.ThreadDescriptorObject;
import com.scicrop.se.commons.utils.XmlUtils;
import com.scicrop.ses.net.SeSocketClient;

public class Launch {

	public static void main(String[] args) {


		System.out.println("\n\nSentinel Extractor Supervisor 0.0.1\nCommand Line Interface (CLI)\nhttps://github.com/Scicrop/sentinel-extractor\n\n");
		
		if(null != args && args.length == 1){
			File f = new File(args[0]);
			
			if(f.exists() && f.isFile()){
				ThreadDescriptorLstObject t = XmlUtils.getInstance().threadDescLst(f);
				List<ThreadDescriptorObject> l = t.getThreadDescriptorLst();
				for (ThreadDescriptorObject threadDescriptorObject : l) {
					SeSocketClient sesc = new SeSocketClient();
					sesc.listen("127.0.0.1", 9001);
				}
			}
		}
		
		
	}

}
