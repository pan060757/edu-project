package timeline;

public class KLD {
	
	private double[] topicDistriA;
	private double[] topicDistriB;
	
	public KLD(double[] topicDistriA, double[] topicDistriB) {
		this.topicDistriA=topicDistriA;
		this.topicDistriB=topicDistriB;
	}
	
	public double divergence(boolean isAToB) {
		if (isAToB) {	//p||q
			double sum=0;
			for (int i=0;i<topicDistriA.length;i++) {
				sum+=topicDistriA[i]*Math.log(topicDistriA[i]/topicDistriB[i]);
			}
			return sum;
		} else {
			double sum=0;
			for (int i=0;i<topicDistriB.length;i++) {
				sum+=topicDistriB[i]*Math.log(topicDistriB[i]/topicDistriA[i]);
			}
			return sum;
		}
	}
	
	private double normalizeFactor(boolean isAToB) {
		double N=5900;
		if (isAToB) {	//p||q
			double maxProb=0;
			for (int i=0;i<topicDistriA.length;i++)
				if (topicDistriA[i]>maxProb)
					maxProb=topicDistriA[i];
			return maxProb*Math.log(N*maxProb);
		} else {
			double maxProb=0;
			for (int i=0;i<topicDistriB.length;i++)
				if (topicDistriB[i]>maxProb)
					maxProb=topicDistriB[i];
			return maxProb*Math.log(N*maxProb);
		}
	}
	
	public double normalizedDivergence(boolean isAToB) {
		return divergence(isAToB)/normalizeFactor(isAToB);
	}

}
