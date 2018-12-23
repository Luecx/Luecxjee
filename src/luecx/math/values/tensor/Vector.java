package luecx.math.values.tensor;

import java.util.Arrays;

public class Vector extends Matrix {

    public Vector(int size) {
        super(size, 1);
        this.m = size;
    }

    public Vector(double... values){
        super(values.length, 1);
        this.setValues(values);
        this.m = values.length;
    }

    public double getValue(int index) {
        return super.getValue(index, 0);
    }

    public int getSize() {
        return m;
    }






    public static void main(String[] args) {
        Matrix t = new Matrix(3,3);
        t.identity();
        Tensor v = new Vector(3);
        ((Vector) v).identity();

        Tensor target = new Vector(3);
        t.mul(v,target);

        System.out.println(t);
        System.out.println(v);
        System.out.println(target);
    }
}
