package relations;

import java.util.HashMap;
import java.util.Map.Entry;

public class VerbAdditions {
	private int noObj;
	private HashMap<String, Integer> dobj;
	private HashMap<String, HashMap<String, Integer>> adp;
	
	public VerbAdditions() {
		noObj = 0;
		dobj = new HashMap<String, Integer>();
		adp = new HashMap<String, HashMap<String, Integer>>();
	}
	
	public void addWithoutObj() {
		noObj++;
	}
	
	public void addDobj(String dobj) {
		Integer count = this.dobj.get(dobj);
		if (count == null)
			this.dobj.put(dobj, 1);
		else
			this.dobj.put(dobj, count + 1);
	}
	
	public void addAdp(String adpmod, String adpobj) {
		HashMap<String, Integer>  adpmodCounts = adp.get(adpmod);
		if (adpmodCounts == null) {
			adpmodCounts = new HashMap<String, Integer>();
			adp.put(adpmod, adpmodCounts);
		}
		
		Integer count = adpmodCounts.get(adpobj);
		if (count == null)
			adpmodCounts.put(adpobj, 1);
		else
			adpmodCounts.put(adpobj, count + 1);
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("no obj: " + noObj);
		if (dobj.size() > 0) {
			ret.append(", direct:");
			for (Entry<String, Integer> entry : dobj.entrySet())
				ret.append(" (" + entry.getKey() + "," + entry.getValue() + ")");
		}
		if (adp.size() > 0) {
			ret.append(", adp:");
			for (Entry<String, HashMap<String, Integer>> entry1 : adp.entrySet()) {
				for (Entry<String, Integer> entry2 : entry1.getValue().entrySet())
					ret.append(" (" + entry1.getKey() + " " + entry2.getKey() + "," + entry2.getValue() + ")");
			}
		}
		return ret.toString();
	}
}
