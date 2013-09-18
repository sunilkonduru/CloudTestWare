package edu.uci.testclient;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import com.amazonaws.services.sqs.model.Message;

import edu.uci.util.MsgFormat;
import edu.uci.util.MsgFormatHandler;
import edu.uci.util.SQS.SimpleQueuingServiceImpl;

public class TestInvoker {

	private ZipFileCreator zipfileCreator;
	private S3Uploader s3Uploader;
	private SimpleQueuingServiceImpl simpleQueueingService = new SimpleQueuingServiceImpl("Active");
	private MsgFormatHandler msgFormatHandler;

	public TestInvoker(){
		zipfileCreator = new ZipFileCreator();
		s3Uploader = new S3Uploader();
		msgFormatHandler = new MsgFormatHandler();
	}
	
	public void process(String[] args) throws IOException {
			UUID testId = UUID.randomUUID();
			zipfileCreator.zip(args, testId);
			System.out.println("Uploading zip file " + testId + ".zip to S3 bucket");
			s3Uploader.upload(testId);
			FileUtils.deleteQuietly(new File(testId + ".zip"));
			int messagesSent = 0;
			System.out.println("The current test artifacts are present at " + s3Uploader.bucketUrl() + testId + ".zip");
			
			System.out.println("Starting to publish tests to SQS...");
			
		 	JarFile jarFile = new JarFile(args[1]);
		    Enumeration<JarEntry> e = jarFile.entries();

		    URL[] urls = new URL[args.length];
		    int argIndex = 0;
		    for (String string : args) {
				urls[argIndex] = new URL("jar:file:" + args[argIndex++] +"!/");
			}
		    URLClassLoader cl = URLClassLoader.newInstance(urls);

		    while (e.hasMoreElements()) {
		       JarEntry je = (JarEntry) e.nextElement();
	           if(je.isDirectory() || !je.getName().endsWith("Test.class") || je.getName().contains("$")){
	               continue;
	           }	
		
		       String className = je.getName().substring(0,je.getName().length()-6);
			   className = className.replace('/', '.');
			   try {
				   Class c = cl.loadClass(className);
				   TestClass testClass = new TestClass(c);
				   List<FrameworkMethod> testList = testClass.getAnnotatedMethods(org.junit.Test.class);
				   for (FrameworkMethod frameworkMethod : testList) {
					   MsgFormat msg = new MsgFormat();
					   msg.setTestName(c.getName() + "." + frameworkMethod.getName());
					   msg.setS3URL(s3Uploader.bucketUrl() + testId + ".zip");
					   String marshalledMsg = msgFormatHandler.marshallMsg(msg);
					   simpleQueueingService.sendMessageToSQS(marshalledMsg);
					   System.out.println("Published " + c.getName() + "." + frameworkMethod.getName());
					   messagesSent++;
				   }
			   } 
			   catch (ClassNotFoundException e1) {
				   e1.printStackTrace();
			   } 
			   catch (Exception e1) {
				   e1.printStackTrace();
			   }
			
		}
		jarFile.close();
		
		System.out.println(messagesSent + " tests published.");
		System.out.println("Waiting for results...");
		
		SimpleQueuingServiceImpl completedSQSQueue = new SimpleQueuingServiceImpl("Completed");;
		
		while(true) {
			if(messagesSent == 0) return;
			List<Message> messagesInQueue = completedSQSQueue.receiveMessageFromSQS();
			if(messagesInQueue.size() == 0) {
				try {
					Thread.sleep(1000);
					System.out.println("Sleeping for a second...");
					continue;
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			
			for (Message message : messagesInQueue) {
				messagesSent--;
				MsgFormat msg = null;
				try {
					msg = msgFormatHandler.unmarshallMsg(message.getBody());
				} catch (JAXBException e1) {
					e1.printStackTrace();
				}
				if(msg.getStatus())
				{
					System.out.println("Test " + msg.getTestName() + " passed");
				}
				else
				{
					System.out.println("Test " + msg.getTestName() + " failed");
					System.out.println("Result Message: " + msg.getResult());
				}
				if(messagesSent == 0)
					return;
			}
		}
	}

}
