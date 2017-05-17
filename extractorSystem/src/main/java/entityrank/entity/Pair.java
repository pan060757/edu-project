package entityrank.entity;


public class Pair implements Comparable<Pair> {
	
	private String entity;
	private double rank;
		
	public Pair(String entity, double rank) {
		super();
		this.entity = entity;
		this.rank = rank;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public double getRank() {
		return rank;
	}

	public void setRank(double rank) {
		this.rank = rank;
	}

	public int compareTo(Pair arg0) {
		if (rank>arg0.rank)
			return 1;
		else if (rank<arg0.rank)
			return -1;
		else
			return 0;
	}
	
}