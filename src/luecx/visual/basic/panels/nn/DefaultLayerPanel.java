package luecx.visual.basic.panels.nn;

import luecx.ai.neuralnetwork.layers.Layer;
import luecx.visual.basic.framework.Panel;
import luecx.visual.basic.panels.arrays.Array3DPanel;

import javax.swing.*;
import java.awt.*;

public class DefaultLayerPanel extends Panel {

    private Layer layer;
    private Array3DPanel output_panel;

    public DefaultLayerPanel(Layer layer) {
        this.layer = layer;
        this.setLayout(new BorderLayout());
        this.output_panel = new Array3DPanel(layer.getOutput_values());
        this.output_panel.setBorder(BorderFactory.createTitledBorder("output"));
        this.add(output_panel);
    }

    @Override
    public void drawContent(Graphics2D graphics2D) {

    }

    @Override
    public void update() {

    }
}
