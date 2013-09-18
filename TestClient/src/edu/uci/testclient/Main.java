package edu.uci.testclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;

import edu.uci.util.MsgFormat;
import edu.uci.util.DB.SimpleDBImpl;
import edu.uci.util.S3.S3Impl;

public class Main {

	private static MsgFormat _message = null;

	public static void main(String args[]) throws Exception {

		if(!args[0].equals("TestRunner")){
			if (args.length < 3) {
				System.out
				.println("Usage: TestClient.jar <run type> <test library jar> <optional source and dependent jars>");
				System.out
				.println("run type is either \"TestInvoker\" or \"TestRunner\"");
				return;		
			}
		}
		if (args[0].equals("TestInvoker"))
			try {
				new TestInvoker().process(args);
			} catch (IOException e) {
				System.out.println("Error: " + e.getMessage());
			}
		else {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
			// insert Client Information into database
			ChatClient cc = new ChatClient();
			cc.connect(args[1], 4455);
			SimpleDBImpl sdb = new SimpleDBImpl("SubscribersList");
			// insert the client information into the SimpleDB
			List<ReplaceableItem> insertData = new ArrayList<ReplaceableItem>();
			String ip=cc.getIpAddress();
			insertData.add(new ReplaceableItem(ip).withAttributes(
					new ReplaceableAttribute("Status", "Idle", true),
					new ReplaceableAttribute("Alive", "Yes", true)));
			sdb.insertData(insertData);
			System.out.println("Added to Subscribers List");


		}

	}

	public static void processTestRunner() throws Exception {

		// get URL and TestName from the message
		MsgFormat message = getMessage();
		String url = message.getS3URL();
		String testName = message.getTestName();

		// get the zip file name
		String[] urlTokens = url.split("/");
		int indexObjectName = urlTokens.length;
		String zipFileName = urlTokens[indexObjectName - 1];

		System.out.println("add zip file"+zipFileName);
		File file = getZipFileToLocalDirectory(zipFileName);
		System.out.println("got zip file");
		File directoryName=new File(zipFileName.substring(0, zipFileName.length() - 4));
		directoryName.mkdir();
		unzip(file, directoryName);
		//extractToLocalDirectory(file, zipFileName);
		System.out.println("Extracted zip file");

		// run that particular method now after getting the file

		String localTestFolder = zipFileName.substring(0,
				zipFileName.length() - 4);
		System.out.println(localTestFolder);
		String localTestJar = zipFileName
				.substring(0, zipFileName.length() - 4) +"/ZipContents/"+"test.jar";
		System.out.println(localTestJar);
		System.out.println(localTestJar);

		String[] listFiles = getListOfFiles(localTestJar);

		System.out.println("Starting the run method..");
		runTestMethod(listFiles,message);
		System.out.println("completed the run method"); 
		// add to message to the completed queue
		
		//delete the row after certain point


	}

