package luecx.ai.neuralnetwork.activation;

/**
 * Created by finne on 07.05.2018.
 */
public class Softmax extends ActivationFunction {

    @Override
    public double activation(double x) {
        return Math.exp(x);
    }

    @Override
    public double activation_prime(double x) {
        return 0;
    }

    @Override
    public void apply(double[][][] output, double[][][] output_derivative) {
        double max = 0;
        for(int i = 0; i < output.length; i++){
            for(int n = 0; n < output[0].length; n++) {
                for(int j = 0; j < output[0][0].length; j++){
                    output[i][n][j] = activation(output[i][n][j]);
                    max += output[i][n][j];
                }
            }
        }
        for(int i = 0; i < output.length; i++){
            for(int n = 0; n < output[0].length; n++) {
                for(int j = 0; j < output[0][0].length; j++){
                    output_derivative[i][n][j] = (output[i][n][j] * (max - output[i][n][j])) / (max * max);
                    output[i][n][j] /= max;
                }
            }
        }


    }
}
