package luecx.ai.neuralnetwork.error;


import luecx.ai.neuralnetwork.layers.OutputLayer;

/**
 * Created by finne on 22.01.2018.
 */
public abstract class ErrorFunction{


    public abstract double overall_error(OutputLayer outputLayer, double[][][] expected);

    public abstract void apply(OutputLayer outputLayer, double[][][] expected);

}
