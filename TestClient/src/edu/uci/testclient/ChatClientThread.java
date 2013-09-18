package edu.uci.testclient;

import java.net.*;
import java.io.*;

public class ChatClientThread extends Thread {  
	private Socket           socket   = null;
	private ChatClient       client   = null;
	private DataInputStream  streamIn = null;

	public ChatClientThread(ChatClient _client, Socket _socket) {
		client   = _client;
		socket   = _socket;
		open();  
		start();
	}

	public void open() {  
		try {  
			streamIn  = new DataInputStream(socket.getInputStream());
		} catch(IOException ioe) {  
			System.out.println("Error getting input stream: " + ioe);
			// client.stop();
		}
	}
	
	public void close() {  
		try {  
			if (streamIn != null) {
				streamIn.close();
			}
		} catch(IOException ioe) {  
			System.out.println("Error closing input stream: " + ioe);
		}
	}

	public void run()  {  
		while (true) {  
			try {  
				client.handle(streamIn.readUTF());
			} catch(IOException ioe) {  
				System.out.println("Listening error: " + ioe.getMessage());
//				client.close();
//				while(true)
//				{
//					try {
//						
//						if(client.connect("192.168.0.3", 4455))
//							break;
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				// client.stop();
			}
		}
	}
}