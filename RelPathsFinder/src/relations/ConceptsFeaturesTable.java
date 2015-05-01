package relations;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class ConceptsFeaturesTable {
	private String relsFile;
	private HashMap<String, HashMap<String, String>> conceptsFeatures;
	
	public ConceptsFeaturesTable(String relsFile) {
		this.relsFile = relsFile;
		this.conceptsFeatures = new HashMap<String, HashMap<String, String>>();
	}
	
	public void read() throws IOException {
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(relsFile));
		while ((line = reader.readLine()) != null) {
			String[] lineParts = line.split("\t");
			HashMap<String, String> conceptMap = conceptsFeatures.get(lineParts[0]);
			if (conceptMap == null) {
				conceptMap = new HashMap<String, String>();
				conceptsFeatures.put(lineParts[0], conceptMap);
			}
			conceptMap.put(lineParts[2], lineParts[1]);
		}
		reader.close();
	}
	
	public Iterable<WordAndRel> getConceptFeatures(String word) {
		LinkedList<WordAndRel> ret = new LinkedList<WordAndRel>();
		HashMap<String, String> wordMap = conceptsFeatures.get(word);
		if (wordMap != null) {
			for (Entry<String, String> entry : wordMap.entrySet())
				ret.add(new WordAndRel(entry.getKey(), entry.getValue()));
		}
		return ret;
	}
	
	public static class RelAndFile {
		public String rel;
		public String file;
		
		public RelAndFile(String rel, String file) {
			super();
			this.rel = rel;
			this.file = file;
		}
		
		@Override
		public String toString() {
			return "(" + rel + ", " + file + ")";
		}
	}
	
	public static class WordAndRel {
		public String word;
		public String rel;
		
		public WordAndRel(String word, String rel) {
			super();
			this.word = word;
			this.rel = rel;
		}
	}
}
