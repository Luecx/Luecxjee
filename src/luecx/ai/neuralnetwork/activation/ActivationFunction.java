package luecx.ai.neuralnetwork.activation;

/**
 * Created by finne on 22.01.2018.
 */
public abstract class ActivationFunction {

    public abstract double activation(double x);

    public abstract double activation_prime(double x);

    public void apply(double[][][] output, double[][][] output_derivative) {
        for(int i = 0; i < output.length; i++){
            for(int n = 0; n < output[0].length; n++) {
                for(int j = 0; j < output[0][0].length; j++){
                    output_derivative[i][n][j] = activation_prime(output[i][n][j]);
                    output[i][n][j] = activation(output[i][n][j]);
                }
            }
        }
    }
}
