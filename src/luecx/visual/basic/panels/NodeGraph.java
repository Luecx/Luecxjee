package luecx.visual.basic.panels;

import luecx.math.automatic_differentiation.*;
import luecx.math.automatic_differentiation.nodes.*;
import luecx.math.values.tensor.Vector;
import luecx.visual.basic.framework.Panel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class NodeGraph extends Panel {

    private Function function;
    private HashMap<Node, Vector> positions = new HashMap<>();

    public NodeGraph(Function f) {
        super(100);
        this.function = f;

        ArrayList<ArrayList<Node>> t = f.nodeList_with_depth();

        for(ArrayList<Node> n: t){
            for(Node k: n){
                positions.put(k, new Vector(
                        (t.indexOf(n) + 1) / (double)(t.size() + 1),
                        (n.indexOf(k) + 1) / (double)(n.size() + 1)));
            }
        }

    }

    @Override
    public void update() {

    }

    private void drawNode(Node n, Graphics2D g2d){

        int x = (int)(positions.get(n).getValue(0) * this.getWidth());
        int y = (int)(positions.get(n).getValue(1) * this.getHeight());

        g2d.setColor(Color.green);
        g2d.fillOval(x - 25,y - 25,50,50);
        g2d.setColor(Color.black);
        g2d.drawOval(x - 25,y - 25,50,50);
        g2d.setFont(new Font("Arial", 1, 24));


        if(n instanceof AddNode){
            centerString(g2d,"+", x,y, g2d.getFont());
        }else if(n instanceof MinusNode){
            centerString(g2d,"-", x,y, g2d.getFont());
        }else if(n instanceof ExpNode){
            centerString(g2d,"exp", x,y, g2d.getFont());
        }else if(n instanceof MulNode){
            centerString(g2d,"*", x,y, g2d.getFont());
        }else if(n instanceof PowerNode){
            centerString(g2d,"^"+((PowerNode) n).getPower(), x,y, g2d.getFont());
        }else if(n instanceof DivNode){
            centerString(g2d,"/", x,y, g2d.getFont());
        }else if(n instanceof ValueNode){
            centerString(g2d,((ValueNode) n).getValue()+"", x,y, g2d.getFont());
        }

        g2d.setFont(new Font("Arial", 1, 14));

        centerString(g2d, n.getOutput()+"",x - 25,y - 25,g2d.getFont());
    }

    @Override
    public void drawContent(Graphics2D graphics2D) {
        for(Node n:positions.keySet()){
            int x = (int)(positions.get(n).getValue(0) * this.getWidth());
            int y = (int)(positions.get(n).getValue(1) * this.getHeight());

            if(n instanceof Node2D){
                graphics2D.drawLine(x,y,
                        (int)(positions.get(((Node2D)n).getChildA()).getValue(0) * this.getWidth()),
                        (int)(positions.get(((Node2D)n).getChildA()).getValue(1) * this.getHeight()));

                graphics2D.drawLine(x,y,
                        (int)(positions.get(((Node2D)n).getChildB()).getValue(0) * this.getWidth()),
                        (int)(positions.get(((Node2D)n).getChildB()).getValue(1) * this.getHeight()));
            }

            if(n instanceof Node1D){
                graphics2D.drawLine(x,y,
                        (int)(positions.get(((Node1D)n).getChild()).getValue(0) * this.getWidth()),
                        (int)(positions.get(((Node1D)n).getChild()).getValue(1) * this.getHeight()));
            }

            if(n instanceof ValueNode && ((ValueNode) n).getChild() != null){
                graphics2D.drawLine(x,y,
                        (int)(positions.get(((ValueNode)n).getChild()).getValue(0) * this.getWidth()),
                        (int)(positions.get(((ValueNode)n).getChild()).getValue(1) * this.getHeight()));
            }
        }
        for(Node n:positions.keySet()){
            drawNode(n, graphics2D);
        }
    }
}
