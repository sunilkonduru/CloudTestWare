package edu.uci.util.S3;

import com.amazonaws.services.s3.model.S3Object;

public interface IS3 {

	public void createBucket();
	
	public void pushObject(String objectName);
	
	public void deleteObject(String objectName);
	
	public S3Object pullObject(String objectName);
	
}
