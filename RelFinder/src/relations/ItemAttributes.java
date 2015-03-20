package relations;

import java.util.HashMap;
import java.util.Map.Entry;

public class ItemAttributes {
	private HashMap<String, Integer> adjectives;
	private HashMap<String, VerbAdditions> verbs;
	
	public ItemAttributes() {
		adjectives = new HashMap<String, Integer>();
		verbs = new HashMap<String, VerbAdditions>();
	}
	
	public void addAdjective(String adjective) {
		Integer count = adjectives.get(adjective);
		if (count == null)
			adjectives.put(adjective, 1);
		else
			adjectives.put(adjective, count + 1);
	}
	
	private VerbAdditions getOrCreateVerbAdditions(String verb) {
		VerbAdditions verbCounts = verbs.get(verb);
		if (verbCounts == null) {
			verbCounts = new VerbAdditions();
			verbs.put(verb, verbCounts);
		}
		return verbCounts;
	}
	
	public void addVerbWithoutObj(String verb) {
		VerbAdditions verbCounts = getOrCreateVerbAdditions(verb);
		verbCounts.addWithoutObj();
	}
	
	public void addVerbWithDobj(String verb, String dobj) {
		VerbAdditions verbCounts = getOrCreateVerbAdditions(verb);
		verbCounts.addDobj(dobj);
	}
	
	public void addVerbWithAdp(String verb, String adpmod, String adpobj) {
		VerbAdditions verbCounts = getOrCreateVerbAdditions(verb);
		verbCounts.addAdp(adpmod, adpobj);
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		if (adjectives.size() > 0) {
			ret.append("[adjectives:");
			for (Entry<String, Integer> entry : adjectives.entrySet())
				ret.append(" (" + entry.getKey() + "," + entry.getValue() + ")");
			ret.append("]");
		}
		if (verbs.size() > 0) {
			if (ret.length() > 0)
				ret.append(", ");
			ret.append("[verbs:");
			for (Entry<String, VerbAdditions> entry : verbs.entrySet())
				ret.append(" (" + entry.getKey() + ", " + entry.getValue().toString() + ")");
			ret.append("]");
		}
		return ret.toString();
	}
}
