package edu.uci.util.SQS;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SimpleQueuingServiceImpl implements ISimpleQueuingService {

    AmazonSQS sqs;
    Region usWest2;
    String myQueueURL;
    String queueName;

    public SimpleQueuingServiceImpl(String queueName) {
        sqs = new AmazonSQSClient(new AWSCredentials() {
			
			@Override
			public String getAWSSecretKey() {
				return "oSSzTsoesWeF6I7NX2yMaBbP1D0Z6gNJAY86C8pi";
			}
			
			@Override
			public String getAWSAccessKeyId() {
				return "AKIAJLTYIAVJS3IP2QIA";
			}
		});
        usWest2 = Region.getRegion(Regions.US_WEST_2);
        sqs.setRegion(usWest2);
        this.queueName = queueName;
    }

    @Override
    public void createQueue() {
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        myQueueURL = sqs.createQueue(createQueueRequest).getQueueUrl();
        System.out.println(myQueueURL);
    }

    @Override
    public void sendMessageToSQS(String message) {
    	GetQueueUrlResult queueURL = sqs.getQueueUrl(new GetQueueUrlRequest(queueName));
    	
        sqs.sendMessage(new SendMessageRequest(queueURL.getQueueUrl(), message));
    }

    @Override
    public List<Message> receiveMessageFromSQS() {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueURL);
        return sqs.receiveMessage(receiveMessageRequest).getMessages();
    }

    public String getMessageAtPosition(List<Message> messages,int position) {
        return messages.get(position).getBody();
    }

    @Override
    public Message deleteMessage(int position) {
        List<Message> messages = receiveMessageFromSQS();
        if(messages.size() == 0) {
            return null;
        } else {
            Message currentMessage = messages.get(position);
            String messageRecieptHandle = currentMessage.getReceiptHandle();
            System.out.println("messageRecieptHandle" + messageRecieptHandle);
            GetQueueUrlResult queueUrl = sqs.getQueueUrl(new GetQueueUrlRequest(queueName));
            sqs.deleteMessage(new DeleteMessageRequest().withQueueUrl(queueUrl.getQueueUrl()).withReceiptHandle(messageRecieptHandle));
            return currentMessage;
        }
    }

}