package luecx.visual.basic.panels.nn;

import luecx.ai.neuralnetwork.layers.DenseLayer;
import luecx.visual.basic.framework.Panel;
import luecx.visual.basic.panels.arrays.Array2DPanel;

import javax.swing.*;
import java.awt.*;

public class DensePanel extends Panel {

    private final DenseLayer layer;
    private Array2DPanel panel;

    public DensePanel(DenseLayer layer) {
        super();
        this.layer = layer;
        this.setLayout(new BorderLayout());
        this.panel = new Array2DPanel(layer.getWeights());
        this.panel.setBorder(BorderFactory.createTitledBorder("weights"));
        this.add(panel);
    }

    @Override
    public void drawContent(Graphics2D graphics2D) {

    }

    @Override
    public void update() {

    }
}
