package projects.interpolator;

import luecx.visual.basic.framework.Panel;

import java.awt.*;

public class NetworkOutputPanel extends Panel {

    private BooleanInterpolation interpolation;
    private InterpolationPanel parent;

    public NetworkOutputPanel(BooleanInterpolation interpolation, InterpolationPanel parent) {
        super(50);
        this.interpolation = interpolation;
        this.parent = parent;
    }

    public NetworkOutputPanel(InterpolationPanel parent) {
        super(50);
        this.parent = parent;
    }

    public BooleanInterpolation getInterpolation() {
        return interpolation;
    }

    public void setInterpolation(BooleanInterpolation interpolation) {
        this.interpolation = interpolation;
    }

    @Override
    public void drawContent(Graphics2D graphics2D) {

        graphics2D.clearRect(0, 0, 10000, 10000);


        if(interpolation != null && interpolation.getImage() != null )
            graphics2D.drawImage(interpolation.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);

        if(interpolation != null){
            double z = parent.getjSlider1().getValue() / 100d;
            for(int i = 0; i < interpolation.getTrainSet().size(); i++){
                int x = (int)(this.getWidth() * interpolation.getTrainSet().getInput(i)[0][0][0]);
                int y = (int)(this.getHeight() * interpolation.getTrainSet().getInput(i)[0][0][1]);
                if(Math.sqrt((interpolation.getTrainSet().getInput(i)[0][0][2] - z) * (interpolation.getTrainSet().getInput(i)[0][0][2] - z)) < 0.03){



                    graphics2D.setColor(interpolation.getTrainSet().getOutput(i)[0][0][0] > 0.5 ? Color.red:Color.green);
                    graphics2D.fillOval(x-10, y-10, 20,20);
                    graphics2D.setColor(Color.black);
                    graphics2D.drawOval(x-10, y-10, 20,20);

                }
            }
        }

    }

    @Override
    public void update() {

    }
}
