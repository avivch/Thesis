package relations;

import java.io.*;

import reader.POSRelPath;

public class RelPathsChooser {
	private RelAndFile[] input;
	private RelPathsTable relPathsTable;
	private double selectionPercent;
	private String outputFile;
	
	public RelPathsChooser(RelAndFile[] input, double selectionPercent, String outputFile) {
		this.input = input;
		this.selectionPercent = selectionPercent;
		this.outputFile = outputFile;
	}
	
	public void run() {
		try {
			relPathsTable = new RelPathsTable();
			readPaths();
			relPathsTable.chooseBestRelPaths(selectionPercent);
			relPathsTable.outputRelPathsTable(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readPaths() throws IOException {
		for (RelAndFile relAndFile : input) {
			BufferedReader reader = new BufferedReader(new FileReader(relAndFile.file));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] lineParts = line.split("\t");
				POSRelPath path = new POSRelPath(lineParts[0]);
				int count = Integer.parseInt(lineParts[1]);
				relPathsTable.addRelPath(path, relAndFile.rel, count);
			}
			reader.close();
		}
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
