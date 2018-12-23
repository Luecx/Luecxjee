package luecx.visual.basic.panels;

import luecx.visual.basic.framework.Panel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Graph extends Panel {


    private double min_x = -5, min_y = -2, max_x = 5, max_y = 2;
    private ArrayList<double[][]> data = new ArrayList<>();

    public Graph(double[][]... data) {
        super(100);
        for(double[][] d:data){
            this.data.add(d);
        }
    }

    public void addData(double[][] data){
        this.data.add(data);
    }

    public double getMin_x() {
        return min_x;
    }

    public void setMin_x(double min_x) {
        this.min_x = min_x;
    }

    public double getMin_y() {
        return min_y;
    }

    public void setMin_y(double min_y) {
        this.min_y = min_y;
    }

    public double getMax_x() {
        return max_x;
    }

    public void setMax_x(double max_x) {
        this.max_x = max_x;
    }

    public double getMax_y() {
        return max_y;
    }

    public void setMax_y(double max_y) {
        this.max_y = max_y;
    }

    @Override
    public void update() {
        this.repaint();
    }

    public Point to_clip_space(double x, double y){
        return new Point(
                (int)(this.getWidth() * (double)(x-min_x)/(double)(max_x-min_x)),
                (int)(this.getHeight() - this.getHeight() * (double)(y-min_y)/(double)(max_y-min_y))
        );
    }

    public void drawContent(Graphics2D g){
        g.clearRect(0,0,this.getWidth(),this.getHeight());
        for(double[][] d:data){
            Point last = to_clip_space(d[0][0], d[0][1]);
            for (double[] p:d){

                Point  current = to_clip_space(p[0],p[1]);
                g.drawLine(last.x, last.y, current.x, current.y);
                last = current;
            }
        }
    }
}
