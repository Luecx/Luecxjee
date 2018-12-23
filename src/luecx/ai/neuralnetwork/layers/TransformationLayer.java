package luecx.ai.neuralnetwork.layers;

/**
 * Created by finne on 28.01.2018.
 */
public class TransformationLayer extends Layer {

    @Override
    protected void on_build() throws Exception {

    }

    @Override
    protected void calculateOutputDimensions() throws Exception {
        this.OUTPUT_DEPTH = 1;
        this.OUTPUT_WIDTH = 1;
        this.OUTPUT_HEIGHT = this.getINPUT_HEIGHT() * this.getINPUT_DEPTH() * this.getINPUT_WIDTH();
    }

    private int map(int d, int w, int h) {
        return  (d * (this.getINPUT_WIDTH() * this.getINPUT_HEIGHT()) + w * this.getINPUT_HEIGHT() + h);
    }

    @Override
    public void calculate() {
        for(int i = 0; i < this.getINPUT_DEPTH(); i++) {
            for(int n = 0; n< this.getINPUT_WIDTH(); n++) {
                for(int j = 0;j < this.getINPUT_HEIGHT(); j++) {
                    int index = map(i,n,j);
                    this.output_values[0][0][index] = this.getInput_values()[i][n][j];
                    this.output_derivative_values[0][0][index] = this.getInput_derivative_values()[i][n][j];
                }
            }
        }
    }

    @Override
    public void backprop_error() {

        for(int i = 0; i < this.getINPUT_DEPTH(); i++) {
            for(int n = 0; n< this.getINPUT_WIDTH(); n++) {
                for(int j = 0;j < this.getINPUT_HEIGHT(); j++) {
                    int index = map(i,n,j);
                    this.getPrev_layer().output_error_values[i][n][j] = this.output_error_values[0][0][index];
                }
            }
        }
    }

    @Override
    public Layer clone() {
        return new TransformationLayer();
    }

    @Override
    public void update_weights(double eta) {

    }
}
