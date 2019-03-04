package luecx.ai.neuralnetwork.layers;


import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.NetworkBuilder;
import luecx.ai.neuralnetwork.activation.ActivationFunction;
import luecx.ai.neuralnetwork.activation.ReLU;
import luecx.ai.neuralnetwork.tools.ArrayTools;

/**
 * Created by finne on 04.02.2018.
 */
public class ConvLayer extends Layer {


    private int channel_amount;
    private int filter_size;
    private int filter_Stride;
    private int padding;

    private double[][][][] filter;
    private double[] bias;

    private int[][] y_i_range;
    private int[][] x_i_range;
    private int[][] filter_xy;

    public ConvLayer(int channel_amount, int filter_size, int filter_Stride, int padding) {
        this.channel_amount = channel_amount;
        this.filter_size = filter_size;
        this.filter_Stride = filter_Stride;
        this.padding = padding;
        this.filter = new double[channel_amount][][][];
        this.bias = new double[channel_amount];
    }

    private double lowerWeightsRange = Double.NaN, upperWeigthsRange = Double.NaN;
    private double lowerBiasRange = 0, upperBiasRange = 1;

    private ActivationFunction activationFunction = new ReLU();

    public ConvLayer weightsRange(double lower, double upper) {
        this.lowerWeightsRange = lower;
        this.upperWeigthsRange = upper;
        return this;
    }

    public void printWeights() {
        for (double[][][] f : this.filter) {
            Layer.printArray(f);
        }
    }

    public ConvLayer biasRange(double lower, double upper) {
        this.lowerBiasRange = lower;
        this.upperBiasRange = upper;
        return this;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public ConvLayer setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
        return this;
    }

    public ConvLayer setFilter(int index, double[][][] filter, double bias) {
        this.bias[index] = bias;
        this.filter[index] = filter;
        return this;
    }

    public double[][][] getFilter(int index) {
        return filter[index];
    }

    public double[] getBias(){
        return bias;
    }

    public void setBias(double[] bias){
        this.bias = bias;
    }

    @Override
    protected void on_build() throws Exception {
        if (Double.isNaN(lowerWeightsRange) || Double.isNaN(upperWeigthsRange)) {
            lowerWeightsRange = -1d / (Math.sqrt(this.INPUT_DEPTH * filter_size * filter_size));
            upperWeigthsRange = -lowerWeightsRange;
            lowerBiasRange = lowerWeightsRange;
            upperBiasRange = upperWeigthsRange;
        }

        for (int i = 0; i < this.filter.length; i++) {
            if (this.filter[i] == null || this.filter[i].length != this.INPUT_DEPTH
                    || this.filter[i][0].length != this.filter_size
                    || this.filter[i][0][0].length != this.filter_size) {
                filter[i] = ArrayTools.createRandomArray(this.getINPUT_DEPTH(), filter_size, filter_size, lowerWeightsRange, upperWeigthsRange);
                bias[i] = ArrayTools.randomValue(lowerBiasRange, upperBiasRange);
            }
        }

        this.x_i_range = new int[this.OUTPUT_WIDTH][2];
        this.y_i_range = new int[this.OUTPUT_HEIGHT][2];
        this.filter_xy = new int[Math.max(this.INPUT_WIDTH, this.INPUT_HEIGHT)][Math.max(this.OUTPUT_WIDTH, this.OUTPUT_HEIGHT)];

        for (int j = 0; j < this.filter_xy.length; j++) {
            for (int i = 0; i < this.filter_xy[0].length; i++) {
                this.filter_xy[j][i] = j + padding - i * filter_Stride;
            }
        }

        for (int j = 0; j < this.OUTPUT_WIDTH; j++) {
            this.x_i_range[j][0] = Math.max(0, -padding + (j * filter_Stride) + 0);
            this.x_i_range[j][1] = Math.min(this.INPUT_WIDTH, -padding + (j * filter_Stride) + filter_size);
        }
        for (int j = 0; j < this.OUTPUT_HEIGHT; j++) {
            this.y_i_range[j][0] = Math.max(0, -padding + (j * filter_Stride) + 0);
            this.y_i_range[j][1] = Math.min(this.INPUT_HEIGHT, -padding + (j * filter_Stride) + filter_size);
        }

    }

