package luecx.data.mnist;


import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.NetworkBuilder;
import luecx.ai.neuralnetwork.activation.ReLU;
import luecx.ai.neuralnetwork.activation.Softmax;
import luecx.ai.neuralnetwork.data.TrainSet;
import luecx.ai.neuralnetwork.error.CrossEntropy;
import luecx.ai.neuralnetwork.error.MSE;
import luecx.ai.neuralnetwork.layers.ConvLayer;
import luecx.ai.neuralnetwork.layers.DenseLayer;
import luecx.ai.neuralnetwork.layers.PoolingLayer;
import luecx.ai.neuralnetwork.layers.TransformationLayer;
import luecx.ai.neuralnetwork.tools.ArrayTools;

import java.io.File;

/**
 * Created by Luecx on 10.08.2017.
 */
public class Mnist {


    public static void main(String[] args) {

//        NetworkBuilder builder = new NetworkBuilder(1,10,10);
//        builder.addLayer(new TransformationLayer());
//        builder.addLayer(new DenseLayer(30).setActivationFunction(new LeakyReLU()));
//        builder.addLayer(new DenseLayer(10).setActivationFunction(new LeakyReLU()));
//
//        Network network = builder.buildNetwork();
//
//        Layer.printArray(network.calculate(ArrayTools.createRandomArray(1,10,10,0,1)));
//        network.analyseNetwork();
//
//        double[][][] in = ArrayTools.createRandomArray(1,10,10,0,1);
//        double[][][] out = ArrayTools.createRandomArray(1,1,10,0,1);
//
//        for(int i = 0; i < 100; i++){
//            network.train(in,out,0.9);
//        }
//
//        System.out.println("#################################################");
//        Layer.printArray(network.calculate(in));
//        Layer.printArray(out);

//        NetworkBuilder builder = new NetworkBuilder(1, 28, 28);
//        builder.addLayer(new TransformationLayer());
//        builder.addLayer(new DenseLayer(75)
//                .setActivationFunction(new Sigmoid()));
//        builder.addLayer(new DenseLayer(30)
//                .setActivationFunction(new Sigmoid()));
//        builder.addLayer(new DenseLayer(10)
//                .setActivationFunction(new Softmax())
//        );
//        Network network = builder.buildNetwork();
//        network.setErrorFunction(new CrossEntropy());
//
//        TrainSet trainSet = createTrainSet(0,9999);
//        trainData(network, trainSet, 50,10);
//        testTrainSet(network, createTrainSet(10000,11999),10);



        //Julian Abhari
        //Julian Abhari
        //Julian Abhari
        //Julian Abheurari
        //Julian Abhari
        //Julian Abhari

        ConvLayer conv1;

        NetworkBuilder builder = new NetworkBuilder(1, 28, 28);
        builder.addLayer(conv1 = new ConvLayer(4, 5, 1, 0)
                .biasRange(-0.3, 0.3)
                .weightsRange(-0.3, 0.3)
                .setActivationFunction(new ReLU()));
        builder.addLayer(new PoolingLayer(2));
        builder.addLayer(new ConvLayer(6, 5, 1, 0)
                .biasRange(-0.3, 0.3)
                .weightsRange(-0.3, 0.3)
                .setActivationFunction(new ReLU()));
        builder.addLayer(new ConvLayer(6, 3, 1, 0)
                .biasRange(-0.3, 0.3)
                .weightsRange(-0.3, 0.3)
                .setActivationFunction(new ReLU()));
        builder.addLayer(new PoolingLayer(2));
        builder.addLayer(new ConvLayer(6, 3, 1, 0)
                .biasRange(-0.3, 0.3)
                .weightsRange(-0.3, 0.3)
                .setActivationFunction(new ReLU()));
        builder.addLayer(new TransformationLayer());
        builder.addLayer(new DenseLayer(10)
                .setActivationFunction(new Softmax())
        );
        Network network = builder.buildNetwork();
        network.setErrorFunction(new CrossEntropy());

        network.overview();

        TrainSet trainSet = createTrainSet(1,100);
        network.train(trainSet,1000,10,0.01);
        testTrainSet(network, trainSet,1);

    }

    public static TrainSet createTrainSet(int start, int end) {
        TrainSet set = new TrainSet(1, 28, 28, 1, 1, 10);

        try {
            String path = new File("").getAbsolutePath();

            MnistImageFile m = new MnistImageFile("D:\\Informatik\\Programming\\Java Projects\\Luecxjee\\res\\trainImage.idx3-ubyte", "rw");
            MnistLabelFile l = new MnistLabelFile("D:\\Informatik\\Programming\\Java Projects\\Luecxjee\\res\\trainLabel.idx1-ubyte", "rw");

            for (int i = start; i <= end; i++) {
                if (i % 100 == 0) {
                    System.out.println("prepared: " + i);
                }

                double[][] input = new double[28][28];
                double[] output = new double[10];

                output[l.readLabel()] = 1d;
                for (int j = 0; j < 28 * 28; j++) {
                    input[j / 28][j % 28] = (double) m.read() / (double) 256;
                }

                set.addData(new double[][][]{input}, new double[][][]{{output}});
                m.next();
                l.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return set;
    }

    public static void trainData(Network net, TrainSet set, int epochs, int batch_size) {
        net.train(set, epochs, batch_size, 0.1);
    }

    public static void testTrainSet(Network net, TrainSet set, int printSteps) {
        int correct = 0;
        for (int i = 0; i < set.size(); i++) {

            double highest = ArrayTools.indexOfHighestValue(ArrayTools.convertFlattenedArray(net.calculate(set.getInput(i))));
            double actualHighest = ArrayTools.indexOfHighestValue(ArrayTools.convertFlattenedArray(set.getOutput(i)));
            if (highest == actualHighest) {
                correct++;
            }
            if (i % printSteps == 0) {
                System.out.println(i + ": " + (double) correct / (double) (i + 1));
            }
        }
        System.out.println("Testing finished, RESULT: " + correct + " / " + set.size() + "  -> " + (double) correct / (double) set.size() + " %");
    }
}
