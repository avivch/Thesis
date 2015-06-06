package main;

import java.io.IOException;

import context.ContextFinder;

public class Main {
	public static void main(String[] args) throws IOException {
		String inputFile = null;
		int sentencesNo = 0;
		String wordsFile = null;
		String outputFile = null;
		int i = 0;
		while (i < args.length) {
			if ((args[i].intern() == "-i") && (i < args.length - 1)) {
				inputFile = args[i + 1];
				i++;
			}
			
			else if ((args[i].intern() == "-s") && (i < args.length - 1)) {
				sentencesNo = Integer.parseInt(args[i + 1]);
				i++;
			}
			
			else if (args[i].intern() == "-w") {
				wordsFile = args[i + 1];
				i++;
			}
			
			else if ((args[i].intern() == "-o") && (i < args.length - 1)) {
				outputFile = args[i + 1];
				i++;
			}
			
			i++;
		}
		
		if (inputFile == null)
			throw new IllegalArgumentException("The -i argument is missing");
		if (wordsFile == null)
			throw new IllegalArgumentException("The -w argument is missing");
		if (outputFile == null)
			throw new IllegalArgumentException("The -o argument is missing");
		
		ContextFinder finder = new ContextFinder(inputFile, sentencesNo, wordsFile, outputFile);
		finder.run();
	}
}
