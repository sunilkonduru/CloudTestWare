package listener;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.sqs.model.Message;

import server.DBKeyValue;
import server.SimpleDBImpl;
import server.SimpleQueuingServiceImpl;
import tcpcomm.ChatServer;
import util.DBUtil;

public class ActiveQueueListener implements Runnable {

	static ChatServer chatServer = null;
	
	SimpleQueuingServiceImpl activeSQSQueue = new SimpleQueuingServiceImpl("Active");
	SimpleQueuingServiceImpl processingSQSQueue = new SimpleQueuingServiceImpl("Processing");
	
	SimpleDBImpl simpleDB = new SimpleDBImpl("SubscribersList");

	@Override
	public void run() {
		List<String> freeAndActiveIpAddressFromDatabase = new ArrayList<String>();
		while(true) {
			System.out.println("First loop");
			List<Message> messagesInQueue = activeSQSQueue.receiveMessageFromSQS();
			if(messagesInQueue.size() == 0) {
				try {
					Thread.sleep(2000);
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				for (Message message : messagesInQueue) {

					String msgContents = message.getBody();
					String freeSubscribersIpAddr = ""; // get it by querying database

					while(true) {
						System.out.println("Second loop");
						// Query database for free and active subscribers
						List<DBKeyValue> kvList = DBUtil.findFreeAndIdleSubscribers();
						SelectRequest selectData = simpleDB.selectData("IPAddress", kvList);

						if(freeAndActiveIpAddressFromDatabase.size() == 0) {
							for (Item item : simpleDB.getSdb().select(selectData).getItems()) {
								for (Attribute attribute : item.getAttributes()) {
									freeAndActiveIpAddressFromDatabase.add(attribute.getValue());
								}
							}
						}

						// Checking if arraylist is populated after select operation.. else retrying...
						if(freeAndActiveIpAddressFromDatabase.size() != 0) {
							freeSubscribersIpAddr = freeAndActiveIpAddressFromDatabase.get(0);
							freeAndActiveIpAddressFromDatabase.remove(0);
							break;
						} else {
							try {
								Thread.sleep(2000);
								continue;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

					chatServer.handle(freeSubscribersIpAddr, msgContents);

					// Deleting message from Active Queue
					// sqsImpl.deleteMessage(0); // I don't think this is needed.

					// Update the status of the Subscriber with this IP in DB to Busy
					DBKeyValue updateValues = new DBKeyValue();
					updateValues.setAttribute("Status");
					updateValues.setValue("Busy");
					simpleDB.updateData(freeSubscribersIpAddr, updateValues);

					// Pushing the data in the Processing Queue
					processingSQSQueue.sendMessageToSQS(msgContents);
				}
			}
		}
	}

	public static void main(String[] args) {
		chatServer = new ChatServer(4444);
		ActiveQueueListener aql = new ActiveQueueListener();
		aql.init();
		aql.start();
		aql.pushData();
	}

	private void start() {
		Thread thread = new Thread(this);
		thread.start();
	}
	
	private void init() {
		activeSQSQueue.createQueue();
		simpleDB.createDB();
	}
	
	private void pushData() {
		activeSQSQueue.sendMessageToSQS("Hello");
	}
}