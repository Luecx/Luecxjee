/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package luecx.visual.basic.panels.nn;

import luecx.ai.neuralnetwork.layers.ConvLayer;
import luecx.ai.neuralnetwork.layers.DenseLayer;
import luecx.ai.neuralnetwork.layers.Layer;
import luecx.visual.basic.framework.Panel;
import luecx.visual.basic.panels.arrays.Array3DPanel;

import javax.swing.*;
import java.awt.*;

public class LayerPanel extends Panel {

    protected Layer layer;

    public LayerPanel(Layer layer) {
        super(1000);
        this.layer = layer;
        initComponents();
    }

    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new Array3DPanel(layer.getInput_values());
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Input"));

        if(layer instanceof ConvLayer){
            jPanel2 = new ConvolutionPanel((ConvLayer) layer);
        } else if(layer instanceof DenseLayer){
            jPanel2 = new DensePanel((DenseLayer) layer);
        }else{
            jPanel2 = new DefaultLayerPanel(layer);
        }

        setLayout(new java.awt.BorderLayout());


        jSplitPane1.setLeftComponent(jPanel1);
        jSplitPane1.setRightComponent(jPanel2);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }


    private Array3DPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;

    public Layer getLayer() {
        return layer;
    }

    public Array3DPanel getjPanel1() {
        return jPanel1;
    }

    public JPanel getjPanel2() {
        return jPanel2;
    }

    public JSplitPane getjSplitPane1() {
        return jSplitPane1;
    }

    @Override
    public void drawContent(Graphics2D graphics2D) {

    }

    @Override
    public void update() {

    }
}
