package main;

import java.io.IOException;

import relations.RelFinder;

public class Main {
	public static void main(String[] args) throws IOException {
		String inputFile = null;
		int sentencesNo = 0;
		String pathsFile = null;
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
			
			else if ((args[i].intern() == "-p") && (i < args.length - 1)) {
				pathsFile = args[i + 1];
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
		if (pathsFile == null)
			throw new IllegalArgumentException("The -p argument is missing");
		if (outputFile == null)
			throw new IllegalArgumentException("The -o argument is missing");
		
		RelFinder finder = new RelFinder(inputFile, sentencesNo, pathsFile, outputFile);
		finder.run();
	}
}
