package luecx.math.values.tensor;

public class Matrix extends Tensor {

    protected int m;
    protected int n;

    public Matrix(Matrix o){
        super(o);
        this.m = o.m;
        this.n = o.n;
    }

    public Matrix(int m, int n) {
        super(m, n);
        this.m = m;
        this.n = n;
    }

    @Override
    public Tensor mul(Tensor right, Tensor target) {
        if(right instanceof Matrix){
            mul((Matrix)right, (Matrix)target);
        }
        return this;
    }

    public Matrix mul(Matrix right, Matrix target) {
        double v = 0;
        for (int x = 0; x < target.n; x++) {
            for (int y = 0; y < target.m; y++) {
                v = 0;
                for (int i = 0; i < n; i++) {
                    v += getValue(y, i) * right.getValue(i, x);
                }
                target.setValue(v, y, x);
            }
        }
        return this;
    }

    public void identity(){
        for(int i = 0; i < Math.min(m,n); i++){
            setValue(1, i, i);
        }
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }


    @Override
    public String toString() {
        String s ="Matrix [" + m +
                ", " + n +"]\n";
        for(int i = 0; i < m; i++){
            for(int n = 0; n < this.n; n++){
                s += getValue(i,n) + ", ";
            }
            s+= "\n";
        }
        return s;
    }
}
