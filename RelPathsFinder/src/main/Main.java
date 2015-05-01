package main;

import java.io.IOException;

import relations.RelPathsFinder;

public class Main {
	public static void main(String[] args) throws IOException {
		String inputFile = null;
		int sentencesNo = 0;
		String relsFile = null;
		double selectionPercent = 0;
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
			
			else if (args[i].intern() == "-r") {
				relsFile = args[i + 1];
				i++;
			}
			
			else if ((args[i].intern() == "-p") && (i < args.length - 1)) {
				selectionPercent = Double.parseDouble(args[i + 1]);
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
		if (relsFile == null)
			throw new IllegalArgumentException("The -r argument is missing");
		if (outputFile == null)
			throw new IllegalArgumentException("The -o argument is missing");
		
		RelPathsFinder finder = new RelPathsFinder(inputFile, sentencesNo, relsFile, selectionPercent, outputFile);
		finder.run();
	}
}
