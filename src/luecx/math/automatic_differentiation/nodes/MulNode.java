package luecx.math.automatic_differentiation.nodes;

import luecx.math.automatic_differentiation.Node;
import luecx.math.automatic_differentiation.Node2D;

public class MulNode extends Node2D {
    public MulNode(Node childA, Node childB) {
        super(childA, childB);
    }

    @Override
    protected double evaluate_func(double in1, double in2) {
        childA.setGradient(in2);
        childB.setGradient(in1);
        return in1 * in2;
    }
}
