package relations;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import reader.POSRelPath;

public class RelPathsTable {
	private HashMap<POSRelPath, HashMap<String, Integer>> paths;
	
	public RelPathsTable() {
		paths = new HashMap<POSRelPath, HashMap<String, Integer>>();
	}
	
	public void addPath(POSRelPath path, String relation) {
		HashMap<String, Integer> pathMap = paths.get(path);
		if (pathMap != null) {
			Integer count = pathMap.get(relation);
			if (count != null)
				pathMap.put(relation, count + 1);
			else
				pathMap.put(relation, 1);
		}
		else {
			pathMap = new HashMap<String, Integer>();
			pathMap.put(relation, 1);
			paths.put(path, pathMap);
		}
	}
	
	public void outputAll(String outputFile) throws IOException {
		TreeSet<PathRelationAndCount> sorted = new TreeSet<PathRelationAndCount>(new PathsComparator());
		for (Entry<POSRelPath, HashMap<String, Integer>> entry1 : paths.entrySet()) {
			for (Entry<String, Integer> entry2 : entry1.getValue().entrySet())
				sorted.add(new PathRelationAndCount(entry1.getKey(), entry2.getKey(), entry2.getValue()));
		}
		
		PrintWriter writer = new PrintWriter(outputFile);
		for (PathRelationAndCount path : sorted)
			writer.println(path.path + "\t" + path.relation + "\t" + path.count);
		writer.close();
	}
	
	public void outputBest(String outputFile, double selectionPercent) throws IOException {
		TreeSet<PathRelationAndCount> sorted = new TreeSet<PathRelationAndCount>(new PathsComparator());
		int countAll = 0;
		for (Entry<POSRelPath, HashMap<String, Integer>> entry1 : paths.entrySet()) {
			String bestRel = null;
			int bestCount = 0;
			for (Entry<String, Integer> entry2 : entry1.getValue().entrySet()) {
				if ((bestRel == null) || (entry2.getValue() > bestCount)) {
					bestRel = entry2.getKey();
					bestCount = entry2.getValue();
				}
			}
			sorted.add(new PathRelationAndCount(entry1.getKey(), bestRel, bestCount));
			countAll += bestCount;
		}
		
		PrintWriter writer = new PrintWriter(outputFile);
		int pathsCountNo = (int)Math.ceil(selectionPercent / 100 * countAll);
		int pathsChosen = 0;
		int lastCount = 0;
		for (PathRelationAndCount path : sorted) {
			if ((pathsChosen < pathsCountNo) || (path.count == lastCount)) {
				writer.println(path.path + "\t" + path.relation);
				pathsChosen += path.count;
				lastCount = path.count;
			}
			else
				break;
		}
		writer.close();
	}
	
	private static class PathRelationAndCount {
		public POSRelPath path;
		public String relation;
		public int count;
		
		public PathRelationAndCount(POSRelPath path, String relation, int count) {
			this.path = path;
			this.relation = relation;
			this.count = count;
		}
	}
	
	private static class PathsComparator implements Comparator<PathRelationAndCount> {
		@Override
		public int compare(PathRelationAndCount o1, PathRelationAndCount o2) {
			int res = o2.count - o1.count;
			return (res < 0) ? -1 : 1;
		}
	}
}
