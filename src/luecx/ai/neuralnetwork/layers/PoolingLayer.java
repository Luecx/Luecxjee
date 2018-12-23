package luecx.ai.neuralnetwork.layers;

/**
 * Created by finne on 05.05.2018.
 */
public class PoolingLayer extends Layer {

    private int pooling_factor;

    public PoolingLayer(int pooling_factor) {
        super();
        this.pooling_factor = pooling_factor;
    }

    @Override
    protected void on_build() throws Exception {

    }

    @Override
    protected void calculateOutputDimensions() throws Exception {
        this.OUTPUT_DEPTH = this.INPUT_DEPTH;
        this.OUTPUT_WIDTH = this.INPUT_WIDTH / pooling_factor + (this.INPUT_WIDTH % pooling_factor > 0 ? 1 : 0);
        this.OUTPUT_HEIGHT = this.INPUT_HEIGHT / pooling_factor + (this.INPUT_HEIGHT % pooling_factor > 0 ? 1 : 0);

    }

    @Override
    public void calculate() {


        for (int i = 0; i < this.OUTPUT_DEPTH; i++) {
            for (int n = 0; n < this.OUTPUT_WIDTH; n++) {
                for (int k = 0; k < this.OUTPUT_HEIGHT; k++) {
                    this.output_values[i][n][k] = 0;
                    this.output_derivative_values[i][n][k] = 0;
                }
            }
        }
        for (int i = 0; i < this.OUTPUT_DEPTH; i++) {
            for (int n = 0; n < this.OUTPUT_WIDTH; n++) {
                for (int k = 0; k < this.OUTPUT_HEIGHT; k++) {

                    double max = 0;
                    double d = 0;

                    for (int x = 0; x < pooling_factor; x++) {
                        for (int y = 0; y < pooling_factor; y++) {

                            int x_i = n * pooling_factor + x;
                            int y_i = k * pooling_factor + y;

                            if (x_i < this.INPUT_WIDTH && y_i < this.INPUT_HEIGHT) {
                                if (getInput_values()[i][x_i][y_i] > max) {
                                    max = getInput_values()[i][x_i][y_i];
                                    d = getInput_derivative_values()[i][x_i][y_i];
                                }
                            }
                        }
                    }

                    output_values[i][n][k] = max;
                    output_derivative_values[i][n][k] = d;

                }
            }
        }
    }

    @Override
    public void backprop_error() {
        for (int i = 0; i < this.OUTPUT_DEPTH; i++) {
            for (int n = 0; n < this.OUTPUT_WIDTH; n++) {
                for (int k = 0; k < this.OUTPUT_HEIGHT; k++) {

                    for (int x = 0; x < pooling_factor; x++) {
                        for (int y = 0; y < pooling_factor; y++) {

                            int x_i = n * pooling_factor + x;
                            int y_i = k * pooling_factor + y;

                            if (x_i < this.INPUT_WIDTH && y_i < this.INPUT_HEIGHT) {
                                if (getInput_values()[i][x_i][y_i] == output_values[i][n][k]) {
                                    this.getPrev_layer().output_error_values[i][x_i][y_i] = output_error_values[i][n][k];
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void update_weights(double eta) {

    }

    @Override
    public Layer clone() {
        return new PoolingLayer(pooling_factor);
    }

    public int getPooling_factor() {
        return pooling_factor;
    }

}
