package reader;

import java.util.*;

import parser.*;

public class POSRelPath {
	private LinkedList<Object> path;
	private String desc;
	
	public POSRelPath() {
		path = new LinkedList<Object>();
		desc = "";
	}
	
	public POSRelPath(String desc) {
		String[] parts = desc.split(" ");
		POSRelPath ret = new POSRelPath();
		for (int i = 0; i < parts.length; i++) {
			if (i % 2 == 1) {
				int splitIndex = parts[i].lastIndexOf(":");
				String word = parts[i].substring(0, splitIndex);
				POSTag pos = POSTag.valueOf(parts[i].substring(splitIndex + 1));
				ret = ret.addWord(word, pos);
			}
			else {
				if (parts[i].startsWith("<")) {
					RelTag rel = RelTag.valueOf(parts[i].substring(3, parts[i].length() - 2));
					ret = ret.addRel(rel, Direction.Governor);
				}
				else {
					RelTag rel = RelTag.valueOf(parts[i].substring(2, parts[i].length() - 3));
					ret = ret.addRel(rel, Direction.Dependent);
				}
			}
		}
		this.path = ret.path;
		this.desc = ret.desc;
	}
	
	private POSRelPath(POSRelPath original, String word, POSTag pos) {
		if (original.path.size() % 2 != 1)
			throw new IllegalArgumentException("Cannot add word, add relation instead");
		this.path = new LinkedList<Object>(original.path);
		this.path.add(new WordAndPOS(word, pos));
		this.desc = original.desc + " " + word + ":" + pos;
	}
	
	private POSRelPath(POSRelPath original, RelTag rel, Direction dir) {
		if (original.path.size() % 2 != 0)
			throw new IllegalArgumentException("Cannot add relation, add word instead");
		this.path = new LinkedList<Object>(original.path);
		this.path.add(new RelAndDir(rel, dir));
		if (dir == Direction.Governor)
			this.desc = original.desc + (this.path.size() > 1 ? " " : "") + "<--" + rel + "--";
		else // Dependent
			this.desc = original.desc + (this.path.size() > 1 ? " " : "") + "--" + rel + "-->";
	}
	
	public POSRelPath addWord(String word, POSTag pos) {
		return new POSRelPath(this, word, pos);
	}
	
	public POSRelPath addRel(RelTag rel, Direction dir) {
		return new POSRelPath(this, rel, dir);
	}
	
	public List<ParsedWord> followPath(ParsedWord word) {
		ArrayList<ParsedWord> candidates = new ArrayList<ParsedWord>();
		candidates.add(word);
		Iterator<Object> itr = path.iterator();
		while ((itr.hasNext()) && (!candidates.isEmpty())) {
			Object obj = itr.next();
			if (obj instanceof WordAndPOS) {
				WordAndPOS wordAndPOS = (WordAndPOS)obj;
				ArrayList<ParsedWord> newCandidates = new ArrayList<ParsedWord>();
				for (ParsedWord candidate : candidates) {
					if ((candidate.word.intern() == wordAndPOS.word.intern()) && (candidate.pos == wordAndPOS.pos))
						newCandidates.add(candidate);
				}
				candidates = newCandidates;
			}
			else { // obj instanceof RelAndDir
				RelAndDir relAndDir = (RelAndDir)obj;
				if (relAndDir.dir == Direction.Governor) {
					ArrayList<ParsedWord> newCandidates = new ArrayList<ParsedWord>();
					for (ParsedWord candidate : candidates) {
						if ((candidate.governor != null) && (candidate.rel == relAndDir.rel))
							newCandidates.add(candidate.governor);
					}
					candidates = newCandidates;
				}
				else { // relAndDir.dir == Direction.Dependent
					ArrayList<ParsedWord> newCandidates = new ArrayList<ParsedWord>();
					for (ParsedWord candidate : candidates) {
						for (ParsedWord dependent : candidate.dependents) {
							if (dependent.rel == relAndDir.rel)
								newCandidates.add(dependent);
						}
					}
					candidates = newCandidates;
				}
			}
		}
		return candidates;
	}
	
	@Override
	public int hashCode() {
		return desc.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof POSRelPath))
			return false;
		POSRelPath other = (POSRelPath)obj;
		return (this.desc.intern() == other.desc.intern());
	}
	
	@Override
	public String toString() {
		return desc;
	}
	
	private static class WordAndPOS {
		public String word;
		public POSTag pos;
		
		public WordAndPOS(String word, POSTag pos) {
			super();
			this.word = word;
			this.pos = pos;
		}
	}
	
	public static enum Direction {
		Governor, Dependent
	}
	
	private static class RelAndDir {
		public RelTag rel;
		public Direction dir;
		
		public RelAndDir(RelTag rel, Direction dir) {
			this.rel = rel;
			this.dir = dir;
		}
	}
}
