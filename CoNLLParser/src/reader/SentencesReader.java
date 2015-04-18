package reader;

import java.io.*;

import parser.ParsedSentence;

public abstract class SentencesReader {
	private String inputFile;
	private ProgressPrinter progressPrinter;
	
	protected SentencesReader(String inputFile) {
		this(inputFile, 0);
	}
	
	protected SentencesReader(String inputFile, int sentencesNo) {
		this.inputFile = inputFile;
		if (sentencesNo > 0)
			this.progressPrinter = new ProgressPrinter(sentencesNo);
		else
			this.progressPrinter = null;
	}
	
	public abstract void run();
	
	protected void readSentences() throws IOException {
		int finished = 0;
		if (progressPrinter != null)
			progressPrinter.reportProgress(0);
		
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));

		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			
			ParsedSentence sentence = new ParsedSentence();
			while ((line != null) && (line.intern() != "")) {
				sentence.addWord(line);
				line = reader.readLine();
			}
			processSentence(sentence);
			
			finished++;
			if (progressPrinter != null)
				progressPrinter.reportProgress(finished);
			
			if (line == null)
				break;
		}

		reader.close();
	}
	
	protected abstract void processSentence(ParsedSentence sentence);
}
