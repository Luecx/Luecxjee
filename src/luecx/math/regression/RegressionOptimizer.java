package luecx.math.regression;

import luecx.math.automatic_differentiation.Function;
import luecx.math.automatic_differentiation.Node;
import luecx.math.automatic_differentiation.ValueNode;

public abstract class RegressionOptimizer extends Function {

    public RegressionOptimizer() {
        super(null);
        ValueNode start = new ValueNode();
        Node out = build_function(start);
        this.outputNode = out;
    }

    protected abstract Node build_function(ValueNode start);

    public RegressionOptimizer create(){
        try {
            return this.getClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
