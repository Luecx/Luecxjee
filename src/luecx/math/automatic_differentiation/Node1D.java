package luecx.math.automatic_differentiation;

public abstract class Node1D extends Node {


    protected Node child;

    public Node1D(Node child) {
        this.child = child;
    }

    public double evaluate(){
        this.output = this.evaluate_func(child.evaluate());
        return this.output;
    }

    @Override
    protected void eval_back(double d) {
        this.gradient = d;
        this.child.eval_back(child.getGradient() * this.gradient);
    }

    public Node getChild() {
        return child;
    }

    protected abstract double evaluate_func(double in);
}
