package main;

import java.util.LinkedList;

import relations.RelPathsChooser;
import relations.RelPathsChooser.RelAndFile;

public class Main {
	public static void main(String[] args) {
		RelAndFile[] input = null;
		double selectionPercent = 0;
		String outputFile = null;
		int i = 0;
		while (i < args.length) {
			if (args[i].intern() == "-i") {
				LinkedList<RelAndFile> list = new LinkedList<RelAndFile>();
				i++;
				while ((i < args.length - 1) && (!args[i].startsWith("-"))) {
					String[] relAndFile = args[i].split("\\|");
					list.add(new RelAndFile(relAndFile[0], relAndFile[1]));
					i++;
				}
				input = list.toArray(new RelAndFile[] { });
				if (i < args.length - 1)
					i--;
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
		if (input == null)
			return;
		if (selectionPercent == 0)
			return;
		if (outputFile == null)
			return;
		RelPathsChooser chooser = new RelPathsChooser(input, selectionPercent, outputFile);
		chooser.run();
	}
}
