package entityrank.entity;

public class Edge {
	
	private int fromId;
	private int toId;
	private boolean isEntityToSentenceEdge;
	private double weight;
	
	public int getFromId() {
		return fromId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public int getToId() {
		return toId;
	}

	public void setToId(int toId) {
		this.toId = toId;
	}

	public boolean isEntityToSentenceEdge() {
		return isEntityToSentenceEdge;
	}

	public void setEntityToSentenceEdge(boolean isEntityToSentenceEdge) {
		this.isEntityToSentenceEdge = isEntityToSentenceEdge;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Edge(int fromId, int toId, boolean isEntityToSentenceEdge,
			double weight) {
		super();
		this.fromId = fromId;
		this.toId = toId;
		this.isEntityToSentenceEdge = isEntityToSentenceEdge;
		this.weight = weight;
	}
	
}
