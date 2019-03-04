package luecx.ai.swarm.boids_model;

import luecx.math.values.tensor.Vector;

public class BoidUnit extends Unit {

    public BoidUnit(Vector position) {
        super(position);
    }

    public BoidUnit(double... position) {
        super(position);
    }

    public BoidUnit(int dimensions) {
        super(dimensions);
    }

    @Override
    public Vector calculateDirection(Swarm swarm) {
        if(swarm instanceof BoidSwarm){
            return (Vector) new Vector(this.getPosition()).sub(((BoidSwarm) swarm).getAveragePosition()).negate();
        }
        return new Vector();

    }
}
