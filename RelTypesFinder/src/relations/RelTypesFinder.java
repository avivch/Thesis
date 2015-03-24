package relations;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import parser.*;
import parser.POSRelPath.Direction;

public class RelTypesFinder extends SentencesReader {
	private String conceptsFile;
	private HashMap<String, String[]> conceptsFeatures;
	private HashMap<POSRelPath, Integer> relTypes;
	private String outputFile;
	
	public RelTypesFinder(String inputFile, String conceptsFile, String outputFile) {
		this(inputFile, 0, conceptsFile, outputFile);
	}
	
	public RelTypesFinder(String inputFile, int sentencesNo, String conceptsFile, String outputFile) {
		super(inputFile, sentencesNo);
		this.conceptsFile = conceptsFile;
		this.relTypes = new HashMap<POSRelPath, Integer>();
		this.outputFile = outputFile;
	}

	@Override
	public void run() {
		try {
			initConceptsFeatures();
			readSentences();
			outputRelTypes();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void initConceptsFeatures() throws IOException {
		conceptsFeatures = new HashMap<String, String[]>();
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(conceptsFile));
		while ((line = reader.readLine()) != null) {
			String[] lineParts = line.split("\t");
			String concept = lineParts[0];
			String[] features = lineParts[1].split(",");
			for (int i = 0; i < features.length; i++)
				features[i] = features[i].toLowerCase();
			
			conceptsFeatures.put(concept, features);
		}
		reader.close();
	}
	
	@Override
	protected void processSentence(ParsedSentence sentence) {
		for (int i = 1; i < sentence.length(); i++) {
			ParsedWord word = sentence.getWord(i);
			
			if ((word.pos == POSTag.NN) || (word.pos == POSTag.NNS)) {
				for (Entry<String, String[]> entry : conceptsFeatures.entrySet()) {
					String concept = entry.getKey();
					String[] features = entry.getValue();
					
					if (word.word.toLowerCase().intern() == concept.intern()) {
						for (POSRelPath path : getPathsToFeatures(word, features)) {
							Integer count = relTypes.get(path);
							if (count == null)
								relTypes.put(path, 1);
							else
								relTypes.put(path, count + 1);
						}
						break;
					}
				}
			}
		}
	}
	
	private ArrayList<POSRelPath> getPathsToFeatures(ParsedWord word, String[] features) {
		ArrayList<POSRelPath> paths = new ArrayList<POSRelPath>();
		
		Queue<WordPath> q = new LinkedList<WordPath>();
		Set<ParsedWord> v = new HashSet<ParsedWord>();
		v.add(word);
		q.add(new WordPath(word, new POSRelPath()));
		
		while (!q.isEmpty()) {
			WordPath wordPath = q.remove();
			
			for (String feature : features) {
				if (wordPath.word.word.toLowerCase().intern() == feature.intern()) {
					paths.add(wordPath.path.addPOS(wordPath.word.pos));
					break;
				}
			}
			
			if ((wordPath.word.governor != null) && (!v.contains(wordPath.word.governor))) {
				v.add(wordPath.word.governor);
				POSRelPath path = wordPath.path.addPOS(wordPath.word.pos);
				path = path.addRel(wordPath.word.rel, Direction.Governor);
				q.add(new WordPath(wordPath.word.governor, path));
			}
			
			for (ParsedWord dep : wordPath.word.dependents) {
				if (!v.contains(dep)) {
					v.add(dep);
					POSRelPath path = wordPath.path.addPOS(wordPath.word.pos);
					path = path.addRel(dep.rel, Direction.Dependent);
					q.add(new WordPath(dep, path));
				}
			}
		}
		
		return paths;
	}
	
	private void outputRelTypes() throws IOException {
		SortedSet<Entry<POSRelPath, Integer>> sorted = new TreeSet<Entry<POSRelPath, Integer>>(new Comparator<Entry<POSRelPath, Integer>>() {
			@Override
			public int compare(Entry<POSRelPath, Integer> o1, Entry<POSRelPath, Integer> o2) {
				int res = o2.getValue() - o1.getValue();
				return (res < 0) ? -1 : 1;
			}
		});
		sorted.addAll(relTypes.entrySet());
		
		PrintWriter	writer = new PrintWriter(outputFile);
		for (Entry<POSRelPath, Integer> entry : sorted)
			writer.println(entry.getKey().toString() + "\t" + entry.getValue());
		writer.close();
	}
	
	private static class WordPath {
		public ParsedWord word;
		public POSRelPath path;
		
		public WordPath(ParsedWord word, POSRelPath path) {
			this.word = word;
			this.path = path;
		}
		
		@Override
		public String toString() {
			return "(" + word.toString() + ", " + path.toString() + ")";
		}
	}
}
