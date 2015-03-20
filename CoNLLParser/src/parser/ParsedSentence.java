package parser;

import java.util.*;

public class ParsedSentence {
	private ArrayList<ParsedWord> words;
	private HashMap<Integer, ArrayList<ParsedWord>> needGovernor;
	
	ParsedSentence() {
		words = new ArrayList<ParsedWord>();
		needGovernor = new HashMap<Integer, ArrayList<ParsedWord>>();
	}
	
	public int length() {
		return words.size();
	}
	
	public ParsedWord getWord(int index) {
		return words.get(index - 1);
	}
	
	ParsedWord addWord(String wordLine) {
		String[] lineParts = wordLine.split("\t");
		
		POSTag pos;
		try {
			pos = POSTag.valueOf(lineParts[3]);
		}
		catch (IllegalArgumentException ex) {
			pos = POSTag.Other;
		}
		
		ParsedWord word = new ParsedWord(Integer.parseInt(lineParts[0]), lineParts[1], pos, Integer.parseInt(lineParts[6]), RelTag.valueOf(lineParts[7]));
		words.add(word);
		
		ArrayList<ParsedWord> governorCurrent = needGovernor.get(words.size());
		if (governorCurrent != null) {
			for (ParsedWord dependent : governorCurrent)
				dependent.governor = word;
			word.dependents.addAll(governorCurrent);
			needGovernor.remove(words.size());
		}
		
		if (word.head > 0) {
			if (word.head < words.size()) {
				ParsedWord governor = getWord(word.head);
				word.governor = governor;
				governor.dependents.add(word);
			}
			else {
				ArrayList<ParsedWord> governorDependents = needGovernor.get(word.head);
				if (governorDependents == null) {
					governorDependents = new ArrayList<ParsedWord>();
					needGovernor.put(word.head, governorDependents);
				}
				governorDependents.add(word);
			}
		}
		
		return word;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder("[");
		for (ParsedWord word : words)
			ret.append(word.toString() + ", ");
		ret.delete(ret.length() - 2, ret.length());
		ret.append("]");
		return ret.toString();
	}
}
