package luecx.visual.basic.panels.nn;

import luecx.ai.neuralnetwork.layers.ConvLayer;
import luecx.visual.basic.framework.Panel;
import luecx.visual.basic.panels.arrays.Array3DPanel;

import javax.swing.*;
import java.awt.*;

public class ConvolutionPanel extends Panel {

    private ConvLayer convLayer;
    private Array3DPanel[] panels;

    public ConvolutionPanel(ConvLayer convLayer) {
        super();
        this.convLayer = convLayer;
        this.setLayout(new GridLayout(convLayer.getOUTPUT_DEPTH(),1,10,10));
        this.panels = new Array3DPanel[convLayer.getOUTPUT_DEPTH()];
        for(int i = 0; i < convLayer.getOUTPUT_DEPTH(); i++){
            Array3DPanel p = new Array3DPanel(convLayer.getFilter(i), true);
            p.setBorder(BorderFactory.createTitledBorder("Filter " + i));
            this.panels[i] = p;
            this.add(p);
        }
    }

    @Override
    public void drawContent(Graphics2D graphics2D) {
        graphics2D.clearRect(0,0,10000,10000);
    }

    @Override
    public void update() {

    }
}
