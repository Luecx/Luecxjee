package luecx.ai.neuralnetwork.layers;


import luecx.ai.neuralnetwork.error.ErrorFunction;
import luecx.ai.neuralnetwork.error.MSE;

/**
 * Created by finne on 27.01.2018.
 */
public class OutputLayer extends Layer {

    public OutputLayer(int OUTPUT_DEPTH, int OUTPUT_WIDTH, int OUTPUT_HEIGHT) {
        super(OUTPUT_DEPTH, OUTPUT_WIDTH, OUTPUT_HEIGHT);
    }
    public OutputLayer(Layer prev_layer) {
        this(prev_layer.OUTPUT_DEPTH, prev_layer.OUTPUT_WIDTH, prev_layer.OUTPUT_HEIGHT);
    }

    private ErrorFunction errorFunction;

    public ErrorFunction getErrorFunction() {
        return errorFunction;
    }

    public void setErrorFunction(ErrorFunction errorFunction) {
        this.errorFunction = errorFunction;
    }

    public void calculateOutputErrorValues(double[][][] expectedOutput) {
        this.errorFunction.apply(this, expectedOutput);
    }

    public double overall_error(double[][][] expected) {
        return this.getErrorFunction().overall_error(this,expected);
    }

    @Override
    protected void on_build() {
        if(this.errorFunction == null) this.errorFunction = new MSE();
    }

    @Override
    protected void calculateOutputDimensions() {

    }

    @Override
    public void calculate() {
        this.output_values = this.getInput_values();
        this.output_derivative_values = this.getInput_derivative_values();
    }

    @Override
    public void backprop_error() {
        this.getPrev_layer().setOutput_error_values(this.output_error_values);
    }

    @Override
    public void update_weights(double eta) {

    }

    @Override
    public Layer clone() {
        return null;
    }
}
