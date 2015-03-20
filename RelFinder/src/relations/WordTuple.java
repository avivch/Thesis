package relations;

public class WordTuple {
	public String governor;
	public String dependent;
	
	public WordTuple(String governor, String dependent) {
		this.governor = governor;
		this.dependent = dependent;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WordTuple))
			return false;
		WordTuple other = (WordTuple) obj;
		return ((this.governor.intern() == other.governor.intern()) && (this.dependent.intern() == other.dependent.intern()));
	}

	@Override
	public int hashCode() {
		return governor.hashCode() ^ dependent.hashCode();
	}
	
	@Override
	public String toString() {
		return "(" + governor + ", " + dependent + ")";
	}
}
