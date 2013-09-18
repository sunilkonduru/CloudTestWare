package edu.uci.testclient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

import edu.uci.util.MsgFormat;
import edu.uci.util.MsgFormatHandler;
import edu.uci.util.DB.DBKeyValue;
import edu.uci.util.DB.SimpleDBImpl;
//import edu.uci.util.DB.SimpleDBImpl;

public class ChatClient {  
	private Socket socket              = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client    = null;
	private String    serverName = "localhost";
	private int       serverPort = 4444;
	private String ipAddress;
	
	public boolean connect(String serverName, int serverPort) throws Exception {
		// println("Establishing connection. Please wait ...");
		try {  
			socket = new Socket(serverName, serverPort);
			// println("Connected: " + socket);
			open();

			// Send Initial Message
//			NetworkInterface ni = NetworkInterface.getByName("eth1");
//			for (Enumeration<InetAddress> addresses =  ni.getInetAddresses(); addresses.hasMoreElements(); ) {
//				InetAddress address = addresses.nextElement();
//				System.out.println("Address:   " + address);
				ipAddress= getIpAddress();
			    
				streamOut.writeUTF("init/" + ipAddress); streamOut.flush();
				
//			}
			System.out.println("The IP Address set: " + ipAddress);
		} catch(UnknownHostException uhe) {  
			return false; 
		} catch(IOException ioe) {  
			return false; 
		} 
		return true;
	}

	private void send() {  
		/*try {  
			streamOut.writeUTF(input.getText()); streamOut.flush(); input.setText("");
		} catch(IOException ioe) {  
			println("Sending error: " + ioe.getMessage()); close(); 
		} */
	}

	public void handle(String msg) {  
		
		//UnMarshall The String
		try{
	    System.out.println("a:"+msg);
	    
		MsgFormat message=MsgFormatHandler.unmarshallMsg(msg);
		Main.setMessage(message);
		System.out.println("TestName"+message.getTestName());
		System.out.println("Starting the process test Runner");
		Main.processTestRunner();
		System.out.println("ran it!!");
		// update the entry from the database
		SimpleDBImpl sdb = new SimpleDBImpl("SubscribersList");
	    DBKeyValue updateValues = new DBKeyValue();
		updateValues.setAttribute("Status");
		updateValues.setValue("Idle");
		sdb.updateData(getIpAddress(), updateValues);
		System.out.println("updated it!!");
		
		/*
		 * 
		 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?><msgformat><testname>org.apache.logging.log4j.core.appender.db.jpa.converter.ContextMapAttributeConverterTest.testConvertToDatabaseColumn01</testname><s3url>https://s3-us-west-2.amazonaws.com/cloud-test-ware/f8a5ccf9-8237-4cb7-80f8-fed70e4b399f.zip</s3url></msgformat>
		 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?><msgformat><testname>org.apache.logging.log4j.core.appender.db.jdbc.DriverManagerConnectionSourceTest.testValidUrlUsernamePassword</testname><s3url>https://s3-us-west-2.amazonaws.com/cloud-test-ware/f8a5ccf9-8237-4cb7-80f8-fed70e4b399f.zip</s3url></msgformat>

		 * 
		 */
		
		}
		catch(Exception e){
			System.out.println(e);
		}
		
		if (msg.equals(".bye")) {  
			// println("Good bye. Press RETURN to exit ...");  close(); 
		} else  {
			// println(msg); 
		}
	}

	public void open() {  
		try {  
			streamOut = new DataOutputStream(socket.getOutputStream());
			client = new ChatClientThread(this, socket);
		} catch(IOException ioe) {  
			// println("Error opening output stream: " + ioe); 
		} 
	}

	public void close() {  
		try {  
			if (streamOut != null)  streamOut.close();
			if (socket    != null)  socket.close(); 
		} catch(IOException ioe) {
			//delete the row from the table
			
			// println("Error closing ..."); 
		}
		client.close();  client.stop(); 
	}

	/*private void println(String msg) {  
		display.appendText(msg + "\n"); 
	}*/

	public void getParameters() {  
		/*serverName = getParameter("host");
		serverPort = Integer.parseInt(getParameter("port"));*/ 
		serverName = "localhost";
		serverPort = 4444; 
	}
	public String getIpAddress() throws Exception{
//		NetworkInterface ni = NetworkInterface.getByName("eth1");
//		for (Enumeration<InetAddress> addresses =  ni.getInetAddresses(); addresses.hasMoreElements(); ) {
//			InetAddress address = addresses.nextElement();
//			System.out.println("Address:   " + address);
//			ipAddress=address.toString();
//			streamOut.writeUTF("init" + address.toString()); streamOut.flush();
//		}
//		return ipAddress;
		String finalAddress=null;
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		OUTER : for (NetworkInterface interface_ : Collections.list(interfaces)) {
		  // we shouldn't care about loopback addresses
		  if (interface_.isLoopback())
		    continue;

		  // if you don't expect the interface to be up you can skip this
		  // though it would question the usability of the rest of the code
		  if (!interface_.isUp())
		    continue;

		  // iterate over the addresses associated with the interface
		  Enumeration<InetAddress> addresses = interface_.getInetAddresses();
		  for (InetAddress address : Collections.list(addresses)) {
		    // look only for ipv4 addresses
		    if (address instanceof Inet6Address)
		      continue;

		    // use a timeout big enough for your needs
		    if (!address.isReachable(3000))
		      continue;
		    finalAddress=address.toString();
		   
		    break;
		    
      }

		  if(finalAddress!=null)
		  {
		   System.out.println(finalAddress);
		   break;
		  }
		  
}
		finalAddress=finalAddress.substring(1);
		return finalAddress;
	}
}