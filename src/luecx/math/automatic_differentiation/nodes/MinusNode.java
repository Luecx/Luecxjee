package luecx.math.automatic_differentiation.nodes;

import luecx.math.automatic_differentiation.Node;
import luecx.math.automatic_differentiation.Node1D;

public class MinusNode extends Node1D {
    public MinusNode(Node child) {
        super(child);
    }

    @Override
    protected double evaluate_func(double in1) {
        child.setGradient(-1);
        return -in1;
    }
}
