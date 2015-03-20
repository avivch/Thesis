package relations;

import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

public class RelTable {
	private HashMap<WordTuple, Double> tuples;
	private HashMap<String, Integer> words;
	private int wordsCount;
	
	public RelTable() {
		tuples = new HashMap<WordTuple, Double>();
		words = new HashMap<String, Integer>();
		wordsCount = 0;
	}
	
	public void addRelation(String governor, String dependent) {
		WordTuple tuple = new WordTuple(governor, dependent);
		Double doubleCount = tuples.get(tuple);
		tuples.put(tuple, doubleCount != null ? doubleCount + 1 : 1);
		
		Integer count = words.get(governor);
		words.put(governor, count != null ? count + 1 : 1);
		
		count = words.get(dependent);
		words.put(dependent, count != null ? count + 1 : 1);
		
		wordsCount += 1;
	}
	
	public void calcLikelihoodRatios() {
		for (Entry<WordTuple, Double> entry : tuples.entrySet()) {
			WordTuple tuple = entry.getKey();
			double c1 = words.get(tuple.governor);
			double c2 = words.get(tuple.dependent);
			double c12 = entry.getValue();
			double p = c2 / wordsCount;
			double p1 = c12 / c1;
			double p2 = (c2 - c12) / (wordsCount - c1);
			double b1 = binomial(c12, c1, p);
			double b2 = binomial(c2 - c12, wordsCount - c1, p);
			double b3 = binomial(c12, c1, p1);
			double b4 = binomial(c2 - c12, wordsCount - c1, p2);
			if ((b1 == 0) || (b2 == 0) || (b3 == 0) || (b4 == 0))
				tuples.put(tuple, 0.0);
			else {
				double logLikelihood = Math.log(b1) + Math.log(b2) - Math.log(b3) - Math.log(b4);
				tuples.put(tuple, - 2 * logLikelihood);
			}
		}
	}
	
	private double binomial(double k, double n, double x) {
		return Math.pow(x, k) * Math.pow(1 - x, n - k);
	}
	
	public void outputRelTable(String outputFile) throws Exception {
		PrintWriter writer = new PrintWriter(outputFile);
		for (Entry<WordTuple, Double> entry : getSortedRelations()) {
			WordTuple tuple = entry.getKey();
			writer.println(tuple.governor + "\t" + tuple.dependent + "\t" + entry.getValue());
		}
		writer.close();
	}
	
	private SortedSet<Entry<WordTuple, Double>> getSortedRelations() {
		SortedSet<Entry<WordTuple, Double>> sortedRelations = new TreeSet<Entry<WordTuple, Double>>(new RelationComparator());
		sortedRelations.addAll(tuples.entrySet());
		return sortedRelations;
	}
	
	private static class RelationComparator implements Comparator<Entry<WordTuple, Double>> {
		@Override
		public int compare(Entry<WordTuple, Double> o1, Entry<WordTuple, Double> o2) {
			double res = o2.getValue() - o1.getValue();
			return (res < 0) ? -1 : 1;
		}
	}
	
	public void close() {
	}
}
