package luecx.math.automatic_differentiation.nodes;

import luecx.math.automatic_differentiation.Node;
import luecx.math.automatic_differentiation.Node1D;

public class PowerNode extends Node1D {

    private double power = 0;

    public PowerNode(Node child, double power) {
        super(child);
        this.power = power;
    }

    @Override
    protected double evaluate_func(double in) {
        child.setGradient(power * Math.pow(in, power-1));
        return Math.pow(in, power);
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }
}
