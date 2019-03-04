package luecx.ai.swarm.boids_model;

import luecx.math.values.tensor.Vector;

public class BoidSwarm extends Swarm {

    private Vector averagePosition;

    @Override
    protected void update() {
        if(this.units.size() == 0) return;
        this.averagePosition = new Vector(this.units.get(0).getPosition().getSize());
        for(Unit i:units){
            averagePosition.add(new Vector(i.getPosition()).scale(1d / units.size()));
        }
    }

    public Vector getAveragePosition() {
        return averagePosition;
    }

    public void setAveragePosition(Vector averagePosition) {
        this.averagePosition = averagePosition;
    }
}
