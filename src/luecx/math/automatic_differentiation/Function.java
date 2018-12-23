package luecx.math.automatic_differentiation;

import luecx.math.values.tensor.Vector;

import java.util.*;


public class Function {

    protected ValueNode[] parameters;
    protected Node outputNode;

    public Function( Node outputNode, ValueNode... parameters) {
        this.parameters = parameters;
        this.outputNode = outputNode;
    }

    public Function(Function first, ValueNode v, Function wrapper){
        Set<ValueNode> set = new HashSet<>();
        for(ValueNode v1:first.parameters){
            set.add(v1);
        } for(ValueNode v2:wrapper.parameters){
            set.add(v2);
        }
        ValueNode[] ar = new ValueNode[set.size()];
        v.setChild(first.getOutputNode());
        this.parameters = set.toArray(ar);
        this.outputNode = wrapper.outputNode;
    }

    public double calculate(){
        return outputNode.evaluate();
    }

    public Function copy(){
        ArrayList<ValueNode> par = new ArrayList<>();
        Node output = copy_rec(outputNode, par);
        return new Function(outputNode, par.toArray(new ValueNode[0]));
    }

    public ArrayList<ArrayList<Node>> nodeList_with_depth(){
        ArrayList<ArrayList<Node>> par = new ArrayList<>();
        nodeList_with_depth_rec(outputNode, par, 0);
        return par;
    }

    private void nodeList_with_depth_rec(Node parent, ArrayList<ArrayList<Node>> nodes, int depth){

        if(nodes.size() <= depth){
            nodes.add(new ArrayList<>());
        }

        if(parent instanceof Node2D){
            nodeList_with_depth_rec(((Node2D) parent).childA, nodes, depth + 1);
            nodes.get(parent instanceof ValueNode ? depth-1: depth).add(parent);
            nodeList_with_depth_rec(((Node2D) parent).childB, nodes, depth + 1);
        }if(parent instanceof Node1D){
            nodes.get(parent instanceof ValueNode ? depth-1: depth).add(parent);
            nodeList_with_depth_rec(((Node1D) parent).child, nodes, depth + 1);
        }if(parent instanceof ValueNode){
            nodes.get(parent instanceof ValueNode ? depth-1: depth).add(parent);
            if(((ValueNode) parent).getChild() != null){
                nodeList_with_depth_rec(((ValueNode) parent).getChild(), nodes, depth + 1);
            }
        }
    }

    public ArrayList<Node> nodeList(){
        ArrayList<Node> par = new ArrayList<>();
        nodeList_rec(outputNode, par);
        return par;
    }

    private void nodeList_rec(Node parent, ArrayList<Node> nodes){
        nodes.add(parent);
        if(parent instanceof Node2D){
            nodeList_rec(((Node2D) parent).childA, nodes);
            nodeList_rec(((Node2D) parent).childB, nodes);
        }if(parent instanceof Node1D){
            nodeList_rec(((Node1D) parent).child, nodes);
        }if(parent instanceof ValueNode){
            if(((ValueNode) parent).getChild() != null){
                nodeList_rec(((ValueNode) parent).getChild(), nodes);
            }
        }
    }

    private Node copy_rec(Node parent, ArrayList<ValueNode> parameters){
        try {
            Node k = parent.getClass().newInstance();
            if(k instanceof Node2D){
                ((Node2D) k).childA = copy_rec(((Node2D)parent).childA, parameters);
                ((Node2D) k).childB = copy_rec(((Node2D)parent).childB, parameters);
            }if(k instanceof Node1D){
                ((Node1D) k).child = copy_rec(((Node1D)parent).child, parameters);
            }if(k instanceof ValueNode){
                ((ValueNode) k).setValue(((ValueNode)parent).getValue());
                if(((ValueNode) k).getChild() != null) {
                    ((ValueNode) k).setChild(copy_rec(((ValueNode)parent).getChild(), parameters));
                }else{
                    parameters.add((ValueNode)k);
                }
            }
            return k;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.exit(-1);
        return null;
    }

    public void setInputs(double... values){
        for(int i = 0; i < Math.min(values.length, parameters.length); i++){
            parameters[i].setValue(values[i]);
        }
    }

    public void calculate_gradients(){
        this.outputNode.eval_back(1);
    }

    public ValueNode[] getParameters() {
        return parameters;
    }

    public Node getOutputNode() {
        return outputNode;
    }
}
