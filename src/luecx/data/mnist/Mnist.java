package luecx.data.mnist;


import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.NetworkBuilder;
import luecx.ai.neuralnetwork.activation.ReLU;
import luecx.ai.neuralnetwork.activation.Sigmoid;
import luecx.ai.neuralnetwork.data.TrainSet;
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


    public static void main(String[] args) throws InterruptedException {
        NetworkBuilder builder = new NetworkBuilder(1, 28, 28);
        builder.addLayer(new ConvLayer(5, 5, 1, 0)
                .setActivationFunction(new ReLU()));
        builder.addLayer(new ConvLayer(8, 5, 1, 0)
                .setActivationFunction(new ReLU()));
        builder.addLayer(new PoolingLayer(2));
        builder.addLayer(new ConvLayer(5, 5, 1, 0)
                .setActivationFunction(new ReLU()));;
        builder.addLayer(new TransformationLayer());
        builder.addLayer(new DenseLayer(120)
                .setActivationFunction(new ReLU())
        );
        builder.addLayer(new DenseLayer(100)
                .setActivationFunction(new ReLU())
        );
        builder.addLayer(new DenseLayer(10)
                .setActivationFunction(new Sigmoid())
        );
        Network net = builder.buildNetwork();
        net.setErrorFunction(new MSE());

        TrainSet train = createTrainSet(0,100);
        net.train(train, 100,100,0.5);


        TrainSet val = createTrainSet(100,200);
        testTrainSet(net, val, 10);
        net.save_network("res/mnist_network_conv2.txt");


//        Network net = Network.load_network("res/mnist_network_conv2.txt");
//        testTrainSet(net, createTestSet(), 1);
//        net.calculate(createTrainSet(0,1).getInput(0));
//
//        new Frame(new NetworkPanel(net));

    }

    public static TrainSet createTrainSet(){
        TrainSet set = new TrainSet(1, 28, 28, 1, 1, 10);

        try {
            String path = new File("").getAbsolutePath();

            MnistImageFile m = new MnistImageFile(path + "/res/train-images.idx3-ubyte", "rw");
            MnistLabelFile l = new MnistLabelFile(path + "/res/train-labels.idx1-ubyte", "rw");

            int i = 0;
            while(true) {
                try{
                    i++;
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
                }catch (Exception e){
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return set;
    }
    public static TrainSet createTestSet(){
        TrainSet set = new TrainSet(1, 28, 28, 1, 1, 10);

        try {
            String path = new File("").getAbsolutePath();

            MnistImageFile m = new MnistImageFile(path + "/res/t10k-images.idx3-ubyte", "r");
            MnistLabelFile l = new MnistLabelFile(path + "/res/t10k-labels.idx1-ubyte", "r");


            int i = 0;
            while(true) {
                try{
                    i++;
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
                }catch (Exception e){
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return set;
    }

    public static TrainSet createTrainSet(int start, int end) {
        TrainSet set = new TrainSet(1, 28, 28, 1, 1, 10);

        try {
            String path = new File("").getAbsolutePath();

            MnistImageFile m = new MnistImageFile("D:\\Informatik\\Programming\\Java Projects\\Luecxjee\\res\\trainImage.idx3-ubyte", "rw");
            MnistLabelFile l = new MnistLabelFile("D:\\Informatik\\Programming\\Java Projects\\Luecxjee\\res\\trainLabel.idx1-ubyte", "rw");

            for(int i = 0; i < start; i++){
                m.next();
                l.next();
            }

            for (int i = start; i < end; i++) {
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
