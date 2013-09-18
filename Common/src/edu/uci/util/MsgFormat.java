package edu.uci.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="msgformat")
@XmlAccessorType(XmlAccessType.NONE)
public class MsgFormat {
	
	@XmlElement(name="testname")
	String testName;
	
	@XmlElement(name="s3url")
	String s3URL;

	@XmlElement(name="testtool")
	String testTool;
	
	@XmlElement(name="publisher")
	String publisher;
	
	@XmlElement(name="subscriber")
	String subscriber;

	@XmlElement(name="passed")
	Boolean passed;
	
	@XmlElement(name="result")
	String result;

	
	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public boolean getStatus() {
		return passed;
	}

	public void setStatus(boolean passed) {
		this.passed = passed;
	}

	public String getS3URL() {
		return s3URL;
	}

	public void setS3URL(String s3url) {
		s3URL = s3url;
	}

	public String getTestTool() {
		return testTool;
	}

	public void setTestTool(String testTool) {
		this.testTool = testTool;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}
	
	@Override
	public String toString() {
		return "MsgFormat [testName=" + testName + ", s3URL=" + s3URL
				+ ", testTool=" + testTool + ", publisher=" + publisher
				+ ", Subscriber=" + subscriber + "]";
	}
}
