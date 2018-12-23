package luecx.ai.neuralnetwork.error;


import luecx.ai.neuralnetwork.layers.OutputLayer;

/**
 * Created by finne on 22.01.2018.
 */
public class MSE extends ErrorFunction{
    
    @Override
    public double overall_error(OutputLayer outputLayer, double[][][] expected) {

        double v = 0;
        double c = 0;

        for(int i = 0; i < outputLayer.getOutput_values().length; i++){
            for(int n = 0; n < outputLayer.getOutput_values()[0].length; n++) {
                for(int j = 0; j < outputLayer.getOutput_values()[0][0].length; j++){
                    v += (outputLayer.getOutput_values()[i][n][j] - expected[i][n][j]) * (outputLayer.getOutput_values()[i][n][j] - expected[i][n][j]);
                    //System.out.println(c + "   " + outputLayer.getOutput_values()[i][n][j] + "  "  + expected[i][n][j] +   "  " + (outputLayer.getOutput_values()[i][n][j] - expected[i][n][j]));
                    c++;
                }
            }
        }

        //System.out.println(v + "  " + c );

        return v / (2 * c);
    }

    @Override
    public void apply(OutputLayer outputLayer, double[][][] expected) {

        double[][][] output = outputLayer.getOutput_values();
        double[][][] output_derivative = outputLayer.getOutput_derivative_values();
        double[][][] error_signals = outputLayer.getOutput_error_values();
        for(int i = 0; i < output.length; i++){
            for(int n = 0; n < output[0].length; n++) {
                for(int j = 0; j < output[0][0].length; j++){
                    //System.out.println(i + "   " + outputLayer.getOutput_values()[i][n][j] + "  "  + expected[i][n][j] +   "  " + (outputLayer.getOutput_values()[i][n][j] - expected[i][n][j]));
                    error_signals[i][n][j] = output_derivative[i][n][j] * (output[i][n][j] - expected[i][n][j]);
                }
            }
        }
    }

}
