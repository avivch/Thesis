package relations;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class RelTable {
	private HashMap<RelTriplet, Double> triplets;
	private HashMap<String, Integer> words;
	private int wordsCount;
	
	public RelTable() {
		triplets = new HashMap<RelTriplet, Double>();
		words = new HashMap<String, Integer>();
		wordsCount = 0;
	}
	
	public void addRelation(String concept, String relation, String feature) {
		RelTriplet triplet = new RelTriplet(concept, relation, feature);
		Double tripletCount = triplets.get(triplet);
		triplets.put(triplet, tripletCount != null ? tripletCount + 1 : 1);
		
		Integer wordCount = words.get(concept);
		words.put(concept, wordCount != null ? wordCount + 1 : 1);
		
		wordCount = words.get(feature);
		words.put(feature, wordCount != null ? wordCount + 1 : 1);
		
		wordsCount += 1;
	}
	
	public void calcLikelihoodRatios() {
		for (Entry<RelTriplet, Double> entry : triplets.entrySet()) {
			RelTriplet triplet = entry.getKey();
			double c1 = words.get(triplet.concept);
			double c2 = words.get(triplet.feature);
			double c12 = entry.getValue();
			double p = c2 / wordsCount;
			double p1 = c12 / c1;
			double p2 = (c2 - c12) / (wordsCount - c1);
			double b1 = binomial(c12, c1, p);
			double b2 = binomial(c2 - c12, wordsCount - c1, p);
			double b3 = binomial(c12, c1, p1);
			double b4 = binomial(c2 - c12, wordsCount - c1, p2);
			if ((b1 == 0) || (b2 == 0) || (b3 == 0) || (b4 == 0))
				triplets.put(triplet, 0.0);
			else {
				double logLikelihood = Math.log(b1) + Math.log(b2) - Math.log(b3) - Math.log(b4);
				triplets.put(triplet, - 2 * logLikelihood);
			}
		}
	}
	
	private double binomial(double k, double n, double x) {
		return Math.pow(x, k) * Math.pow(1 - x, n - k);
	}
	
	public void outputRelTable(String outputFile) throws IOException {
		PrintWriter writer = new PrintWriter(outputFile);
		for (Entry<RelTriplet, Double> entry : getSortedRelations()) {
			RelTriplet triplet = entry.getKey();
			writer.println(triplet.concept + "\t" + triplet.relation + "\t" + triplet.feature + "\t" + entry.getValue());
		}
		writer.close();
	}
	
	private SortedSet<Entry<RelTriplet, Double>> getSortedRelations() {
		SortedSet<Entry<RelTriplet, Double>> sortedRelations = new TreeSet<Entry<RelTriplet, Double>>(new RelationComparator());
		sortedRelations.addAll(triplets.entrySet());
		return sortedRelations;
	}
	
	private static class RelationComparator implements Comparator<Entry<RelTriplet, Double>> {
		@Override
		public int compare(Entry<RelTriplet, Double> o1, Entry<RelTriplet, Double> o2) {
			double res = o2.getValue() - o1.getValue();
			return (res < 0) ? -1 : 1;
		}
	}
}
