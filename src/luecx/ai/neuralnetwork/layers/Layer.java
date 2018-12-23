package luecx.ai.neuralnetwork.layers;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by finne on 27.01.2018.
 */
public abstract class Layer {

    protected int INPUT_DEPTH, INPUT_WIDTH, INPUT_HEIGHT;
    protected int OUTPUT_DEPTH, OUTPUT_WIDTH, OUTPUT_HEIGHT;

    private Layer prev_layer;
    private Layer next_layer;

    protected double[][][] output_values;
    protected double[][][] output_derivative_values;
    protected double[][][] output_error_values;

    public void connectToPreviousLayer(Layer prev_layer) throws Exception {
        this.prev_layer = prev_layer;
        this.prev_layer.next_layer = this;
        this.INPUT_DEPTH = prev_layer.OUTPUT_DEPTH;
        this.INPUT_WIDTH = prev_layer.OUTPUT_WIDTH;
        this.INPUT_HEIGHT = prev_layer.OUTPUT_HEIGHT;

        if (this.OUTPUT_DEPTH == 0 || this.OUTPUT_WIDTH == 0 || this.OUTPUT_HEIGHT == 0) {
            calculateOutputDimensions();
        }

        if (this.OUTPUT_DEPTH < 1 || this.OUTPUT_HEIGHT < 1 || this.OUTPUT_WIDTH < 1) {
            throw new Exception("Bad Dimensions!");
        }

        initializeArrays();

        if(prev_layer instanceof InputLayer){
            prev_layer.initializeArrays();
            prev_layer.on_build();
        }

        on_build();

    }

    private void initializeArrays() {
        output_values = new double[OUTPUT_DEPTH][OUTPUT_WIDTH][OUTPUT_HEIGHT];
        output_derivative_values = new double[OUTPUT_DEPTH][OUTPUT_WIDTH][OUTPUT_HEIGHT];
        output_error_values = new double[OUTPUT_DEPTH][OUTPUT_WIDTH][OUTPUT_HEIGHT];
    }

    protected abstract void on_build() throws Exception;

    protected abstract void calculateOutputDimensions() throws Exception;

    public Layer(int OUTPUT_DEPTH, int OUTPUT_WIDTH, int OUTPUT_HEIGHT) {
        this.OUTPUT_DEPTH = OUTPUT_DEPTH;
        this.OUTPUT_WIDTH = OUTPUT_WIDTH;
        this.OUTPUT_HEIGHT = OUTPUT_HEIGHT;
    }

    public Layer() {

    }

    public abstract void calculate();

    public abstract void backprop_error();

    public abstract void update_weights(double eta);

    public int getINPUT_DEPTH() {
        return INPUT_DEPTH;
    }

    public int getINPUT_WIDTH() {
        return INPUT_WIDTH;
    }

    public int getINPUT_HEIGHT() {
        return INPUT_HEIGHT;
    }

    public int getOUTPUT_DEPTH() {
        return OUTPUT_DEPTH;
    }

    public int getOUTPUT_WIDTH() {
        return OUTPUT_WIDTH;
    }

    public int getOUTPUT_HEIGHT() {
        return OUTPUT_HEIGHT;
    }

    public Layer getPrev_layer() {
        return prev_layer;
    }

    public Layer getNext_layer() {
        return next_layer;
    }

    public double[][][] getOutput_values() {
        return output_values;
    }

    public double[][][] getOutput_derivative_values() {
        return output_derivative_values;
    }

    public double[][][] getOutput_error_values() {
        return output_error_values;
    }

    public double[][][] getInput_values() {
        return prev_layer.output_values;
    }

    public double[][][] getInput_derivative_values() {
        return prev_layer.output_derivative_values;
    }

    public double[][][] getInput_error_values() {
        return prev_layer.output_error_values;
    }

    public void setOutput_values(double[][][] output_values) {
        if (matchingDimensions(output_values))
            this.output_values = output_values;
    }

    public void setOutput_derivative_values(double[][][] output_derivative_values) {
        if (matchingDimensions(output_derivative_values))
            this.output_derivative_values = output_derivative_values;
    }

    public void setOutput_error_values(double[][][] output_error_values) {
        if (matchingDimensions(output_error_values))
            this.output_error_values = output_error_values;
    }

    public static boolean matchingDimensions(Layer layer, double[][][] values) {
        return layer.getOUTPUT_DEPTH() == values.length &&
                layer.getOUTPUT_WIDTH() == values[0].length &&
                layer.getOUTPUT_HEIGHT() == values[0][0].length;
    }

    public boolean matchingDimensions(double[][][] values) {
        return matchingDimensions(this, values);
    }

    public static void printArray(double[][][] array) {
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.CEILING);
        if (array.length == 1 && array[0].length == 1) {
            String s = "";
            for (int i = 0; i < array[0][0].length; i++) {
                s+= String.format("%-8s",df.format(array[0][0][i]))+ " ";
            }
            System.out.println(s);
        } else {
            for (int n = 0; n < array[0][0].length; n++) {
                String s = "";
                for (int i = 0; i < array.length; i++) {
                    for (int k = 0; k < array[0].length; k++) {
                        s+= String.format("%-8s",df.format(array[i][k][n]))+ " ";
                    }
                    s += "       ";
                }
                System.out.println(s);
            }
        }
    }

    public void printOutput() {
        printArray(this.output_values);
    }

    public void printErrorValues() {
        printArray(this.getOutput_error_values());
    }

    public void printOutputDerivative() {
        printArray(this.getOutput_derivative_values());
    }

    public void feedForwardRecursive() {
        this.calculate();
        if (this.next_layer != null) {
            this.next_layer.feedForwardRecursive();
        }
    }

    public void backpropagateErrorRecursive() {
        this.backprop_error();
        if (this.prev_layer != null) {
            this.prev_layer.backpropagateErrorRecursive();
        }
    }

    public void updateWeightsRecursive(double eta) {
        this.update_weights(eta);
        if (this.next_layer != null) {
            this.next_layer.updateWeightsRecursive(eta);
        }
    }

    public abstract Layer clone();

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " {" +
                "  INPUT= " + INPUT_DEPTH +
                ", " + INPUT_WIDTH +
                ", " + INPUT_HEIGHT +
                "  OUTPUT= " + OUTPUT_DEPTH +
                ", " + OUTPUT_WIDTH +
                ", " + OUTPUT_HEIGHT +
                '}';
    }
}
