package edu.uci.util.DB;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;

public class SimpleDBImpl implements ISimpleDB {
	
	/***
	 * Attributes of Subscribers Database are:
	 * 		IPAddress - v4
	 * 		Status - Busy/Idle
	 * 		Category
	 * 		Alive - Yes/No
	 */
	
	String domainName;
	AmazonSimpleDB sdb;
	
	public SimpleDBImpl(String domainName) {
		sdb = new AmazonSimpleDBClient(new AWSCredentials() {
			
			@Override
			public String getAWSSecretKey() {
				return "oSSzTsoesWeF6I7NX2yMaBbP1D0Z6gNJAY86C8pi";
			}
			
			@Override
			public String getAWSAccessKeyId() {
				return "AKIAJLTYIAVJS3IP2QIA";
			}
		});
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		sdb.setRegion(usWest2);
		this.domainName = domainName;
	}

	@Override
	public void createDB() {
		sdb.createDomain(new CreateDomainRequest(domainName));
	}

	@Override
	public void insertData(List<ReplaceableItem> data) {
		sdb.batchPutAttributes(new BatchPutAttributesRequest(domainName, data));
	}

	public AmazonSimpleDB getSdb() {
		return sdb;
	}

	/***
	 * selectList - * or attributes separated by comma.
	 */
	@Override
	public SelectRequest selectData(String selectList, List<DBKeyValue> whereClauseAttrList) {
		String selectExpression = "select " + selectList + " from `" + domainName + "`";
		int count = 0;
		for(DBKeyValue attr : whereClauseAttrList) {
			if(count != 0) {
				selectExpression += " and ";
			} else {
				selectExpression += " where ";
			}
			selectExpression = selectExpression + attr.getAttribute() + " = '" + attr.getValue() + "'";
			count++;
		}
		System.out.println("Selecting: " + selectExpression + "\n");
        
        return new SelectRequest(selectExpression);
	}

	/***
	 * itemName - here it is the IP Address
	 * @throws InterruptedException 
	 */
	@Override
	public void updateData(String itemName, DBKeyValue attr) {
		
//		List<DBKeyValue> kvList = new ArrayList<DBKeyValue>();
//		DBKeyValue kv = new DBKeyValue();
		
		/*kv.setAttribute("Name");
		kv.setValue("I5_&");
		kvList.add(kv);*/
		
//		kv.setAttribute("IPAddress");
//		kv.setValue(itemName);
//		kvList.add(kv);
//		
//		kv.setAttribute("Alive");
//		kv.setValue("Yes");
//		kvList.add(kv);
//		
//		SelectRequest selectData = selectData(attr.getAttribute(), kvList);
//		String oldValue = "";
//		
//		for (Item item : sdb.select(selectData).getItems()) {
//            for (Attribute attribute : item.getAttributes()) {
//                oldValue = attribute.getValue();
//            }
//        }
		List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>();
        replaceableAttributes.add(new ReplaceableAttribute(attr.getAttribute(), attr.getValue(), true));
        sdb.putAttributes(new PutAttributesRequest(domainName, itemName, replaceableAttributes));
		
		// System.out.println("Old Value = " + oldValue);
		
//		List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>();
//        replaceableAttributes.add(new ReplaceableAttribute(attr.getAttribute(), attr.getValue(), true));
//        sdb.putAttributes(new PutAttributesRequest(domainName, itemName, replaceableAttributes));
//		
//		sdb.deleteAttributes(new DeleteAttributesRequest(domainName, itemName)
//        .withAttributes(new Attribute().withName(attr.getAttribute()).withValue(oldValue)));
//		
	}
        
	
	
	/*
	public static void main(String[] args) {
		SimpleDBImpl s = new SimpleDBImpl("test123");
		
		List<DBKeyValue> kvList = new ArrayList<DBKeyValue>();
		DBKeyValue kv = new DBKeyValue();
		
		kv.setAttribute("Name");
		kv.setValue("I5_*");
		
		kv.setAttribute("Category");
		kv.setValue("Car Parts");
		
		kv.setAttribute("Year");
		kv.setValue("2001");
		
		kvList.add(kv);
		
		SelectRequest sr = s.selectData("*", kvList);
		
		
		for (Item item : s.sdb.select(sr).getItems()) {
            System.out.println("  Item");
            System.out.println("    Name: " + item.getName());
            for (Attribute attribute : item.getAttributes()) {
                System.out.println("      Attribute");
                System.out.println("        Name:  " + attribute.getName());
                System.out.println("        Value: " + attribute.getValue());
            }
        }
		
		s.updateData("Item_05", kv);
		
SelectRequest sr = s.selectData("*", kvList);
		
		
		for (Item item : s.sdb.select(sr).getItems()) {
            System.out.println("  Item");
            System.out.println("    Name: " + item.getName());
            for (Attribute attribute : item.getAttributes()) {
                System.out.println("      Attribute");
                System.out.println("        Name:  " + attribute.getName());
                System.out.println("        Value: " + attribute.getValue());
            }
        }
		
	}
	*/
	
}