package luecx.math.automatic_differentiation.nodes;

import luecx.math.automatic_differentiation.Node;
import luecx.math.automatic_differentiation.Node2D;

public class DivNode extends Node2D {
    public DivNode(Node childA, Node childB) {
        super(childA, childB);
    }

    @Override
    protected double evaluate_func(double in1, double in2) {
        childA.setGradient(1d/in2);
        childB.setGradient(-in1 * 1d / (in2 * in2));
        return in1/in2;

    }
}
