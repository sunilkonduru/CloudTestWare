package edu.uci.testclient;

import java.util.UUID;
import edu.uci.util.S3.*;;

public class S3Uploader {

	private S3Impl s3Impl;
	private String bucketURL;
	public S3Uploader(){
		s3Impl = new S3Impl("cloud-test-ware");
		bucketURL = s3Impl.bucketLink();
	}
	public void upload(UUID testId) {
		s3Impl.pushObject(testId + ".zip");
	}
	
	public String bucketUrl(){
		return bucketURL;
	}

}
