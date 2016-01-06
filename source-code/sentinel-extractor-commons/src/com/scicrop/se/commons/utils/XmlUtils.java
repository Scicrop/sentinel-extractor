package com.scicrop.se.commons.utils;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.scicrop.se.commons.dataobjects.ThreadDescriptorLstObject;
import com.scicrop.se.commons.dataobjects.ThreadDescriptorObject;


public class XmlUtils {

	private XmlUtils(){}

	private static XmlUtils INSTANCE = null;

	public static XmlUtils getInstance(){
		if(INSTANCE == null) INSTANCE = new XmlUtils();
		return INSTANCE;
	}

	public ThreadDescriptorLstObject threadDescLst(File fXmlFile){

		ThreadDescriptorLstObject ret = null;

		ArrayList<ThreadDescriptorObject> threadDescLst = null;
		try {
			threadDescLst  = new ArrayList<ThreadDescriptorObject>();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("thread");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					threadDescLst.add(new ThreadDescriptorObject(eElement.getAttribute("prop")));


				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		ret = new ThreadDescriptorLstObject(threadDescLst);
		
		return ret;
	}

}
