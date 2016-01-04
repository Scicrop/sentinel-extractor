package com.scicrop.se.commons.utils;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.scicrop.se.commons.dataobjects.ThreadDescriptorLstObject;


public class XmlUtils {
	
	private XmlUtils(){}

	private static XmlUtils INSTANCE = null;

	public static XmlUtils getInstance(){
		if(INSTANCE == null) INSTANCE = new XmlUtils();
		return INSTANCE;
	}

	public ThreadDescriptorLstObject threadDescLst(String xmlPath){
		
		ThreadDescriptorLstObject threadDescLst = null;
		
		try {

			File fXmlFile = new File(xmlPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
					
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
					
			NodeList nList = doc.getElementsByTagName("xmlPath");
					
			//System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
						
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					System.out.println("Staff id : " + eElement.getAttribute("prop"));
					

				}
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		
		return threadDescLst;
	}
	
}
