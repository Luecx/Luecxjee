package luecx.ai.neuralnetwork.activation;

/**
 * Created by finne on 22.01.2018.
 */
public class ReLU extends ActivationFunction{

    @Override
    public double activation(double x) {
        return x >= 0 ? x:0;
    }

    @Override
    public double activation_prime(double x) {
        return x > 0 ? 1:0;
    }
}
