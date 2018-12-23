package luecx.visual.basic.framework;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public abstract class Panel extends JPanel{


    private class Updater extends Thread{

        private Panel panel;
        private int refresh;

        public Updater(Panel panel, int refresh) {
            this.panel = panel;
            this.refresh = refresh;
            this.start();
        }

        public void run(){
            while(!this.isInterrupted()){
                try{
                    panel.update();
                    panel.repaint();
                    Thread.sleep(refresh);
                }catch (Exception e){
                    e.printStackTrace();
                    this.interrupt();
                }
            }
            this.interrupt();
        }
    }

    public Panel(int refreshtime) {
        new Updater(this, refreshtime);
    }

    public Panel(){

    }

    @Override
    protected void paintComponent(Graphics g) {
        drawContent((Graphics2D)g);
    }

    protected void centerString(Graphics g, String s, int x, int y, Font font){
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D r2D = font.getStringBounds(s, frc);

        g.setFont(font);
        g.drawString(s, x - (int)(r2D.getWidth() / 2), y + (int)(r2D.getHeight() / 3));
    }

    protected void centerString(Graphics g, String s, Rectangle r, Font font) {
        FontRenderContext frc = new FontRenderContext(null, true, true);

        Rectangle2D r2D = font.getStringBounds(s, frc);
        int rWidth = (int) Math.round(r2D.getWidth());
        int rHeight = (int) Math.round(r2D.getHeight());
        int rX = (int) Math.round(r2D.getX());
        int rY = (int) Math.round(r2D.getY());

        int a = r.width / 2 - rWidth / 2 - rX;
        int b = r.height / 2 - rHeight / 2 - rY;

        g.setFont(font);
        g.drawString(s, r.x + a, r.y + b);
    }

    public abstract void drawContent(Graphics2D graphics2D);
    public abstract void update();
}
