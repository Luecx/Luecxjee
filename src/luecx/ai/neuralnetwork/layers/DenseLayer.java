package luecx.ai.neuralnetwork.layers;


import luecx.ai.neuralnetwork.activation.ActivationFunction;
import luecx.ai.neuralnetwork.activation.Sigmoid;
import luecx.ai.neuralnetwork.tools.ArrayTools;

/**
 * Created by finne on 27.01.2018.
 */
public class DenseLayer extends Layer {

    private double[][] weights;
    private double[]   bias;

    private ActivationFunction activationFunction;


    public DenseLayer(int output_height) {
        super(1, 1, output_height);
    }

    public DenseLayer setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
        return this;
    }

    private double lowerWeightsRange = Double.NaN, upperWeigthsRange = Double.NaN;
    private double lowerBiasRange = 0, upperBiasRange = 1;


    public DenseLayer weightsRange(double lower, double upper) {
        this.lowerWeightsRange = lower;
        this.upperWeigthsRange = upper;
        return this;
    }

    public DenseLayer biasRange(double lower, double upper) {
        this.lowerBiasRange = lower;
        this.upperBiasRange = upper;
        return this;
    }

    @Override
    protected void on_build() throws Exception{
        if(this.INPUT_WIDTH > 1 || this.INPUT_DEPTH > 1){
            throw new Exception("Input must be flattened");
        }else{
            if(weights == null || weights.length != this.OUTPUT_HEIGHT || weights[0].length != this.INPUT_HEIGHT) {
                if (Double.isNaN(lowerWeightsRange) || Double.isNaN(upperWeigthsRange)) {
                    weights = ArrayTools.createRandomArray(this.OUTPUT_HEIGHT, this.INPUT_HEIGHT, -1d / Math.sqrt(this.INPUT_HEIGHT), 1d / Math.sqrt(this.INPUT_HEIGHT));
                } else {
                    weights = ArrayTools.createRandomArray(this.OUTPUT_HEIGHT, this.INPUT_HEIGHT, lowerWeightsRange, upperWeigthsRange);
                }
            }
            if(bias == null || bias.length != this.OUTPUT_HEIGHT){
                bias =  ArrayTools.createRandomArray(this.OUTPUT_HEIGHT, lowerBiasRange, upperBiasRange);
            }
            if(activationFunction == null) {
                activationFunction = new Sigmoid();
            }
        }
    }

    public void printWeights() {
        Layer.printArray(new double[][][]{this.weights});
        Layer.printArray(new double[][][]{{this.bias}});
    }

    @Override
    protected void calculateOutputDimensions() {

    }

    @Override
    public void calculate() {
        for(int i = 0; i < this.OUTPUT_HEIGHT; i++) {
            double sum = bias[i];
            for(int n = 0; n < this.INPUT_HEIGHT; n++) {
                sum += this.getInput_values()[0][0][n] * weights[i][n];
            }
            this.output_values[0][0][i] = sum;
            this.output_derivative_values[0][0][i] = sum;
        }
        this.activationFunction.apply(this.output_values, this.output_derivative_values);
    }

    @Override
    public void backprop_error() {
        for(int i = 0; i < this.INPUT_HEIGHT; i++) {
            double sum = 0;
            for(int n = 0; n < this.getOUTPUT_HEIGHT(); n++) {
                sum += weights[n][i] * output_error_values[0][0][n];
            }
            this.getPrev_layer().getOutput_error_values()[0][0][i] = this.getInput_derivative_values()[0][0][i] * sum;
        }
    }

    public DenseLayer setWeights(double[][] weights){
        if(weights != null && weights[0] != null){
            this.weights = weights;
            this.lowerWeightsRange = -1;
            this.upperWeigthsRange = 1;
        }
        return this;
    }

    public DenseLayer setBias(double[] bias){
        if(bias != null && bias.length == this.OUTPUT_HEIGHT){
            this.bias = bias;
            this.lowerBiasRange = -1;
            this.upperBiasRange = 1;
        }
        return this;
    }

    public double[][] getWeights() {
        return weights;
    }

    public double[] getBias() {
        return bias;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    @Override
    public void update_weights(double eta) {
        for(int i = 0; i < this.OUTPUT_HEIGHT; i++) {
            double delta = - eta * this.output_error_values[0][0][i];
            bias[i] += delta;

            for(int prevNeuron = 0; prevNeuron < this.INPUT_HEIGHT; prevNeuron ++) {
                weights[i][prevNeuron] += delta * getInput_values()[0][0][prevNeuron];
            }
        }
    }

    @Override
    public Layer clone() {
        DenseLayer denseLayer = new DenseLayer(this.OUTPUT_HEIGHT);
        denseLayer.setActivationFunction(this.getActivationFunction());
        denseLayer.setWeights(ArrayTools.copyArray(this.getWeights()));
        denseLayer.setBias(ArrayTools.copyArray(this.getBias()));
        return denseLayer;
    }
}
