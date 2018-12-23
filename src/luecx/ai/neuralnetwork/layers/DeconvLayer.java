package luecx.ai.neuralnetwork.layers;


import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.NetworkBuilder;
import luecx.ai.neuralnetwork.activation.ActivationFunction;
import luecx.ai.neuralnetwork.activation.ReLU;
import luecx.ai.neuralnetwork.data.TrainSet;
import luecx.ai.neuralnetwork.error.MSE;
import luecx.ai.neuralnetwork.tools.ArrayTools;
import luecx.data.image.Loader;

public class DeconvLayer extends Layer {


    private int channel_amount;
    private int filter_size;
    private int filter_Stride;
    private int padding = 0;


    private double[][][][] filter;
    private double[] bias;

    private double lowerWeightsRange = Double.NaN, upperWeigthsRange = Double.NaN;
    private double lowerBiasRange = 0, upperBiasRange = 1;

    private ActivationFunction activationFunction = new ReLU();

    public DeconvLayer(int channel_amount, int filter_size, int filter_Stride) {
        this.channel_amount = channel_amount;
        this.filter_size = filter_size;
        this.filter_Stride = filter_Stride;


    }

    public DeconvLayer weightsRange(double lower, double upper) {
        this.lowerWeightsRange = lower;
        this.upperWeigthsRange = upper;
        return this;
    }

    public void printWeights() {
        for (double[][][] f : this.filter) {
            Layer.printArray(f);
        }
    }

    public DeconvLayer biasRange(double lower, double upper) {
        this.lowerBiasRange = lower;
        this.upperBiasRange = upper;
        return this;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public DeconvLayer setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
        return this;
    }

    public DeconvLayer setFilter(int index, double[][][] filter, double bias) {
        if(this.filter.length <= index) return this;
        this.bias[index] = bias;
        this.filter[index] = filter;
        return this;
    }

    public double[][][] getFilter(int index) {
        return filter[index];
    }


    @Override
    protected void on_build() throws Exception {


        this.filter = new double[this.INPUT_DEPTH][][][];
        this.bias = new double[this.INPUT_DEPTH];

        if (Double.isNaN(lowerWeightsRange) || Double.isNaN(upperWeigthsRange)) {
            lowerWeightsRange = -1d / (Math.sqrt(this.OUTPUT_DEPTH * filter_size * filter_size));
            upperWeigthsRange = -lowerWeightsRange;
            lowerBiasRange = lowerWeightsRange;
            upperBiasRange = upperWeigthsRange;
        }

        for (int i = 0; i < this.filter.length; i++) {
            if (this.filter[i] == null || this.filter[i].length != this.OUTPUT_DEPTH
                    || this.filter[i][0].length != this.filter_size
                    || this.filter[i][0][0].length != this.filter_size) {
                filter[i] = ArrayTools.createRandomArray(this.getOUTPUT_DEPTH(), filter_size, filter_size, lowerWeightsRange, upperWeigthsRange);
                bias[i] = ArrayTools.randomValue(lowerBiasRange, upperBiasRange);
            }
        }
    }

    @Override
    protected void calculateOutputDimensions() throws Exception {
        this.OUTPUT_DEPTH = channel_amount;
        this.OUTPUT_WIDTH = filter_size + (this.INPUT_WIDTH - 1) * filter_Stride;
        this.OUTPUT_HEIGHT = filter_size + (this.INPUT_HEIGHT - 1) * filter_Stride;
    }

    @Override
    public void calculate() {

        for (int j = 0; j < getOUTPUT_DEPTH(); j++) {
            for (int i = 0; i < getOUTPUT_WIDTH(); i++) {
                for (int n = 0; n < getOUTPUT_HEIGHT(); n++) {
                    this.getOutput_values()[j][i][n] = 0;
                }
            }
        }

        for (int i = 0; i < this.INPUT_DEPTH; i++) {
            for (int j = 0; j < this.INPUT_WIDTH; j++) {
                for (int n = 0; n < this.INPUT_HEIGHT; n++) {
                    this.calcSample(i, j, n);
                }
            }
        }
        this.activationFunction.apply(this.output_values, this.output_derivative_values);
    }

