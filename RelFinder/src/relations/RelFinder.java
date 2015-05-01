package relations;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import parser.*;
import reader.*;

public class RelFinder extends SentencesReader {
	private String pathsFile;
	private HashMap<POSRelPath, String> relPaths;
	private RelTable relTable;
	private String outputFile;

	public RelFinder(String inputFile, int sentencesNo, String pathsFile, String outputFile) {
		super(inputFile, sentencesNo);
		this.pathsFile = pathsFile;
		this.relPaths = new HashMap<POSRelPath, String>();
		this.relTable = new RelTable();
		this.outputFile = outputFile;
	}

	@Override
	public void run() throws IOException {
		initRelPaths();
		readSentences();
		relTable.calcLikelihoodRatios();
		relTable.output(outputFile);
	}
	
	private void initRelPaths() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(pathsFile));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] lineParts = line.split("\t");
			relPaths.put(new POSRelPath(lineParts[0]), lineParts[1]);
		}
		reader.close();
	}
	
	@Override
	protected void processSentence(ParsedSentence sentence) {
		for (int i = 1; i < sentence.length(); i++) {
			ParsedWord word = sentence.getWord(i);
			
			for (Entry<POSRelPath, String> entry : relPaths.entrySet()) {
				POSRelPath path = entry.getKey();
				String rel = entry.getValue();
				
				for (ParsedWord feature : path.followPath(word)) {
					if (word.word.toLowerCase().intern() != feature.word.toLowerCase().intern())
						relTable.addRelation(word.word.toLowerCase(), rel, feature.word.toLowerCase());
				}
			}
		}
	}
}
