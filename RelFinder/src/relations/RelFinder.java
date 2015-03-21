package relations;

import java.util.*;

import parser.*;

public class RelFinder extends SentencesReader {
	private RelTable relTable;
	private String outputFile;
	
	public RelFinder(String inputFile, String outputFile) {
		this(inputFile, 0, outputFile);
	}

	public RelFinder(String inputFile, int sentencesNo, String outputFile) {
		super(inputFile, sentencesNo);
		this.outputFile = outputFile;
		this.relTable = null;
	}

	@Override
	public void run() {
		try {
			relTable = new RelTable();
			readSentences();
			relTable.calcLikelihoodRatios();
			relTable.outputRelTable(outputFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	protected void processSentence(ParsedSentence sentence) {
		for (int i = 1; i < sentence.length(); i++) {
			ParsedWord word = sentence.getWord(i);
			switch (word.rel) {
				case acomp:
					checkAcomp(word);
					break;
				case amod:
					checkAmod(word);
					break;
				case attr:
					checkAttr(word);
					break;
				case xcomp:
					checkXcomp(word);
				default:
					break;
			}
		}
	}

	private List<ParsedWord> getConjWords(ParsedWord word) {
		ArrayList<ParsedWord> conjWords = new ArrayList<ParsedWord>();
		conjWords.add(word);
		for (ParsedWord dep : word.dependents) {
			if (dep.rel == RelTag.conj)
				conjWords.addAll(getConjWords(dep));
		}
		return conjWords;
	}
	
	private boolean isNegative(ParsedWord word) {
		for (ParsedWord wordDep : word.dependents) {
			if (wordDep.rel == RelTag.neg)
				return true;
		}
		if (word.governor == null)
			return false;
		for (ParsedWord govDep : word.governor.dependents) {
			if (govDep.rel == RelTag.neg)
				return true;
		}
		return false;
	}
	
	private void addToRelationsWithConjAndNeg(ParsedWord governor, POSTag[] allowedGovernorPOS, ParsedWord dependent, POSTag[] allowedDependentPOS) {
		for (ParsedWord gov : getConjWords(governor)) {
			if ((gov.word.intern() == "[") || (gov.word.intern() == "]"))
				continue;
			for (POSTag govPOS : allowedGovernorPOS) {
				if (gov.pos == govPOS) {
					for (ParsedWord dep : getConjWords(dependent)) {
						if ((dep.word.intern() == "[") || (dep.word.intern() == "]"))
							continue;
						for (POSTag depPOS : allowedDependentPOS) {
							if (dep.pos == depPOS) {
								if (isNegative(dep))
									relTable.addRelation(gov.word.toLowerCase(), "is", "not " + dep.word.toLowerCase());
								else
									relTable.addRelation(gov.word.toLowerCase(), "is", dep.word.toLowerCase());
								break;
							}
						}
					}
					break;
				}
			}
		}
	}
	
	private void checkAcomp(ParsedWord word) {
		ParsedWord verb = word.governor;
		if (verb.rel == RelTag.xcomp)
			verb = verb.governor;
		boolean subjFound = findAcompSubj(verb, word);
		if ((!subjFound) && (verb.rel == RelTag.conj))
			findAcompSubj(verb.governor, word);
	}
	
	private boolean findAcompSubj(ParsedWord verb, ParsedWord word) {
		for (ParsedWord verbDep : verb.dependents) {
			if ((verbDep.rel == RelTag.nsubj) || (verbDep.rel == RelTag.nsubjpass)) {
				if (((verbDep.pos == POSTag.WDT) || (verbDep.pos == POSTag.WP)) && (verb.rel == RelTag.rcmod))
					verbDep = verb.governor;
				addToRelationsWithConjAndNeg(verbDep, new POSTag[] { POSTag.NN, POSTag.NNS }, word, new POSTag[] { POSTag.JJ, POSTag.VBD, POSTag.VBG, POSTag.VBN });
				return true;
			}
		}
		return false;
	}
	
	private void checkAmod(ParsedWord word) {
		addToRelationsWithConjAndNeg(word.governor, new POSTag[] { POSTag.NN, POSTag.NNS }, word, new POSTag[] { POSTag.JJ, POSTag.VBD, POSTag.VBG, POSTag.VBN });
	}
	
	private void checkAttr(ParsedWord word) {
		ParsedWord verb = word.governor;
		if (verb.rel == RelTag.xcomp)
			verb = verb.governor;
		boolean subjFound = findAttrSubj(verb, word);
		if ((!subjFound) && (verb.rel == RelTag.conj))
			findAttrSubj(verb.governor, word);
	}
	
	private boolean findAttrSubj(ParsedWord verb, ParsedWord word) {
		for (ParsedWord verbDep : verb.dependents) {
			if ((verbDep.rel == RelTag.nsubj) || (verbDep.rel == RelTag.nsubjpass)) {
				if (((verbDep.pos == POSTag.WDT) || (verbDep.pos == POSTag.WP)) && (verb.rel == RelTag.rcmod))
					verbDep = verb.governor;
				addToRelationsWithConjAndNeg(verbDep, new POSTag[] { POSTag.NN, POSTag.NNS }, word, new POSTag[] { POSTag.JJ });
				return true;
			}
		}
		return false;
	}

	private void checkXcomp(ParsedWord word) {
		for (ParsedWord wordDep : word.dependents) {
			if ((wordDep.rel == RelTag.nsubj) || (wordDep.rel == RelTag.nsubjpass)) {
				addToRelationsWithConjAndNeg(wordDep, new POSTag[] { POSTag.NN, POSTag.NNS }, word, new POSTag[] { POSTag.JJ });
				break;
			}
		}
	}
}
