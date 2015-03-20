package relations;

import parser.*;

public class RelFinderVerbs extends SentencesReader {
	private String outputFile;
	private RelTableVerbs relTable;
	
	public RelFinderVerbs(String inputFile, String outputFile) {
		this(inputFile, 0, outputFile);
	}

	public RelFinderVerbs(String inputFile, int sentencesNo, String outputFile) {
		super(inputFile, sentencesNo);
		this.outputFile = outputFile;
		this.relTable = null;
	}

	@Override
	public void run() {
		try {
			relTable = new RelTableVerbs();
			readSentences();
			//relTable.calcLikelihoodRatios();
			relTable.outputRelTable(outputFile);
			relTable.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	protected void processSentence(ParsedSentence sentence) {
		for (int i = 1; i < sentence.length(); i++) {
			ParsedWord word = sentence.getWord(i);
			if (((word.pos == POSTag.NN) || (word.pos == POSTag.NNS)) && ((word.rel == RelTag.nsubj) || (word.rel == RelTag.nsubjpass)))
				checkSubj(word);
		}
	}
	
	private void checkSubj(ParsedWord word) {
		if ((word.governor.pos == POSTag.VB) || (word.governor.pos == POSTag.VBD) || (word.governor.pos == POSTag.VBG) || (word.governor.pos == POSTag.VBN) || (word.governor.pos == POSTag.VBP) || (word.governor.pos == POSTag.VBZ)) {
			relTable.addRelation(word.word.toLowerCase(), word.governor.word.toLowerCase());
			for (ParsedWord verbDep : word.governor.dependents) {
				if (verbDep.rel == RelTag.dobj)
					relTable.addRelation(word.word.toLowerCase(), word.governor.word.toLowerCase() + " " + verbDep.word.toLowerCase());
				else if (verbDep.rel == RelTag.adpmod) {
					for (ParsedWord comp : verbDep.dependents) {
						if (comp.rel == RelTag.adpcomp)
							relTable.addRelation(word.word.toLowerCase(), word.governor.word.toLowerCase() + " " + verbDep.word.toLowerCase() + " " + comp.word.toLowerCase());
					}
				}
			}
		}
	}
}
