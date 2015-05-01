package relations;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class ConceptsFeaturesTable {
	private Iterable<RelAndFile> input;
	private HashMap<String, HashMap<String, String>> conceptsFeatures;
	
	public ConceptsFeaturesTable(Iterable<RelAndFile> relFiles) {
		this.input = relFiles;
		this.conceptsFeatures = new HashMap<String, HashMap<String, String>>();
	}
	
	public void read() throws IOException {
		for (RelAndFile relAndFile : input) {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(relAndFile.file));
			while ((line = reader.readLine()) != null) {
				String[] lineParts = line.split("\t");
				String concept = lineParts[0];
				String[] features = lineParts[1].split(",");
				for (String feature : features) {
					HashMap<String, String> conceptMap = conceptsFeatures.get(concept);
					if (conceptMap == null) {
						conceptMap = new HashMap<String, String>();
						conceptsFeatures.put(concept, conceptMap);
					}
					conceptMap.put(feature.toLowerCase(), relAndFile.rel);
				}
			}
			reader.close();
		}
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
