package context;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class ContextTable {
	private HashMap<String, HashMap<String, Integer>> contexts;
	
	public ContextTable() {
		contexts = new HashMap<String, HashMap<String, Integer>>();
	}
	
	public void addContext(String word, String context) {
		HashMap<String, Integer> wordMap = contexts.get(word);
		if (wordMap != null) {
			Integer count = wordMap.get(context);
			if (count != null)
				wordMap.put(context, count + 1);
			else
				wordMap.put(context, 1);
		}
		else {
			wordMap = new HashMap<String, Integer>();
			wordMap.put(context, 1);
			contexts.put(word, wordMap);
		}
	}
	
	public void output(String outputFile) throws IOException {
		PrintWriter writer = new PrintWriter(outputFile);
		TreeSet<Entry<String, HashMap<String, Integer>>> sortedWords = new TreeSet<Entry<String, HashMap<String, Integer>>>(new WordsComparator());
		sortedWords.addAll(contexts.entrySet());
		for (Entry<String, HashMap<String, Integer>> entry1 : sortedWords) {
			writer.print(entry1.getKey());
			TreeSet<Entry<String, Integer>> sortedContexts = new TreeSet<Entry<String, Integer>>(new ContextsComparator());
			sortedContexts.addAll(entry1.getValue().entrySet());
			for (Entry<String, Integer> entry2 : sortedContexts)
				writer.print("\t" + entry2.getKey() + ":" + entry2.getValue());
			writer.println();
		}
		writer.close();
	}
	
	private static class WordsComparator implements Comparator<Entry<String, HashMap<String, Integer>>> {
		@Override
		public int compare(Entry<String, HashMap<String, Integer>> o1, Entry<String, HashMap<String, Integer>> o2) {
			return o1.getKey().compareTo(o2.getKey());
		}
	}
	
	private static class ContextsComparator implements Comparator<Entry<String, Integer>> {
		@Override
		public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
			int res = o2.getValue() - o1.getValue();
			return (res < 0) ? -1 : 1;
		}
	}
}
