package relations;

import java.io.*;
import java.sql.*;

public class RelTableNew {
	private Connection dbConn;
	private int wordsCount;
	
	public RelTableNew() throws SQLException {
		dbConn = DriverManager.getConnection("jdbc:sqlite:file::memory:?cache=shared");
		dbConn.setAutoCommit(false);
		
		String deleteTuples = "DROP TABLE IF EXISTS Tuples";
		dbUpdate(deleteTuples);
		
		String createTuples = "CREATE TABLE Tuples " +
				"(Governor	TEXT	NOT NULL, " +
				"Dependent	TEXT	NOT NULL, " +
				"Count		REAL	NOT NULL, " +
				"CONSTRAINT tupleKey PRIMARY KEY (Governor, Dependent))";
		dbUpdate(createTuples);
		
		String deleteWords = "DROP TABLE IF EXISTS Words";
		dbUpdate(deleteWords);
		
		String createWords = "CREATE TABLE Words " +
				"(Word	TEXT	PRIMARY KEY		NOT NULL, " +
				"Count	INT		NOT NULL)";
		dbUpdate(createWords);
		
		wordsCount = 0;
	}
	
	private void dbUpdate(String sql) throws SQLException {
		Statement stmt = dbConn.createStatement(); 
		stmt.executeUpdate(sql);
		stmt.close();
		dbConn.commit();
	}
	
	public void addRelation(String governor, String dependent) throws SQLException {
		increaseTupleCount(governor, dependent);
		increaseWordCount(governor);
		increaseWordCount(dependent);
		wordsCount += 1;
	}
	
	private void increaseTupleCount(String governor, String dependent) throws SQLException {
		double count = getTupleCount(governor, dependent);
		if (count == 0)
			addTuple(governor, dependent);
		else
			setTupleCount(governor, dependent, count + 1);
	}
	
	private double getTupleCount(String governor, String dependent) throws SQLException {
		Statement stmt = dbConn.createStatement();
		String getTuple = "SELECT * FROM Tuples WHERE " +
								"Governor='" + governor.replaceAll("'", "''") + "' AND " +
								"Dependent='" + dependent.replaceAll("'", "''") + "'";
		ResultSet rs = stmt.executeQuery(getTuple);
		double count = 0;
		if (rs.next())
			count = rs.getDouble("Count");
		rs.close();
		stmt.close();
		return count;
	}
	
	private void addTuple(String governor, String dependent) throws SQLException {
		String addTuple = "INSERT INTO Tuples (Governor, Dependent, Count) " +
				"VALUES ('" + governor.replaceAll("'", "''") + "', '" + dependent.replaceAll("'", "''") + "', 1.0)"; 
		dbUpdate(addTuple);
	}
	
	private void setTupleCount(String governor, String dependent, double count) throws SQLException {
		String setTuple = "UPDATE Tuples SET Count=" + count + " WHERE " +
							"Governor='" + governor.replaceAll("'", "''") + "' AND " +
							"Dependent='" + dependent.replaceAll("'", "''") + "'";
		dbUpdate(setTuple);
	}
	
	private void increaseWordCount(String word) throws SQLException {
		int count = getWordCount(word);
		if (count == 0)
			addWord(word);
		else
			setWordCount(word, count + 1);
	}
	
	private int getWordCount(String word) throws SQLException {
		Statement stmt = dbConn.createStatement();
		String getWord = "SELECT * FROM Words WHERE " +
								"Word='" + word.replaceAll("'", "''") + "'";
		ResultSet rs = stmt.executeQuery(getWord);
		int count = 0;
		if (rs.next())
			count = rs.getInt("Count");
		rs.close();
		stmt.close();
		return count;
	}
	
	private void addWord(String word) throws SQLException {
		String addWord = "INSERT INTO Words (Word, Count) " +
				"VALUES ('" + word.replaceAll("'", "''") + "', 1)"; 
		dbUpdate(addWord);
	}
	
	private void setWordCount(String word, int count) throws SQLException {
		String setTuple = "UPDATE Words SET Count=" + count + " WHERE " +
							"Word='" + word.replaceAll("'", "''") + "'";
		dbUpdate(setTuple);
	}
	
	public void calcLikelihoodRatios() throws SQLException {
		Statement stmt = dbConn.createStatement();
		String getTable = "SELECT * FROM Tuples";
		ResultSet rs = stmt.executeQuery(getTable);
		while (rs.next()) {
			String governor = rs.getString("Governor");
			String dependent = rs.getString("Dependent");
			double c1 = getWordCount(governor);
			double c2 = getWordCount(dependent);
			double c12 = rs.getDouble("Count");
			double p = c2 / wordsCount;
			double p1 = c12 / c1;
			double p2 = (c2 - c12) / (wordsCount - c1);
			double b1 = binomial(c12, c1, p);
			double b2 = binomial(c2 - c12, wordsCount - c1, p);
			double b3 = binomial(c12, c1, p1);
			double b4 = binomial(c2 - c12, wordsCount - c1, p2);
			if ((b1 == 0) || (b2 == 0) || (b3 == 0) || (b4 == 0))
				setTupleCount(governor, dependent, 0);
			else {
				double logLikelihood = Math.log(b1) + Math.log(b2) - Math.log(b3) - Math.log(b4);
				setTupleCount(governor, dependent, - 2 * logLikelihood);
			}
		}
		rs.close();
		stmt.close();
	}
	
	private double binomial(double k, double n, double x) {
		return Math.pow(x, k) * Math.pow(1 - x, n - k);
	}
	
	public void outputRelTable(String outputFile) throws Exception {
		PrintWriter writer = new PrintWriter(outputFile);
		Statement stmt = dbConn.createStatement();
		String getTable = "SELECT * FROM Tuples";
		ResultSet rs = stmt.executeQuery(getTable);
		while (rs.next())
			writer.println(rs.getString("Governor") + "\t" + rs.getString("Dependent") + "\t" + rs.getDouble("Count"));
		rs.close();
		stmt.close();
		writer.close();
	}
	
	public void close() throws SQLException {
		dbConn.close();
	}
}
