package luecx.visual.basic.panels.nn;

import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.layers.Layer;
import luecx.data.mnist.Mnist;
import luecx.visual.basic.framework.Frame;
import luecx.visual.basic.framework.Panel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class NetworkPanel extends Panel {

    private final JTabbedPane jTabbedPane1;
    private ArrayList<LayerPanel> panels = new ArrayList<>();
    private Network network;

    public NetworkPanel(Network network) {
        super(100);
        this.network = network;

        jTabbedPane1 = new javax.swing.JTabbedPane();

        setLayout(new java.awt.BorderLayout());

        Layer l = network.getInputLayer().getNext_layer();
        while(l != null){
            LayerPanel p = new LayerPanel(l);
            this.panels.add(p);
            jTabbedPane1.addTab(l.getClass().getSimpleName(), p);
            l = l.getNext_layer();
        }

        this.add(jTabbedPane1);
    }


    public static void main(String[] args) {
        Network k = Network.load_network("res/mnist_network_conv2.txt");
        new Frame(new NetworkPanel(k));
        int index = (int)(Math.random() * 100);
        System.out.println(index);
        k.calculate(Mnist.createTrainSet(index, index+1).getInput(0));
    }

    @Override
    public void drawContent(Graphics2D graphics2D) {

    }

    @Override
    public void update() {
        if(this.panels.size() == 0) return;
        if(this.panels.get(0).getjPanel1() != null){
            this.panels.get(0).getjPanel1().setArray(network.getInput());
        }
    }
}
