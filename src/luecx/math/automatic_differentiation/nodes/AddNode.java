package luecx.math.automatic_differentiation.nodes;

import luecx.math.automatic_differentiation.Node;
import luecx.math.automatic_differentiation.Node2D;

public class AddNode extends Node2D {
    public AddNode(Node childA, Node childB) {
        super(childA, childB);
    }

    @Override
    protected double evaluate_func(double in1, double in2) {
        childA.setGradient(1);
        childB.setGradient(1);
        return in1 + in2;
    }
}
