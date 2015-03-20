package parser;

import java.util.*;

public class ParsedWord {
	public int index;
	public String word;
	public POSTag pos;
	public int head;
	public RelTag rel;
	public ParsedWord governor;
	public List<ParsedWord> dependents;
	
	public ParsedWord(int index, String word, POSTag pos, int head, RelTag rel) {
		this.index = index;
		this.word = word;
		this.pos = pos;
		this.head = head;
		this.rel = rel;
		this.governor = null;
		this.dependents = new ArrayList<ParsedWord>();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(index, word, pos, head, rel);
	}
	
	@Override
	public boolean equals(Object word) {
		if (!(word instanceof ParsedWord))
			return false;
		ParsedWord other = (ParsedWord)word;
		return ((this.index == other.index) && (this.word.intern() == other.word.intern())
				&& (this.pos == other.pos) && (this.head == other.head)
				&& (this.rel == other.rel));
	}
	
	@Override
	public String toString() {
		return "(" + index + ", " + word + ", " + pos + ", " + head + ", " + rel + ")";
	}
}