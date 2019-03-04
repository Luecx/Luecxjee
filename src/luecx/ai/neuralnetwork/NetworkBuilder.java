package luecx.ai.neuralnetwork;


import luecx.ai.neuralnetwork.activation.ReLU;
import luecx.ai.neuralnetwork.layers.*;
import luecx.ai.neuralnetwork.tools.ArrayTools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by finne on 25.01.2018.
 */
public class NetworkBuilder {

    InputLayer inputLayer;
    ArrayList<Layer> layers = new ArrayList<>();

    public NetworkBuilder(int input_depth, int input_width, int input_height) {
        inputLayer = new InputLayer(input_depth, input_width, input_height);

        inputLayer.setOutput_error_values(new double[input_depth][input_width][input_height]);
        inputLayer.setOutput_derivative_values(new double[input_depth][input_width][input_height]);
        inputLayer.setOutput_values(new double[input_depth][input_width][input_height]);
    }

    public NetworkBuilder addLayer(Layer layer) {
        layers.add(layer);
        return this;
    }

    public void overview(){

        layers.add(0, inputLayer);
        for(Layer cur:layers){
            System.out.println("===========================================================================================");
            System.out.format("%-15s %-30s\n", cur.getClass().getSimpleName(),
                    " -> [" + cur.getOUTPUT_DEPTH() +
                    ", " + cur.getOUTPUT_WIDTH() +
                    ", " + cur.getOUTPUT_HEIGHT() +
                    "]");
        }
        layers.remove(inputLayer);

        System.out.println("===========================================================================================");
        System.out.println("");
    }

    public Network buildNetwork() {
        try{
            Layer b = inputLayer;
            for(Layer l: layers){
                l.connectToPreviousLayer(b);
                b = l;
            }
            OutputLayer outputLayer = new OutputLayer(b);
            outputLayer.connectToPreviousLayer(b);

            return new Network(inputLayer);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
//
//        NetworkBuilder builder = new NetworkBuilder(1,1,5);
//        for(int i = 0; i < 1; i++) {
//            builder.addLayer(
//                    new DenseLayer(2)
//                            .setActivationFunction(new ReLU())
//                            .weightsRange(1,1)
//                            .biasRange(0,1)
//            );
//        }
//        Network network = builder.buildNetwork();
//
//        double[][][] input = ArrayTools.createComplexFlatArray(0.1,0.1,0.1,0.4,0.3);
//        double[][][] expected = ArrayTools.createComplexFlatArray(1,0);
//
//        for(int i = 0; i < 10000; i++){
//            network.train(input,expected,0.3);
//        }
//
//
//        Layer.printArray(network.calculate(input));


        File f1 = new File("res/hautkrebsgesund.jpg");
        File f2 = new File("res/hautkrebsschlecht.jpg");


        BufferedImage a = null;
        BufferedImage b = null;

        try {
            a = ImageIO.read(f1);
            b = ImageIO.read(f2);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double[][][] inputA = new double[3][a.getWidth()][a.getHeight()];
        double[][][] inputB = new double[3][a.getWidth()][a.getHeight()];

        for(int i = 0; i < a.getWidth(); i++) {
            for(int n = 0; n < a.getHeight(); n++) {
                Color c = new Color(a.getRGB(i,n));
                inputA[0][i][n] = (double)c.getRed() / 128 - 1d;
                inputA[1][i][n] = (double)c.getGreen() / 128 - 1d;
                inputA[2][i][n] = (double)c.getBlue() / 128 - 1d;
                c = new Color(b.getRGB(i,n));
                inputB[0][i][n] = (double)c.getRed() / 128 - 1d;
                inputB[1][i][n] = (double)c.getGreen() / 128 - 1d;
                inputB[2][i][n] = (double)c.getBlue() / 128 - 1d;
            }
        }

        double[][][] filter1 = ArrayTools.flipWidthAndHeight(new double[][][]
                {
                        {
                                {-1,-1,-1},
                                {-1,-1,-1},
                                {-1,-1,-1}},
                        {
                                {-1,-1,-1},
                                {-1,-1,-1},
                                {-1,-1,-1}},

                        {
                                {-1,-1,-1},
                                {-1,-1,-1},
                                {-1,-1,-1}
                        }
                });
        double[][][] filter2 = ArrayTools.flipWidthAndHeight(new double[][][]
                {
                        {
                                {1,2,1},
                                {1,-1,0},
                                {0,-1,-1}},
                        {
                                {-1,1,1},
                                {1,1,0},
                                {-1,-1,0}},

                        {
                                {0,-1,0},
                                {-2,1,0},
                                {1,1,-1}
                        }
                });

        ConvLayer convLayer = new ConvLayer(2,3,1,1)
                .setActivationFunction(new ReLU());
        NetworkBuilder builder = new NetworkBuilder(3, a.getWidth(), a.getHeight());
        builder.addLayer(convLayer);

        Network network = builder.buildNetwork();
        convLayer.setFilter(0,filter1, 0);
        convLayer.setFilter(1,filter2, 0);

        double[][][] outputA = network.calculate(inputA);
        double[][][] outputB = network.calculate(inputB);

        System.out.println(inputA+"  " + inputB);
        System.out.println(outputA+"  " + outputB);

        BufferedImage imageAOut1 = new BufferedImage(200,200, 1);
        BufferedImage imageBOut1 = new BufferedImage(200,200, 1);
        BufferedImage imageAOut2 = new BufferedImage(200,200, 1);
        BufferedImage imageBOut2 = new BufferedImage(200,200, 1);

        int offsetA_X = 100;
        int offsetA_Y = 30;


        int offsetB_X = 30;
        int offsetB_Y = 50;


        for(int i = 0; i < imageAOut1.getWidth(); i++){
            for(int n = 0; n < imageAOut1.getHeight(); n++) {
                double fac1 = 10;
                double fac2 = 60;
                double v;
                v = outputA[0][i + offsetA_X][n + offsetA_Y];
                imageAOut1.setRGB(i,n, new Color((int)(fac1 / 1.5 *v),(int)(fac1 / 1.5*v), (int)(fac1 / 1.5*v)).getRGB());

                v = outputA[1][i + offsetA_X][n + offsetA_Y];
                imageAOut2.setRGB(i,n, new Color((int)(fac2*v),(int)(fac2*v), (int)(fac2*v)).getRGB());

                v = outputB[0][i + offsetB_X][n + offsetB_Y];
                imageBOut1.setRGB(i,n, new Color((int)(fac1*v),(int)(fac1*v), (int)(fac1*v)).getRGB());

                v = outputB[1][i + offsetB_X][n + offsetB_Y];
                imageBOut2.setRGB(i,n, new Color((int)(fac2*v),(int)(fac2*v), (int)(fac2*v)).getRGB());
            }
        }

        try {
            ImageIO.write(imageAOut1, "PNG", new File("res/out1.png"));
            ImageIO.write(imageAOut2, "PNG", new File("res/out2.png"));
            ImageIO.write(imageBOut1, "PNG", new File("res/out3.png"));
            ImageIO.write(imageBOut2, "PNG", new File("res/out4.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
