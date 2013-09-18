package util;

import java.util.ArrayList;
import java.util.List;

import server.DBKeyValue;

public class DBUtil {
	
	public static List<DBKeyValue> findFreeAndIdleSubscribers() {
		List<DBKeyValue> kvList = new ArrayList<DBKeyValue>();
		DBKeyValue kv = new DBKeyValue();
		
		kv.setAttribute("Status");
		kv.setValue("Idle");
		kvList.add(kv);
		
		kv.setAttribute("Alive");
		kv.setValue("Yes");
		kvList.add(kv);
		
		return kvList;
	}

}
