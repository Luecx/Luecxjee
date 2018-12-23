package luecx.math.values;

public class RealNumber implements NumericValue{

    private double value;

    public RealNumber(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ""+value;
    }
}