    @Override
    protected void calculateOutputDimensions() throws Exception {

        this.OUTPUT_DEPTH = channel_amount;
        this.OUTPUT_WIDTH = (this.INPUT_WIDTH + this.padding * 2 - filter_size) / filter_Stride + 1;
        this.OUTPUT_HEIGHT = (this.INPUT_HEIGHT + this.padding * 2 - filter_size) / filter_Stride + 1;

        double g = ((double) this.INPUT_WIDTH + (double) this.padding * 2 - (double) filter_size) / (double) filter_Stride + 1;
        double g1 = ((double) this.INPUT_HEIGHT + (double) this.padding * 2 - (double) filter_size) / (double) filter_Stride + 1;

        if (g != (int) g || g1 != (int) g1) throw new Exception("Format does not work! Use a different padding-value!");

    }

    public double getWeight(int channel, int depth, int x, int y) {
        return this.filter[channel][depth][x][y];
    }

    public void increaseWeight(int channel, int depth, int x, int y, double val) {
        this.filter[channel][depth][x][y] += val;
    }

    @Override
    public void calculate() {
        for (int i = 0; i < this.OUTPUT_DEPTH; i++) {
            for (int j = 0; j < this.OUTPUT_WIDTH; j++) {
                for (int n = 0; n < this.OUTPUT_HEIGHT; n++) {
                    this.output_values[i][j][n] = this.calcSample(i, j, n);
                }
            }
        }
        this.activationFunction.apply(this.output_values, this.output_derivative_values);
    }

    public double calcSample(int actIndex, int x, int y) {
        double total = bias[actIndex];
        for (int j = 0; j < getINPUT_DEPTH(); j++) {
//            for (int i = 0; i < filter_size; i++) {
//                for (int n = 0; n < filter_size; n++) {
//                    int x_i = -padding + (x * filter_Stride) + i;
//                    int y_i = -padding + (y * filter_Stride) + n;
//                    if (x_i >= 0 && y_i >= 0 && x_i < getINPUT_WIDTH() && y_i < getINPUT_HEIGHT()) {
//                        total += this.filter[actIndex][j][i][n] *
//                                getInput_values()[j][x_i][y_i];
//                    }
//
//                }
//            }


            for(int x_i = x_i_range[x][0]; x_i < x_i_range[x][1]; x_i++){
                for(int y_i = y_i_range[y][0]; y_i < y_i_range[y][1]; y_i++){
                    total += this.filter[actIndex][j][filter_xy[x_i][x]][filter_xy[y_i][y]] * getInput_values()[j][x_i][y_i];
                }
            }
        }

        return total;
    }

    @Override
    public void backprop_error() {

        for (int j = 0; j < getINPUT_DEPTH(); j++) {
            for (int i = 0; i < getINPUT_WIDTH(); i++) {
                for (int n = 0; n < getINPUT_HEIGHT(); n++) {
                    this.getPrev_layer().getOutput_error_values()[j][i][n] = 0;
                }
            }
        }

        for (int output_d = 0; output_d < this.OUTPUT_DEPTH; output_d++) {
            for (int output_w = 0; output_w < this.OUTPUT_WIDTH; output_w++) {
                for (int output_h = 0; output_h < this.OUTPUT_HEIGHT; output_h++) {

                    for (int j = 0; j < getINPUT_DEPTH(); j++) {
                        for(int x_i = x_i_range[output_w][0]; x_i < x_i_range[output_w][1]; x_i++) {
                            for (int y_i = y_i_range[output_h][0]; y_i < y_i_range[output_h][1]; y_i++) {
                                this.getPrev_layer().output_error_values[j][x_i][y_i] +=
                                this.filter[output_d][j][filter_xy[x_i][output_w]][filter_xy[y_i][output_h]] * output_error_values[output_d][output_w][output_h]
                                                   * getPrev_layer().output_derivative_values[j][filter_xy[x_i][output_w]][filter_xy[y_i][output_h]];
                            }
                        }
//
//                        for (int i = 0; i < filter_size; i++) {
//                            for (int n = 0; n < filter_size; n++) {
//                                int x_i = -padding + (output_w * filter_Stride) + i;
//                                int y_i = -padding + (output_h * filter_Stride) + n;
//                                if (x_i >= 0 && y_i >= 0 && x_i < getINPUT_WIDTH() && y_i < getINPUT_HEIGHT()) {
//                                    this.getPrev_layer().output_error_values[j][x_i][y_i] +=
//
//                                            this.filter[output_d][j][i][n] * output_error_values[output_d][output_w][output_h]
//                                                    * getPrev_layer().output_derivative_values[j][i][n];
//                                }
//
//                            }
//                        }
                    }

                }
            }
        }
    }