	public static void runTestMethod( String[] listFiles,MsgFormat msg)
			throws IOException {
		String fullTestName = msg.getTestName();
		System.out.println("fTestname:"+fullTestName);
		String[] fullTestNameSplit = fullTestName.split("\\.");
		String testName = fullTestNameSplit[fullTestNameSplit.length - 1];
		String qualifiedClassName = fullTestName.substring(0, fullTestName.length() - testName.length() - 1);
		
		JarFile jarFile = new JarFile(listFiles[0]);
		Enumeration<JarEntry> e = jarFile.entries();

		URL[] urls = new URL[listFiles.length];
		int argIndex = 0;
		for (String string : listFiles) {
			urls[argIndex] = new URL("jar:file:" + listFiles[argIndex++] + "!/");
		}
		URLClassLoader cl = URLClassLoader.newInstance(urls);

		while (e.hasMoreElements()) {
			JarEntry je = (JarEntry) e.nextElement();
			if (je.isDirectory() || !je.getName().endsWith(".class")
					|| je.getName().contains("$")) {
				continue;
			}
			// -6 because of .class
			String className = je.getName().substring(0,
					je.getName().length() - 6);
			
			className = className.replace('/', '.');
			if(!className.equals(qualifiedClassName))
				continue;
			try {
				Class c = cl.loadClass(className);
				System.out.println(c.getName());
				TestClass testClass = new TestClass(c);

				List<FrameworkMethod> testList = testClass
						.getAnnotatedMethods(org.junit.Test.class);
				List<FrameworkMethod> BeforeList = testClass
						.getAnnotatedMethods(org.junit.Before.class);
				List<FrameworkMethod> AfterList = testClass
						.getAnnotatedMethods(org.junit.After.class);

				for (FrameworkMethod frameworkMethod : testList) {
					String name = testClass.getName();
					String method = frameworkMethod.getName();
					if(!testName.equals(method))
						continue;
					System.out.println(name + "\t" + method + "\t");
					RunNotifier notifier = new RunNotifier();
					RunListener listener = new CustomListener(msg);
					notifier.addListener(listener);
					new CustomRunner(c).runMethod(method, notifier);

					System.out.println(notifier.toString());
				}
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		jarFile.close();

	}

	public static String[] getListOfFiles(String localTestJar) {
		String[] tokens=localTestJar.split("/");
		String localTestFolder=tokens[0];
		File folder = new File(localTestFolder+"/ZipContents/"+"dependent/");
		File[] listOfFiles = folder.listFiles();
		String[] urls = new String[listOfFiles.length + 1];
		urls[0] = localTestJar;
		for (int i = 1; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				urls[i] = localTestFolder+"/ZipContents/dependent/"
						+ listOfFiles[i].getName();
			}
		}
		return urls;
	}

	public static File getZipFileToLocalDirectory(String zipFileName)
			throws IOException {
		S3Impl s3impl = new S3Impl("cloud-test-ware");
		S3Object s3Obj = s3impl.pullObject(zipFileName);

		InputStream reader = new BufferedInputStream(s3Obj.getObjectContent());
		File file = new File(zipFileName);
		OutputStream writer = new BufferedOutputStream(new FileOutputStream(
				file));

		int read = -1;

		while ((read = reader.read()) != -1) {
			writer.write(read);
		}

		writer.flush();
		writer.close();
		reader.close();
		return file;
	}

	public static void extractToLocalDirectory(File file, String zipFileName)
			throws IOException {
		System.out.println("test:" + file.toURI());
		ZipFile zip = new ZipFile(file);
		String newPath = zipFileName.substring(0, zipFileName.length() - 4);

		new File(newPath).mkdir();
		Enumeration zipFileEntries = zip.entries();
		int BUFFER = 5000;
		while (zipFileEntries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
			String currentEntry = entry.getName();
			File destFile = new File(newPath, currentEntry);

			File destinationParent = destFile.getParentFile();
			// create the parent directory structure if needed
			destinationParent.mkdirs();
			if (!entry.isDirectory()) {
				BufferedInputStream is = new BufferedInputStream(
						zip.getInputStream(entry));
				int currentByte;
				// establish buffer for writing file
				byte data[] = new byte[BUFFER];

				// write the current file to disk
				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos,
						BUFFER);

				// read and write until last byte is encountered
				while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, currentByte);
				}
			}
		}
		zip.close();
	}

	public static MsgFormat getMessage() {
		return _message;
	}

	public static void setMessage(MsgFormat msg) {
		_message = msg;
	}
	public static void unzip(File zipfile, File directory) throws IOException {
	    ZipFile zfile = new ZipFile(zipfile);
	    Enumeration<? extends ZipEntry> entries = zfile.entries();
	    while (entries.hasMoreElements()) {
	      ZipEntry entry = entries.nextElement();
	      File file = new File(directory, entry.getName());
	      if (entry.isDirectory()) {
	        file.mkdirs();
	      } else {
	        file.getParentFile().mkdirs();
	        InputStream in = zfile.getInputStream(entry);
	        try {
	          copy(in, file);
	        } finally {
	          in.close();
	        }
	      }
	    }
	  }
	
	private static void copy(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    while (true) {
	      int readCount = in.read(buffer);
	      if (readCount < 0) {
	        break;
	      }
	      out.write(buffer, 0, readCount);
	    }
	  }

//	  private static void copy(File file, OutputStream out) throws IOException {
//	    InputStream in = new FileInputStream(file);
//	    try {
//	      copy(in, out);
//	    } finally {
//	      in.close();
//	    }
//	  }

	  private static void copy(InputStream in, File file) throws IOException {
	    OutputStream out = new FileOutputStream(file);
	    try {
	      copy(in, out);
	    } finally {
	      out.close();
	    }
	  }

}
