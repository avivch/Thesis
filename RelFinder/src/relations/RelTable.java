package relations;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class RelTable {
	private HashMap<String, HashMap<String, HashMap<String, Double>>> triplets;
	private HashMap<String, Integer> words;
	private int wordsCount;
	
	public RelTable() {
		triplets = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
		words = new HashMap<String, Integer>();
		wordsCount = 0;
	}
	
	public void addRelation(String concept, String relation, String feature) {
		updateTripletCount(concept, relation, feature);
		
		Integer wordCount = words.get(concept);
		words.put(concept, wordCount != null ? wordCount + 1 : 1);
		
		wordCount = words.get(feature);
		words.put(feature, wordCount != null ? wordCount + 1 : 1);
		
		wordsCount += 2;
	}
	
	private void updateTripletCount(String concept, String relation, String feature) {
		HashMap<String, HashMap<String, Double>> conceptMap = null;
		HashMap<String, Double> featureMap = null;
		Double count = null;
		
		conceptMap = triplets.get(concept);
		if (conceptMap != null) {
			featureMap = conceptMap.get(feature);
			if (featureMap != null) {
				count = featureMap.get(relation);
				if (count != null)
					featureMap.put(relation, count + 1);
				else
					featureMap.put(relation, 1.0);
			}
			else {
				featureMap = new HashMap<String, Double>();
				featureMap.put(relation, 1.0);
				conceptMap.put(feature, featureMap);
			}
		}
		else {
			conceptMap = new HashMap<String, HashMap<String, Double>>();
			featureMap = new HashMap<String, Double>();
			featureMap.put(relation, 1.0);
			conceptMap.put(feature, featureMap);
			triplets.put(concept, conceptMap);
		}
	}
	
	public void calcLikelihoodRatios() {
		for (Entry<String, HashMap<String, HashMap<String, Double>>> entry1 : triplets.entrySet()) {
			String concept = entry1.getKey();
			for (Entry<String, HashMap<String, Double>> entry2 : entry1.getValue().entrySet()) {
				String feature = entry2.getKey();
				String relation = unifyRelations(entry2.getValue());
				calcLikelihoodRatio(concept, relation, feature);
			}
		}
	}
	
	private String unifyRelations(HashMap<String, Double> relations) {
		if (relations.size() == 1)
			return relations.keySet().iterator().next();
		String relation = null;
		double relationCount = 0;
		double countAll = 0;
		for (Entry<String, Double> entry : relations.entrySet()) {
			countAll += entry.getValue();
			double count = entry.getValue();
			if (count > relationCount) {
				relationCount = count;
				relation = entry.getKey();
			}
		}
		relations.clear();
		relations.put(relation, countAll);
		return relation;
	}
	
	private void calcLikelihoodRatio(String concept, String relation, String feature) {
		double c1 = words.get(concept);
		double c2 = words.get(feature);
		double c12 = triplets.get(concept).get(feature).get(relation);
		double p = c2 / wordsCount;
		double p1 = c12 / c1;
		double p2 = (c2 - c12) / (wordsCount - c1);
		double b1 = binomial(c12, c1, p);
		double b2 = binomial(c2 - c12, wordsCount - c1, p);
		double b3 = binomial(c12, c1, p1);
		double b4 = binomial(c2 - c12, wordsCount - c1, p2);
		if ((b1 == 0) || (b2 == 0) || (b3 == 0) || (b4 == 0)) {
			triplets.get(concept).get(feature).put(relation, 0.0);
		}
		else {
			double logLikelihood = Math.log(b1) + Math.log(b2) - Math.log(b3) - Math.log(b4);
			triplets.get(concept).get(feature).put(relation, - 2 * logLikelihood);
		}
	}
	
	private double binomial(double k, double n, double x) {
		return Math.pow(x, k) * Math.pow(1 - x, n - k);
	}
	
	public void output(String outputFile) throws IOException {
		PrintWriter writer = new PrintWriter(outputFile);
		for (TripletAndGrade tripletAndGrade : getSortedRelations())
			writer.println(tripletAndGrade.triplet.concept + "\t" + tripletAndGrade.triplet.relation + "\t" + tripletAndGrade.triplet.feature + "\t" + tripletAndGrade.grade);
		writer.close();
	}
	
	private SortedSet<TripletAndGrade> getSortedRelations() {
		TreeSet<TripletAndGrade> sortedRelations = new TreeSet<TripletAndGrade>(new RelationComparator());
		for (Entry<String, HashMap<String, HashMap<String, Double>>> entry1 : triplets.entrySet()) {
			String concept = entry1.getKey();
			for (Entry<String, HashMap<String, Double>> entry2 : entry1.getValue().entrySet()) {
				String feature = entry2.getKey();
				for (Entry<String, Double> entry3 : entry2.getValue().entrySet()) {
					String relation = entry3.getKey();
					Double grade = entry3.getValue();
					sortedRelations.add(new TripletAndGrade(new RelTriplet(concept, relation, feature), grade));
				}
			}
		}
		return sortedRelations;
	}
	
	private static class TripletAndGrade {
		public RelTriplet triplet;
		public double grade;
		
		public TripletAndGrade(RelTriplet triplet, double grade) {
			this.triplet = triplet;
			this.grade = grade;
		}
	}
	
	private static class RelationComparator implements Comparator<TripletAndGrade> {
		@Override
		public int compare(TripletAndGrade o1, TripletAndGrade o2) {
			double res = o2.grade - o1.grade;
			return (res < 0) ? -1 : 1;
		}
	}
}