    @Override
    public void update_weights(double eta) {
        double e= eta;
        for (int output_d = 0; output_d < this.OUTPUT_DEPTH; output_d++) {
            for (int output_w = 0; output_w < this.OUTPUT_WIDTH; output_w++) {
                for (int output_h = 0; output_h < this.OUTPUT_HEIGHT; output_h++) {

                    bias[output_d] -= getOutput_error_values()[output_d][output_w][output_h] * eta;

                    for (int j = 0; j < getINPUT_DEPTH(); j++) {

                        for(int x_i = x_i_range[output_w][0]; x_i < x_i_range[output_w][1]; x_i++) {
                            for (int y_i = y_i_range[output_h][0]; y_i < y_i_range[output_h][1]; y_i++) {

                                this.filter[output_d][j][filter_xy[x_i][output_w]][filter_xy[y_i][output_h]] +=
                                            -getOutput_error_values()[output_d][output_w][output_h] *
                                                    this.getInput_values()[j][x_i][y_i] * e;
                            }
                        }

//                        for (int i = 0; i < filter_size; i++) {
//                            for (int n = 0; n < filter_size; n++) {
//
//                                int x_i = -padding + (output_w * filter_Stride) + i;
//                                int y_i = -padding + (output_h * filter_Stride) + n;
//                                if (x_i >= 0 && y_i >= 0 && x_i < getINPUT_WIDTH() && y_i < getINPUT_HEIGHT()) {
//                                    this.filter[output_d][j][i][n] +=
//                                            -getOutput_error_values()[output_d][output_w][output_h] *
//                                                    this.getInput_values()[j][x_i][y_i] *
//                                                    eta;
//
//                                }
//                            }
//                        }
                    }


                }
            }
        }
    }

    public static void main(String[] args) {
        NetworkBuilder builder = new NetworkBuilder(3, 5, 5);
        ConvLayer convLayer = new ConvLayer(2, 3, 2, 1);
        builder.addLayer(convLayer);

        double[][][] input = ArrayTools.flipWidthAndHeight(new double[][][]
                {
                        {
                                {0, 1, 2, 2, 1},
                                {1, 1, 0, 1, 1},
                                {1, 1, 0, 0, 2},
                                {0, 2, 1, 0, 2},
                                {1, 0, 0, 0, 2}},
                        {
                                {2, 0, 1, 1, 2},
                                {1, 0, 2, 2, 1},
                                {1, 1, 2, 2, 1},
                                {0, 1, 1, 1, 0},
                                {0, 0, 1, 1, 2}},

                        {
                                {1, 1, 0, 2, 2},
                                {2, 2, 1, 1, 0},
                                {0, 0, 0, 1, 1},
                                {0, 0, 2, 0, 2},
                                {2, 0, 1, 1, 0}
                        }
                });


        double[][][] filter1 = ArrayTools.flipWidthAndHeight(new double[][][]
                {
                        {
                                {1, -1, -1},
                                {1, 0, 1},
                                {-1, -1, 1}},
                        {
                                {0, 0, -1},
                                {1, 1, 0},
                                {-1, 0, 0}},

                        {
                                {-1, -1, 1},
                                {-1, -1, 1},
                                {-1, 1, 0}
                        }
                });
        double[][][] filter2 = ArrayTools.flipWidthAndHeight(new double[][][]
                {
                        {
                                {1, 0, 1},
                                {0, -1, 0},
                                {0, -1, -1}},
                        {
                                {-1, 1, 1},
                                {-1, 1, 0},
                                {-1, -1, 0}},

                        {
                                {0, -1, 0},
                                {-1, 1, 0},
                                {1, 1, -1}
                        }
                });

        Network network = builder.buildNetwork();

        convLayer.setFilter(0, filter1, 1);
        convLayer.setFilter(1, filter2, 0);

        Layer.printArray(network.calculate(input));

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
        ConvLayer convLayer = new ConvLayer(channel_amount, filter_size, filter_Stride, padding);
        for (int i = 0; i < this.channel_amount; i++) {
            convLayer.setFilter(i, ArrayTools.copyArray(this.filter[i]), this.bias[i]);
        }
        return convLayer;
    }

}
