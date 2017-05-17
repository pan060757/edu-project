package timeline;

public class JSD {

	private double[] topicDistriA;
	private double[] topicDistriB;
	
	public JSD(double[] topicDistriA, double[] topicDistriB) {
		this.topicDistriA=topicDistriA;
		this.topicDistriB=topicDistriB;
	}
	
	public double divergence() {
		double[] avgDist=avgDist();
		KLD firstKld=new KLD(topicDistriA, avgDist);
		KLD secondKld=new KLD(topicDistriB, avgDist);
		return (firstKld.divergence(true)+secondKld.divergence(true))/2;
	}
	
	private double[] avgDist() {
		double[] avgDist=new double[topicDistriA.length];
		for (int i=0;i<avgDist.length;i++) {
			avgDist[i]=(topicDistriA[i]+topicDistriB[i])/2;
		}
		return avgDist;		
	}
	
}
