package projects;


import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.NetworkBuilder;
import luecx.ai.neuralnetwork.activation.ReLU;
import luecx.ai.neuralnetwork.activation.Sigmoid;
import luecx.ai.neuralnetwork.activation.TanH;
import luecx.ai.neuralnetwork.data.TrainSet;
import luecx.ai.neuralnetwork.error.CrossEntropy;
import luecx.ai.neuralnetwork.error.MSE;
import luecx.ai.neuralnetwork.layers.DenseLayer;
import luecx.ai.neuralnetwork.tools.ArrayTools;
import luecx.visual.basic.framework.Frame;
import luecx.visual.basic.panels.Graph;

import java.util.Arrays;

public class FunctionApprox {

    public static Network gen_network(){

        return gen_network(1,1);
    }

    public static Network gen_network(int x_values, int fx_values){

        NetworkBuilder builder = new NetworkBuilder(1,1,x_values);
        builder.addLayer(new DenseLayer(20).setActivationFunction(new Sigmoid()));
        builder.addLayer(new DenseLayer(35).setActivationFunction(new Sigmoid()));
        builder.addLayer(new DenseLayer(fx_values).setActivationFunction(new TanH()));

        Network n = builder.buildNetwork();
        n.setErrorFunction(new MSE());

        return n;

    }

    public static TrainSet gen_trainset(double minx, double max_x, double steps){
        TrainSet t = new TrainSet(1,1,1,1,1,1);
        for(double x = minx; x <= max_x; x += steps){
            t.addData(ArrayTools.createComplexFlatArray(x), ArrayTools.createComplexFlatArray(func(x)));
        }
        return t;
    }

    public static double difference_integral(double min_x, double max_x, Network network){
        double d = 0.01;
        double v = 0;
        for(double x = min_x; x <= max_x; x += d){
            v += d * (network.calculate(ArrayTools.createComplexFlatArray(x))[0][0][0] - func(x));
        }
        return v;
    }



    public static void main(String[] args){
        Network net = gen_network();
        TrainSet t = gen_trainset(0,5,0.01);

        net.train(t, 1000,10,1);

        double[][] data1 = new double[t.size()][2];
        double[][] data2 = new double[t.size()][2];


        for(int i = 0; i < t.size(); i++){
            data1[i][0] = t.getInput(i)[0][0][0];
            data2[i][0] = t.getInput(i)[0][0][0];

            data1[i][1] = t.getOutput(i)[0][0][0];
            data2[i][1] = net.calculate(t.getInput(i))[0][0][0];
        }


        Graph g = new Graph(data1, data2);
        Frame f = new Frame(g);

        System.out.println(difference_integral(0,2,net));
    }

    public static double[] func_c(double... values){
        return new double[]{Math.exp(-values[0])*Math.sin(values[0]*5)};
    }

    public static double func(double x){
        return func_c(x)[0];
    }

}
