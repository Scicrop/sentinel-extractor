package com.scicrop.se.commons.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.scicrop.se.commons.dataobjects.SupervisorXmlObject;
import com.scicrop.se.commons.dataobjects.ThreadDescriptorLstObject;
import com.scicrop.se.commons.dataobjects.ThreadDescriptorObject;


public class XmlUtils {

	private XmlUtils(){}

	private static XmlUtils INSTANCE = null;

	public static XmlUtils getInstance(){
		if(INSTANCE == null) INSTANCE = new XmlUtils();
		return INSTANCE;
	}

	public Document xmlFile2xmlDocument(File f) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(f);
		doc.getDocumentElement().normalize();
		return doc;
	}

	public ThreadDescriptorLstObject threadDescLst(File fXmlFile){

		ThreadDescriptorLstObject ret = null;

		ArrayList<ThreadDescriptorObject> threadDescLst = null;
		String jarPath =null;
		int udpPort = 0;
		try {
			threadDescLst  = new ArrayList<ThreadDescriptorObject>();
			Document doc = xmlFile2xmlDocument(fXmlFile);
			NodeList nl = doc.getElementsByTagName("supervisor");
			Node n = nl.item(0).getAttributes().getNamedItem("jarpath");
			Node n2 = nl.item(0).getAttributes().getNamedItem("udp_server_port");
			if(n != null && n2 != null) {
				jarPath = n.getNodeValue();
				udpPort = Integer.parseInt(n2.getNodeValue());
				threadDescLst = getElementsByDocAndTagName(doc, "thread");
				
				ret = new ThreadDescriptorLstObject(threadDescLst,jarPath,udpPort);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		return ret;
	}

	private ArrayList<ThreadDescriptorObject> getElementsByDocAndTagName(Document doc, String tagName) {

		ArrayList<ThreadDescriptorObject> threadDescLst =  new ArrayList<ThreadDescriptorObject>();

		NodeList nList = doc.getElementsByTagName(tagName);

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				threadDescLst.add(new ThreadDescriptorObject(eElement.getAttribute("prop")));


			}
		}

		return threadDescLst;
	}

	public SupervisorXmlObject xmlFile2Object(File f) throws SentinelRuntimeException {
		SupervisorXmlObject ret = null;

		Document d = null;
		try {
			ret = new SupervisorXmlObject();
			d = xmlFile2xmlDocument(f);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/supervisor/@jarpath");
			ret.setJarPath(String.valueOf(expr.evaluate(d, XPathConstants.STRING)));
			expr = xpath.compile("/supervisor/@udp_server_port");
			ret.setUdpPort(Integer.parseInt(String.valueOf(expr.evaluate(d, XPathConstants.STRING))));
			expr = xpath.compile("/supervisor/@notifier");
			ret.setNotifier(String.valueOf(expr.evaluate(d, XPathConstants.STRING)));
			ret.setThreadDescriptorLstObject(threadDescLst(f));
			if(ret.getThreadDescriptorLstObject() == null){
				throw new SentinelRuntimeException("The jarPath or udpPortServer was not specified in "+f.getAbsolutePath());
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new SentinelRuntimeException(e);
		} catch (XPathExpressionException e) {
			throw new SentinelRuntimeException(e);
		}



		return ret;
	}

}
