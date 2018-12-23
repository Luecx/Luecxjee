package luecx.ai.neuralnetwork.activation;

/**
 * Created by finne on 22.01.2018.
 */
public class LeakyReLU extends ActivationFunction{

    @Override
    public double activation(double x) {
        return x >= 0 ? x:x * 0.01;
    }

    @Override
    public double activation_prime(double x) {
        return x >= 0 ? 1:0.01;
    }

}
