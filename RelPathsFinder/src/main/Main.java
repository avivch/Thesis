package main;

import relations.RelPathsFinder;

public class Main {
	public static void main(String[] args) {
		String inputFile = null, conceptsFile = null, outputFile = null;
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
			else if ((args[i].intern() == "-c") && (i < args.length - 1)) {
				conceptsFile = args[i + 1];
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
		if (conceptsFile == null)
			return;
		if (outputFile == null)
			return;
		RelPathsFinder finder = new RelPathsFinder(inputFile, sentencesNo, conceptsFile, outputFile);
		finder.run();
	}
}
