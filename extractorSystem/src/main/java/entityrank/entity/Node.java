package entityrank.entity;

public abstract class Node {

    private double priorWeight = 0;
    private double currentWeight = 0;

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public double getPriorWeight() {
        return priorWeight;
    }

    public void setPriorWeight(double priorWeight) {
        this.priorWeight = priorWeight;
    }

}
