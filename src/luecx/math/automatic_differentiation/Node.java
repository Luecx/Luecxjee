package luecx.math.automatic_differentiation;

public abstract class Node {

    protected double output;
    protected double gradient;

    protected abstract double evaluate();
    protected abstract void eval_back(double seed);

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public double getGradient() {
        return gradient;
    }

    public void setGradient(double gradient) {
        this.gradient = gradient;
    }
}
