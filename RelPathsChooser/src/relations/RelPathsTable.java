package relations;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import reader.POSRelPath;

public class RelPathsTable {
	private HashMap<POSRelPath, RelationAndCount> paths;
	private int countAll;
	
	public RelPathsTable() {
		paths = new HashMap<POSRelPath, RelationAndCount>();
		countAll = 0;
	}
	
	public void addRelPath(POSRelPath relPath, String relation, int count) {
		RelationAndCount relationAndCount = paths.get(relPath);
		if (relationAndCount == null) {
			countAll += count;
			paths.put(relPath, new RelationAndCount(relation, count));
		}
		else if (count > relationAndCount.count) {
			countAll += count - relationAndCount.count;
			relationAndCount.relation = relation;
			relationAndCount.count = count;
		}
	}
	
	public void chooseBestRelPaths(double selectionPercent) {
		SortedSet<Entry<POSRelPath, RelationAndCount>> sortedPaths = new TreeSet<Entry<POSRelPath, RelationAndCount>>(new PathsComparator());
		sortedPaths.addAll(paths.entrySet());
		HashMap<POSRelPath, RelationAndCount> bestPaths = new HashMap<POSRelPath, RelationAndCount>();
		int pathsCountNo = (int)Math.ceil(selectionPercent / 100 * countAll);
		int pathsChosen = 0;
		int lastCount = 0;
		for (Entry<POSRelPath, RelationAndCount> entry : sortedPaths) {
			int count = entry.getValue().count;
			if ((pathsChosen < pathsCountNo) || (count == lastCount)) {
				bestPaths.put(entry.getKey(), entry.getValue());
				pathsChosen += count;
				lastCount = count;
			}
			else
				break;
		}
		paths = bestPaths;
	}
	
	public void outputRelPathsTable(String outputFile) throws IOException {
		PrintWriter writer = new PrintWriter(outputFile);
		for (Entry<POSRelPath, RelationAndCount> entry : paths.entrySet())
			writer.println(entry.getKey().toString() + "\t" + entry.getValue().relation);
		writer.close();
	}
	
	private static class RelationAndCount {
		public String relation;
		public int count;
		
		public RelationAndCount(String relation, int count) {
			this.relation = relation;
			this.count = count;
		}
	}
	
	private static class PathsComparator implements Comparator<Entry<POSRelPath, RelationAndCount>> {
		@Override
		public int compare(Entry<POSRelPath, RelationAndCount> o1, Entry<POSRelPath, RelationAndCount> o2) {
			int res = o2.getValue().count - o1.getValue().count;
			return (res < 0) ? -1 : 1;
		}
	}
}
