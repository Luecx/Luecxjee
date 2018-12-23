package luecx.math.automatic_differentiation;

import luecx.math.automatic_differentiation.nodes.*;
import luecx.visual.basic.framework.Frame;
import luecx.visual.basic.panels.NodeGraph;

public abstract class Node2D extends Node {


    protected Node childA;
    protected Node childB;

    public Node2D(Node childA, Node childB) {
        this.childA = childA;
        this.childB = childB;
    }

    public double evaluate(){
        this.output = this.evaluate_func(childA.evaluate(), childB.evaluate());
        return this.output;
    }

    @Override
    protected void eval_back(double d) {
        this.gradient = d; //seed
        this.childA.eval_back(childA.getGradient() * this.gradient);
        this.childB.eval_back(childB.getGradient() * this.gradient);
    }

    public Node getChildA() {
        return childA;
    }

    public Node getChildB() {
        return childB;
    }

    protected abstract double evaluate_func(double in1, double in2);


    public static void main(String[] args) {
        ValueNode w1 = new ValueNode(1);
        ValueNode x1 = new ValueNode(3);
        ValueNode w2 = new ValueNode(-2);
        ValueNode x2 = new ValueNode(2);
        ValueNode w3 = new ValueNode(2);
        ValueNode v1 = new ValueNode(1);
        ValueNode v2 = new ValueNode(1);

        MulNode m1 = new MulNode(w1,x1);
        MulNode m2 = new MulNode(w2,x2);
        AddNode a1 = new AddNode(m1,m2);
        AddNode a2 = new AddNode(a1,w3);

        MinusNode minus1 = new MinusNode(a2);
        ExpNode exp1 = new ExpNode(minus1);
        AddNode a3 = new AddNode(exp1, v1);
        DivNode div = new DivNode(v2,a3);



        ValueNode target = new ValueNode(0.5);
        MinusNode minusNode = new MinusNode(target);

        AddNode addNode = new AddNode(div, minusNode);
        PowerNode square = new PowerNode(addNode, 2);

////        System.out.println(square.evaluate());
////        System.out.println(square.gradient);
//
//        for(int i = 0; i < 100; i++){
//            System.out.println(square.evaluate());
//            System.out.println(div.getOutput());
//            square.eval_back(1);
//            w1.setValue(w1.getValue() - w1.getGradient());
//            w2.setValue(w2.getValue() - w2.getGradient());
//            w3.setValue(w3.getValue() - w3.getGradient());
//        }

        Frame f = new Frame(new NodeGraph(new Function(square)));


    }
}
