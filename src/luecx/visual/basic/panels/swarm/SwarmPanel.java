package luecx.visual.basic.panels.swarm;

import luecx.ai.swarm.boids_model.BoidSwarm;
import luecx.ai.swarm.boids_model.BoidUnit;
import luecx.ai.swarm.boids_model.Swarm;
import luecx.ai.swarm.boids_model.Unit;
import luecx.math.values.tensor.Vector;
import luecx.visual.basic.framework.Frame;
import luecx.visual.basic.framework.Panel;

import java.awt.*;

public class SwarmPanel extends Panel {

    private Swarm swarm;

    public SwarmPanel(Swarm swarm) {
        super(100);
        this.swarm = swarm;
    }

    @Override
    public void drawContent(Graphics2D graphics2D) {
        graphics2D.clearRect(0,0,10000,10000);
        for(Unit u:swarm.getUnits()){
            Vector pos = u.getPosition();
            graphics2D.fillOval((int)(pos.getValue(0) * 10 - 2),
                    (int)(pos.getValue(1) * 10 - 2), 4,4);
        }
    }

    @Override
    public void update() {
        swarm.update(0.01);
    }

    public static void main(String[] args) {

        Swarm s = new BoidSwarm();
        for(int i = 0; i < 200; i++){
            s.getUnits().add(new BoidUnit((Math.random() * 100), (Math.random() * 100)));
        }
        new Frame(new SwarmPanel(s));
    }
}
