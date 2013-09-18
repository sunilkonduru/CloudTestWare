package edu.uci.util.DB;

import java.util.List;

import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;

public interface ISimpleDB {
	
	public void createDB();
	
	public void insertData(List<ReplaceableItem> data);
	
	public SelectRequest selectData(String selectList, List<DBKeyValue> attrList);
	
	public void updateData(String itemName, DBKeyValue attr);
	
}
