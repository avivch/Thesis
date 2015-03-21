package relations;

import java.util.Objects;

public class RelTriplet {
	public String concept;
	public String relation;
	public String feature;
	
	public RelTriplet(String concept, String relation, String feature) {
		this.concept = concept;
		this.relation = relation;
		this.feature = feature;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(concept, relation, feature);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RelTriplet))
			return false;
		RelTriplet other = (RelTriplet) obj;
		return ((this.concept.intern() == other.concept.intern()) &&
				(this.relation.intern() == other.relation.intern()) &&
				(this.feature.intern() == other.feature.intern()));
	}
	
	@Override
	public String toString() {
		return "(" + concept + ", " + relation + ", " + feature + ")";
	}
}