    public void calcSample(int d, int x, int y) {
        for (int j = 0; j < getOUTPUT_DEPTH(); j++) {
            for (int i = 0; i < filter_size; i++) {
                for (int n = 0; n < filter_size; n++) {
                    int x_i = -padding + (x * filter_Stride) + i;
                    int y_i = -padding + (y * filter_Stride) + n;
                    if (x_i >= 0 && y_i >= 0 && x_i < getOUTPUT_WIDTH() && y_i < getOUTPUT_HEIGHT()) {
                        this.output_values[j][x_i][y_i] +=
                                this.filter[d][j][i][n] * getInput_values()[d][x][y];
                    }
                }
            }
        }
    }

    @Override
    public void backprop_error() {
        for (int input_d = 0; input_d < this.INPUT_DEPTH; input_d++) {
            for (int input_w = 0; input_w < this.INPUT_WIDTH; input_w++) {
                for (int input_h = 0; input_h < this.INPUT_HEIGHT; input_h++) {

                    double sum = 0;

                    for (int j = 0; j < getOUTPUT_DEPTH(); j++) {
                        for (int i = 0; i < filter_size; i++) {
                            for (int n = 0; n < filter_size; n++) {

                                int x_i = -padding + (input_w * filter_Stride) + i;
                                int y_i = -padding + (input_h * filter_Stride) + n;
                                if (x_i >= 0 && y_i >= 0 && x_i < getOUTPUT_WIDTH() && y_i < getOUTPUT_HEIGHT()) {
                                    sum += this.filter[input_d][j][i][n] * output_error_values[j][x_i][y_i];
                                }
                            }
                        }
                    }
                    this.getPrev_layer().output_error_values[input_d][input_w][input_h] = sum
                            * getPrev_layer().output_derivative_values[input_d][input_w][input_h];
                }
            }
        }
    }

    @Override
    public void update_weights(double eta) {
        for (int input_d = 0; input_d < this.INPUT_DEPTH; input_d++) {
            for (int input_w = 0; input_w < this.INPUT_WIDTH; input_w++) {
                for (int input_h = 0; input_h < this.INPUT_HEIGHT; input_h++) {

                    //bias[input_d] -= getOutput_error_values()[output_d][output_w][output_h] * eta;

                    for (int j = 0; j < getOUTPUT_DEPTH(); j++) {
                        for (int i = 0; i < filter_size; i++) {
                            for (int n = 0; n < filter_size; n++) {

                                int x_i = -padding + (input_w * filter_Stride) + i;
                                int y_i = -padding + (input_h * filter_Stride) + n;
                                if (x_i >= 0 && y_i >= 0 && x_i < getOUTPUT_WIDTH() && y_i < getOUTPUT_HEIGHT()) {
                                    this.filter[input_d][j][i][n] +=
                                            -getOutput_error_values()[j][x_i][y_i] *
                                                    this.getInput_values()[input_d][input_w][input_h] *
                                                    eta;

                                }
                            }
                        }
                    }


                }
            }
        }
    }

    public static void main(String[] args){
        NetworkBuilder builder = new NetworkBuilder(1,3,3);
        builder.addLayer(new DeconvLayer(6,3,1));
        builder.addLayer(new DeconvLayer(6,3,2));
        builder.addLayer(new DeconvLayer(6,3,2));
        builder.addLayer(new DeconvLayer(1,3,1));
        Network network = builder.buildNetwork();
        network.setErrorFunction(new MSE());

        TrainSet trainSet = new TrainSet(1,3,3, 1,25,25);

        trainSet.addData(ArrayTools.createRandomArray(1,3,3,0,1), Loader.loadImage_3d_bw("res/index.png"));
        trainSet.addData(ArrayTools.createRandomArray(1,3,3,0,1), Loader.loadImage_3d_bw("res/monalisa25.png"));


        network.train(trainSet, 10000,2,0.01);



        double[][][] in = new double[1][3][3];
        for(int i = 0; i < 3; i++){
            for(int n = 0; n < 3; n++){
                in[0][i][n] = (trainSet.getInput(0)[0][i][n] + trainSet.getInput(1)[0][i][n])/2;
            }
        }

        Loader.writeImage("deconv/monalisa25out_1", network.calculate(trainSet.getInput(0)));
        Loader.writeImage("deconv/monalisa25out_2", network.calculate(trainSet.getInput(1)));
        Loader.writeImage("deconv/monalisa25out_3", network.calculate(in));





    }

    public int getChannel_amount() {
        return channel_amount;
    }

    public int getFilter_size() {
        return filter_size;
    }

    public int getFilter_Stride() {
        return filter_Stride;
    }

    public int getPadding() {
        return padding;
    }

    @Override
    public Layer clone() {
        return null;
    }
}
