package luecx.ai.neuralnetwork.activation;

/**
 * Created by finne on 22.01.2018.
 */
public class TanH extends ActivationFunction{

    @Override
    public double activation(double x) {
        return Math.tanh(x);
    }

    @Override
    public double activation_prime(double x) {
        return 1 - Math.tanh(x) * Math.tanh(x);
    }
}
