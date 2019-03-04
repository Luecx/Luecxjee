package luecx.math.values.tensor;

import java.util.Arrays;

public class Tensor {

    protected double[] values;
    protected int[] dimensions;

    public Tensor() {
    }

    public Tensor(int... dimensions) {
        this.dimensions = dimensions;
        int entries = 1;
        for(int i:dimensions){
            entries *= i;
        }
        values = new double[entries];
    }

    public Tensor(Tensor t){
        this.values = Arrays.copyOf(t.values, t.values.length);
        this.dimensions = Arrays.copyOf(t.dimensions, t.dimensions.length);
    }

    public double[] getValues() {
        return values;
    }

    protected void setValues(double[] values) {
        this.values = values;
    }

    protected int[] getDimensions() {
        return dimensions;
    }

    protected void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
    }

    public Tensor mul(Tensor right, Tensor target){

        if(dimensions.length > 2 || right.dimensions.length > 2) return this;
        double v = 0;

        if(right.dimensions.length == 1){
            if(this.dimensions.length == 1){
                target.values[0] = values[0] * right.values[0];
                return this;
            }else{
                for(int y = 0; y < target.dimensions[0]; y++){
                    v = 0;
                    for(int i = 0; i < this.dimensions[1]; i++){
                        v += getValue(y,i) * right.getValue(i);
                    }
                    target.setValue(v,y);
                }
            }
        }else{
            if(this.dimensions.length == 1){
                for(int y = 0; y < target.dimensions[0]; y++){
                    for(int x = 0; x < this.dimensions[1]; x++){
                        target.setValue(values[y] * right.values[x],y,x);
                    }
                }
            }else{
                for(int x = 0; x < target.dimensions[1]; x++){
                    for(int y = 0; y < target.dimensions[0]; y++){
                        v = 0;
                        for(int i = 0; i < dimensions[1]; i++){
                            v += getValue(y,i) * right.getValue(i,x);
                        }
                        target.setValue(v, x,y);
                    }
                }
            }
        }
        return this;
    }

    public int dim(){
        return dimensions.length;
    }

    public Tensor add(Tensor t){
        for(int i = 0; i < Math.min(values.length, t.values.length); i++){
            values[i] += t.values[i];
        }
        return this;
    }

    public Tensor scale(double scalar){
        for(int i = 0; i < values.length; i++){
            values[i] *= scalar;
        }
        return this;
    }

    public Tensor hadamard(Tensor t){
        for(int i = 0; i < Math.min(values.length, t.values.length); i++){
            values[i] *= t.values[i];
        }return this;
    }

    public Tensor sub(Tensor t){
        for(int i = 0; i < Math.min(values.length, t.values.length); i++){
            values[i] -= t.values[i];
        }return this;
    }

    public Tensor negate(){
        for(int i = 0; i < values.length; i++){
            values[i] =- values[i];
        }return this;
    }

    public double getValue(int... position){
        int pos = 0;
        int total = 1;
        for(int i = 0; i < dimensions.length; i++){
            pos += total * position[i];
            total *= dimensions[i];
        }
        return values[pos];
    }

    public void setValue(double val, int... position){
        int pos = 0;
        int total = 1;
        for(int i = 0; i < dimensions.length; i++){
            pos += total * position[i];
            total *= dimensions[i];
        }
        values[pos] = val;
    }

    public static void main(String[] args) {
//        Tensor t = new Tensor(5,6,7,2);
//        System.out.println(t.getValue(0,5,3,5));

        double[][][][] ar = new double[10][20][40][10];
        double[] ar2 = new double[10*20*40*10];

        for(int p = 0; p < 10; p++){
            long time = System.nanoTime();
            for(int i = 0; i < 10; i++){
                for(int j = 0; j < 20; j++){
                    for(int k = 0; k < 40; k++){
                        for(int m = 0; m < 10; m++){
                            ar2[i * 8000 + j * 400 + k * 10 + m] = Math.random();
                        }
                    }
                }
            }

            System.out.print(System.nanoTime()-time);
            time = System.nanoTime();

            for(int i = 0; i < 10; i++){
                for(int j = 0; j < 20; j++){
                    for(int k = 0; k < 40; k++){
                        for(int m = 0; m < 10; m++){
                            ar[i][j][k][m] = Math.random();
                        }
                    }
                }
            }
            System.out.println("   " + (System.nanoTime()-time));
        }





    }
}
