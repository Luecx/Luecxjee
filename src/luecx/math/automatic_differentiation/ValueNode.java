package luecx.math.automatic_differentiation;

public class ValueNode extends Node {

    private double value;
    private Node child;

    public ValueNode(double value) {
        this.value = value;
    }

    public ValueNode() {
    }

    public Node getChild() {
        return child;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    protected double evaluate() {
        this.child.setGradient(1);
        return child == null ? value: child.evaluate();
    }

    @Override
    protected void eval_back(double seed) {
        this.gradient = seed;
        this.child.eval_back(child.getGradient() * this.gradient);
    }

}
