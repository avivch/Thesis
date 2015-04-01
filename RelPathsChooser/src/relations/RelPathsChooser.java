package relations;

import java.io.*;
import java.util.*;

import parser.POSRelPath;

public class RelPathsChooser {
	private RelAndFile[] input;
	private double selectionPercent;
	private String outputFile;
	
	public RelPathsChooser(RelAndFile[] input, double selectionPercent, String outputFile) {
		this.input = input;
		this.selectionPercent = selectionPercent;
		this.outputFile = outputFile;
	}
	
	public void run() {
		try {
			boolean first = true;
			PrintWriter writer = new PrintWriter(outputFile);
			for (RelAndFile relAndFile : input) {
				if (!first)
					writer.println();
				else
					first = false;
				writer.println(relAndFile.rel);
				for (POSRelPath path : choosePaths(relAndFile.file))
					writer.println(path.toString());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<POSRelPath> choosePaths(String file) throws IOException {
		String line;
		int countAll = 0;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while ((line = reader.readLine()) != null) {
			int count = Integer.parseInt(line.split("\t")[1]);
			countAll += count;
		}
		int pathsCountNo = (int)Math.ceil(selectionPercent / 100 * countAll);
		reader.close();
		
		ArrayList<POSRelPath> paths = new ArrayList<POSRelPath>();
		int pathsChosen = 0;
		int lastCount = 0;
		reader = new BufferedReader(new FileReader(file));
		while ((line = reader.readLine()) != null) {
			String[] lineParts = line.split("\t");
			POSRelPath path = new POSRelPath(lineParts[0]);
			int count = Integer.parseInt(lineParts[1]);
			
			if ((pathsChosen < pathsCountNo) || (count == lastCount)) {
				paths.add(path);
				pathsChosen += count;
				lastCount = count;
			}
			else
				break;
		}
		reader.close();
		return paths;
	}
	
	public static class RelAndFile {
		public String rel;
		public String file;
		
		public RelAndFile(String rel, String file) {
			super();
			this.rel = rel;
			this.file = file;
		}
		
		@Override
		public String toString() {
			return "(" + rel + ", " + file + ")";
		}
	}
}
