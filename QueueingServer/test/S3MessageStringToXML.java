package test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class S3MessageStringToXML {

	private String stringContent;
	private Document xmlMessage;

	public S3MessageStringToXML(String stringContent) throws ParserConfigurationException, SAXException, IOException {
		this.stringContent = stringContent;
		convertStringToXML();
	}

	private void convertStringToXML() throws ParserConfigurationException, SAXException,
	IOException {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource inputSrc = new InputSource();
		inputSrc.setCharacterStream(new StringReader(stringContent));
		xmlMessage = db.parse(inputSrc);
	}

	public String addTagDesc(String tagName) {
		NodeList nodes = xmlMessage.getElementsByTagName(tagName);
		return nodes.item(0).getTextContent();
	}

	public String getTestName() {
		return addTagDesc("TestName");
	}

	public String getS3URL() {
		return addTagDesc("S3URL");
	}

	public String getTestTool() {
		return addTagDesc("TestTool");
	}

	public String getPublisher() {
		return addTagDesc("Publisher");
	}

	public String getSubscriber() {
		return addTagDesc("Subscriber");
	}

}
