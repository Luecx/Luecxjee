package luecx.visual.basic.panels.arrays;

import luecx.ai.neuralnetwork.layers.Layer;
import luecx.ai.neuralnetwork.tools.ArrayTools;
import luecx.visual.basic.framework.Panel;

import java.awt.*;

public class Array2DPanel extends Panel {

    private double[][] ar;

    public Array2DPanel(double[][] ar) {
        this.ar = ar;
    }

    @Override
    public void drawContent(Graphics2D graphics2D) {
        if(ar == null) {
            System.out.println("...");
            return;
        };

        double[][] copy = ArrayTools.normaliseValues(new double[][][]{ar})[0];
        for(int i = 0; i < copy[0].length; i++){
            for(int n = 0; n < copy.length; n++){
                int v = Math.min(255,Math.max(0,(int) (255 * copy[n][i])));
                graphics2D.setColor(new Color(v,v,v));


                if(ar.length == 1){
                    graphics2D.fillRect(
                            (int)((double)n * this.getWidth() / copy.length),
                            (int)((double)i * this.getHeight() / copy[0].length),
                            (int)((double)this.getWidth() / copy.length + 1),
                            (int)((double)this.getHeight() / copy[0].length + 1)
                    );
                }else{
                    graphics2D.fillRect(
                            (int)((double)i * this.getWidth() / copy[0].length),
                            (int)((double)n * this.getHeight() / copy.length),
                            (int)((double)this.getWidth() / copy[0].length + 1),
                            (int)((double)this.getHeight() / copy.length + 1)
                    );
                }

            }
        }
    }

    public double[][] getAr() {
        return ar;
    }

    public void setAr(double[][] ar) {
        this.ar = ar;
    }

    @Override
    public void update() {
        this.repaint();
    }
}
