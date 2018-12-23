package luecx.math.values.tensor;

public class Number extends Vector {

    public Number() {
        super(1);
    }

    public Number(double value) {
        super(1);
        values[0] = value;
    }
}
