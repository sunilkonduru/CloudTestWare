package edu.uci.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class MsgFormatHandler {
	
	public static String marshallMsg(MsgFormat msgFormat) throws JAXBException {
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(MsgFormat.class);
		Marshaller m = context.createMarshaller();
		m.marshal(msgFormat, writer);
		return writer.toString();
	}
	
	public static MsgFormat unmarshallMsg(String message) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(MsgFormat.class);
		Unmarshaller um = context.createUnmarshaller();
		return (MsgFormat)um.unmarshal(new StringReader(message.toString()));
	}
}
