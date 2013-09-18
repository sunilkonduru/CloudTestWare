package edu.uci.util.S3;

import java.io.File;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class S3Impl implements IS3 {
	
	AmazonS3 s3;
	Region usWest2;
	String bucketName;
	
	public S3Impl(String bucketName) {
		s3 = new AmazonS3Client(new AWSCredentials() {
			
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
		s3.setRegion(usWest2);
		this.bucketName = bucketName;
	}

	public void createBucket() {
		CannedAccessControlList acl = CannedAccessControlList.PublicRead;
        s3.createBucket(bucketName);
        s3.setBucketAcl(bucketName, acl);
	}
	
	public String bucketLink(){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("https://s3-us-west-2.amazonaws.com/")
					 .append(bucketName)
					 .append("/");
		return stringBuilder.toString();
	}

	@Override
	public void pushObject(String objectName) {
		PutObjectRequest por = new PutObjectRequest(bucketName,  objectName, new File(objectName)).withCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(por);
	}
	
	@Override
	public S3Object pullObject(String objectName){
		 GetObjectRequest getReq=new GetObjectRequest(bucketName,objectName);
		 return s3.getObject(getReq);
	}

	@Override
	public void deleteObject(String objectName) {
		s3.deleteObject(bucketName, objectName);
	}
	
}
