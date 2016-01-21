package com.scicrop.se.commons.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;



public class PropertyReader {

	private String filePath;
	private Map<String,String> attributesMap = null;


	public PropertyReader(String filePath){
		this.filePath = filePath;
	}


	public Map<String,String> getAllProperties() throws IOException{
		if(this.attributesMap == null){
			Path p = Paths.get(filePath); 
			File f = p.toFile();
			if(f.isFile() && f.exists()){
				if(f.canRead()){
					for(String line : Files.readAllLines(Paths.get(filePath), Charset.defaultCharset())){
						String[] keyValue = line.split("=");
						if(keyValue.length == 2){
							String key = line.split("=")[0].trim();
							String value = line.split("=")[1].trim();
							this.attributesMap.put(key, value);
						}else{
							//throw
						}
					}
				}else{
					//throw
				}
			}else{
				//throw
			}
		}
		return this.attributesMap;
	}


}
