package context;

import java.io.*;
import java.util.HashSet;

import parser.*;
import reader.SentencesReader;

public class ContextFinder extends SentencesReader {
	private String wordsFile;
	private HashSet<String> words;
	private ContextTable contextTable;
	private String outputFile;
	
	public ContextFinder(String inputFile, int sentencesNo, String wordsFile, String outputFile) {
		super(inputFile, sentencesNo);
		this.wordsFile = wordsFile;
		this.words = new HashSet<String>();
		this.contextTable = new ContextTable();
		this.outputFile = outputFile;
	}

	@Override
	public void run() throws IOException {
		readWords();
		readSentences();
		contextTable.output(outputFile);
	}
	
	private void readWords() throws IOException {
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(wordsFile));
		while ((line = reader.readLine()) != null)
			words.add(line);
		reader.close();
	}

	@Override
	protected void processSentence(ParsedSentence sentence) {
		for (int i = 1; i < sentence.length(); i++) {
			ParsedWord word = sentence.getWord(i);
			if (words.contains(word.word.toLowerCase())) {
				for (int j = 1; j < sentence.length(); j++) {
					ParsedWord context = sentence.getWord(j);
					if ((word.word.toLowerCase().intern() != context.word.toLowerCase().intern()) &&
							(checkContextPOS(context.pos)) && (checkContextContent(context.word.toLowerCase()))) {
						contextTable.addContext(word.word.toLowerCase(), context.word.toLowerCase());
					}
				}
			}
		}
	}
	
	private boolean checkContextPOS(POSTag pos) {
		return (pos == POSTag.JJ) || (pos == POSTag.JJR) || (pos == POSTag.JJS) ||
				(pos == POSTag.NN) || (pos == POSTag.NNS) || (pos == POSTag.NNP) || (pos == POSTag.NNPS) ||
				(pos == POSTag.VB) || (pos == POSTag.VBD) || (pos == POSTag.VBG) ||
				(pos == POSTag.VBN) || (pos == POSTag.VBP) || (pos == POSTag.VBZ);
	}
	
	private boolean checkContextContent(String context) {
		return (context.intern() != "is") && (context.intern() != "was") && (context.intern() != "are") &&
				(context.intern() != "be") && (context.intern() != "were") && (context.intern() != "has") &&
				(context.intern() != "have") && (context.intern() != "not") && (context.intern() != "had") &&
				(context.intern() != "been");
	}
}
