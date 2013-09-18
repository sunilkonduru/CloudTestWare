package test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class S3MessageXMLParser {

	private String fileName;
	private Document msgFormatDoc;

	S3MessageXMLParser() throws ParserConfigurationException, SAXException, IOException {
		fileName = "MessageFormat.xml";
		readFile();
	}

	
	/***
	 * Read the input file and Parsing it into a XML file using DOM
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void readFile() throws ParserConfigurationException, SAXException,
			IOException {
		File msgFormat = new File(fileName);
		DocumentBuilderFactory docbuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docuBuilder = docbuilderFactory.newDocumentBuilder();
		msgFormatDoc = docuBuilder.parse(msgFormat);
		msgFormatDoc.getDocumentElement().normalize();
	}

	
	/***
	 * Adding text content to the tags corresponding to the name passed
	 * @param tagName
	 * @param textContent
	 */
	public void addTagDesc(String tagName, String textContent) {
		NodeList nodes = msgFormatDoc.getElementsByTagName(tagName);
		nodes.item(0).setTextContent(textContent);
	}
	
	public void setTestName(String testName) {
		addTagDesc("TestName", testName);
	}
	
	public void setS3URL(String s3URL) {
		addTagDesc("S3URL", s3URL);
	}
	
	public void setTestTool(String testToolName) {
		addTagDesc("TestTool", testToolName);
	}
	
	public void setPublisher(String publisherName) {
		addTagDesc("Publisher", publisherName);
	}
	
	public void setSubscriber(String subscriberName) {
		addTagDesc("Subscriber", subscriberName);
	}

	private String convertXmlToString() throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(msgFormatDoc), new StreamResult(
				writer));

		String xmlConvertedToString = writer.getBuffer().toString();
		return xmlConvertedToString;
	}

	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException, TransformerException {
		S3MessageXMLParser p = new S3MessageXMLParser();
		p.setTestName("cs237");
		String str = p.convertXmlToString();
		
		System.out.println(str);
		
		S3MessageStringToXML abc = new S3MessageStringToXML(str);
		System.out.println(abc.getTestName());
	}

}
