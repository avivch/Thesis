package relations;

import java.io.*;
import java.util.*;

import parser.*;
import reader.*;
import reader.POSRelPath.Direction;
import relations.ConceptsFeaturesTable.*;

public class RelPathsFinder extends SentencesReader {
	private ConceptsFeaturesTable conceptsFeatures;
	private RelPathsTable relPaths;
	private double selectionPercent;
	private String outputFile;
	
	public RelPathsFinder(String inputFile, int sentencesNo, String relsFile, double selectionPercent, String outputFile) {
		super(inputFile, sentencesNo);
		this.conceptsFeatures = new ConceptsFeaturesTable(relsFile);
		this.relPaths = new RelPathsTable();
		this.selectionPercent = selectionPercent;
		this.outputFile = outputFile;
	}

	@Override
	public void run() throws IOException {
		conceptsFeatures.read();
		readSentences();
		if (selectionPercent != 0)
			relPaths.outputBest(outputFile, selectionPercent);
		else
			relPaths.outputAll(outputFile);
	}
	
	@Override
	protected void processSentence(ParsedSentence sentence) {
		for (int i = 1; i < sentence.length(); i++) {
			ParsedWord word = sentence.getWord(i);
			if ((word.pos == POSTag.NN) || (word.pos == POSTag.NNS)) {
				Iterable<WordAndRel> features = conceptsFeatures.getConceptFeatures(word.word.toLowerCase());
				findPathsToFeatures(word, features);
			}
		}
	}
	
	private void findPathsToFeatures(ParsedWord source, Iterable<WordAndRel> features) {
		LinkedList<WordPath> activePaths = new LinkedList<WordPath>();
		HashSet<ParsedWord> wordHistory = new HashSet<ParsedWord>();
		wordHistory.add(source);
		activePaths.add(new WordPath(source, new POSRelPath()));
		
		while (!activePaths.isEmpty()) {
			WordPath wordPath = activePaths.remove();
			
			for (WordAndRel feature : features) {
				if (wordPath.word.word.toLowerCase().intern() == feature.word.intern()) {
					relPaths.addPath(wordPath.path, feature.rel);
					break;
				}
			}
			
			if ((wordPath.word.governor != null) && (!wordHistory.contains(wordPath.word.governor))) {
				wordHistory.add(wordPath.word.governor);
				POSRelPath path = wordPath.path;
				if (!wordPath.word.equals(source))
					path = path.addWord(wordPath.word.word, wordPath.word.pos);
				path = path.addRel(wordPath.word.rel, Direction.Governor);
				activePaths.add(new WordPath(wordPath.word.governor, path));
			}
			
			for (ParsedWord dep : wordPath.word.dependents) {
				if (!wordHistory.contains(dep)) {
					wordHistory.add(dep);
					POSRelPath path = wordPath.path;
					if (!wordPath.word.equals(source))
						path = path.addWord(wordPath.word.word, wordPath.word.pos);
					path = path.addRel(dep.rel, Direction.Dependent);
					activePaths.add(new WordPath(dep, path));
				}
			}
		}
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
