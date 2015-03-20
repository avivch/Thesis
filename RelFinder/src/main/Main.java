package main;

import relations.RelFinder;

public class Main {
	public static void main(String[] args) {
		String inputFile = null, outputFile = null;
		int sentencesNo = 0;
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
			else if ((args[i].intern() == "-o") && (i < args.length - 1)) {
				outputFile = args[i + 1];
				i++;
			}
			
			i++;
		}
		if (inputFile == null)
			return;
		if (outputFile == null)
			outputFile = inputFile + ".rel";
		RelFinder finder = new RelFinder(inputFile, sentencesNo, outputFile);
		finder.run();
	}
}
