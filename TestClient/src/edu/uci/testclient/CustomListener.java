package edu.uci.testclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import edu.uci.util.MsgFormat;
import edu.uci.util.MsgFormatHandler;
import edu.uci.util.SQS.SimpleQueuingServiceImpl;

public class CustomListener extends RunListener{

	public List<String> runInfo = new ArrayList<String>();
	MsgFormat _msg = null;
	
	public CustomListener(MsgFormat msg){
		_msg=msg;
	}
	
	@Override
	public void testFailure(Failure failure) throws Exception {
		System.out.println("Failure: " + failure.getMessage());
    }
	
	@Override
	public void testRunFinished(Result result) throws Exception {
		runInfo.add(result.toString());
		System.out.println(result.toString());
    }
	
	@Override
	 public void testFinished(Description description) throws Exception {
		if(runInfo.size()!=0){
			_msg.setResult(runInfo.get(0));
			_msg.setStatus(false);
		}
		else{
		_msg.setResult(description.toString());
		_msg.setStatus(true);
		System.out.println(description.toString());
		}
		SimpleQueuingServiceImpl finishedSQSQueue = new SimpleQueuingServiceImpl("Completed");
		MsgFormatHandler msgHandler = new MsgFormatHandler();
		String msg=msgHandler.marshallMsg(_msg);
		System.out.println("add message to SQS Completed");
		finishedSQSQueue.sendMessageToSQS(msg);
		System.out.println("added message to SQS Completed"+_msg.getStatus());
    }

}
