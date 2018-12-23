package luecx.math.automatic_differentiation.nodes;

import luecx.math.automatic_differentiation.Node;
import luecx.math.automatic_differentiation.Node1D;

public class ExpNode extends Node1D {
    public ExpNode(Node child) {
        super(child);
    }

    @Override
    protected double evaluate_func(double in) {
        child.setGradient(Math.exp(in));
        return child.getGradient();
    }
}
