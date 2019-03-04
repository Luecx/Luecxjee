package luecx.visual.basic.panels.arrays;

import luecx.ai.neuralnetwork.tools.ArrayTools;
import luecx.visual.basic.framework.Frame;
import luecx.visual.basic.framework.Panel;

import java.awt.*;

public class Array3DPanel extends Panel {

    private Array2DPanel[] panels;
    private double[][][] array;

    public Array3DPanel(double[][][] array, boolean flip) {
        this.array = array;
        this.setLayout(new GridLayout(flip ? 1:array.length,flip? array.length:1,10,10));
        this.panels = new Array2DPanel[array.length];
        for(int i = 0; i < array.length; i++){
            Array2DPanel p = new Array2DPanel(array[i]);
            this.panels[i] = p;
            this.add(p);
        }
    }

    public Array3DPanel(double[][][] array) {
        super();
        this.setLayout(new GridLayout(array.length,1,10,10));
        this.panels = new Array2DPanel[array.length];
        for(int i = 0; i < array.length; i++){
            Array2DPanel p = new Array2DPanel(array[i]);
            this.panels[i] = p;
            this.add(p);
        }
    }



    @Override
    public void drawContent(Graphics2D graphics2D) {

    }

    @Override
    public void update() {

    }

    public double[][][] getArray() {
        return array;
    }

    public void setArray(double[][][] array) {
        this.array = array;
        for(int i = 0; i < panels.length; i++){
            panels[i].setAr(array[i]);
        }
    }

    public static void main(String[] args) {
        double[][][] ar = ArrayTools.createRandomArray(3,5,5,-1,1);
        new Frame(new Array3DPanel(ar));
    }
}
