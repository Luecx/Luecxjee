package luecx.ai.swarm.boids_model;

import luecx.math.values.tensor.Vector;

public abstract class Unit {

    private Vector position;
    private Vector direction;

    public Unit(Vector position) {
        this.position = position;
    }

    public Unit(double... position) {
        this.position = new Vector(position);
    }

    public Unit(int dimensions){
        this.position = new Vector(dimensions);
    }

    public void updatePosition(double t){
        position.add(new Vector(this.direction.getValues()).scale(t));
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public Vector getDirection() {
        return direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public abstract Vector calculateDirection(Swarm swarm);
}
