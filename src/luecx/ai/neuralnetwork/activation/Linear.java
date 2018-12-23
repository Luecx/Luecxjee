package luecx.ai.neuralnetwork.activation;

/**
 * Created by finne on 26.01.2018.
 */
public class Linear extends ActivationFunction{
    @Override
    public double activation(double x) {
        return x;
    }

    @Override
    public double activation_prime(double x) {
        return 1;
    }
}
