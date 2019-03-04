package projects;


import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.NetworkBuilder;
import luecx.ai.neuralnetwork.data.TrainSet;
import luecx.ai.neuralnetwork.error.MSE;
import luecx.ai.neuralnetwork.layers.*;
import luecx.ai.neuralnetwork.tools.ArrayTools;
import luecx.data.image.Loader;

import java.util.ArrayList;

public class MagicPainter {

    public static Network createNetwork(int d, int w, int h) {
        NetworkBuilder builder = new NetworkBuilder(d, w, h);
        builder.addLayer(new ConvLayer(20, 5, 1, 2));
        builder.addLayer(new ConvLayer(12   , 5, 1, 2));



        Network net = builder.buildNetwork();
        net.setErrorFunction(new MSE());
        return net;
    }

    public static Network downSampler(int d, int w, int h, int fac) {
        NetworkBuilder builder = new NetworkBuilder(d, w, h);
        double[][][][] weights = new double[d][d][fac][fac];
        for (int k = 0; k < d; k++) {
            for (int x = 0; x < fac; x++) {
                for (int y = 0; y < fac; y++) {
                    weights[k][k][x][y] = 1d / (d * fac * fac);
                }
            }
        }
        ConvLayer convLayer;
        builder.addLayer(convLayer = new ConvLayer(d, fac, fac, 0));
        Network network = builder.buildNetwork();
        for(int i = 0; i < d; i++){
            convLayer.setFilter(i, weights[i], 0);
        }
        return network;
    }

    public static double[][][] evaluateGramMatrix(ConvLayer convLayer) {
        double[][][] out = new double[1][convLayer.getOUTPUT_WIDTH()][convLayer.getOUTPUT_HEIGHT()];
        for (int x = 0; x < convLayer.getOUTPUT_WIDTH(); x++) {
            for (int y = 0; y < convLayer.getOUTPUT_HEIGHT(); y++) {
                double v = 1;
                for (int i = 0; i < convLayer.getOUTPUT_DEPTH(); i++) {
                    v *= convLayer.getOutput_values()[i][x][y];
                }
                out[0][x][y] = v;
            }
        }
        return out;
    }

    public static void averageValue(double[][][] input, double[][][] delta) {
        for (int d = 0; d < input.length; d++) {
            for (int x = 0; x < input[0].length; x++) {
                for (int y = 0; y < input[0][0].length; y++) {
                    input[d][x][y] += (delta[d][x][y] - input[d][x][y]) / 2;
                }
            }
        }
    }

    public static void tweakValues(double[][][] input, double[][][] delta, double factor) {
        for (int d = 0; d < input.length; d++) {
            for (int x = 0; x < input[0].length; x++) {
                for (int y = 0; y < input[0][0].length; y++) {
                    input[d][x][y] += delta[d][x][y] * factor;
                }
            }
        }
    }

    public static double[][][] drawMap(Network k,double[][][] expected) {

        double[][][] tweakInput = ArrayTools.createRandomArray(k.getINPUT_DEPTH(), k.getINPUT_WIDTH(), k.getINPUT_HEIGHT(),0.2,0.2);

        TrainSet set = new TrainSet(tweakInput.length, tweakInput[0].length, tweakInput[0][0].length,
                expected.length, expected[0].length, expected[0][0].length);
        set.addData(tweakInput, expected);
        double e = 1;
        for (int i = 0; i < 100; i++) {
            double v = k.train(set,1,1,0);
            if(v < e){
                e = v;
            }else{
                return tweakInput;
            }
            System.out.println(i + " Error: " + e);
            tweakValues(tweakInput, k.getInputLayer().getOutput_error_values(), -1);
        }

        return tweakInput;
    }

    public static double[][][] generateContentMap(Network k, double[][][] input){
        return drawMap(k, k.calculate(input));
    }

    public static ArrayList<double[][][]> generateStyleMaps(Network k, double[][][] input) {
        k.calculate(input);

        ArrayList<Layer> layers = new ArrayList<>();
        Layer c = k.getInputLayer();
        while (c != k.getOutputLayer()) {
            c = c.getNext_layer();
            if (c instanceof ConvLayer) {
                layers.add(c);
            }
        }
        ArrayList<double[][][]> out = new ArrayList<>();
        for(Layer l:layers){
            double[][][] exp = evaluateGramMatrix((ConvLayer) l);
            out.add(drawMap(k.extractNewNetwork(l, new MSE()), exp));
        }
        return out;
    }

    public static double[][][] generateContentLoss(Network k, double[][][] input){
        return k.calculate(input);
    }

    public static ArrayList<double[][][]> generateStyleLoss(Network k, double[][][] input) {
        k.calculate(input);

        ArrayList<Layer> layers = new ArrayList<>();
        Layer c = k.getInputLayer();
        while (c != k.getOutputLayer()) {
            c = c.getNext_layer();
            if (c instanceof ConvLayer) {
                layers.add(c);
            }
        }
        ArrayList<double[][][]> out = new ArrayList<>();
        for(Layer l:layers){
            double[][][] exp = evaluateGramMatrix((ConvLayer) l);
            out.add(exp);
        }
        return out;
    }



    public static double[][][] generateImage(Network k, double[][][] content, double[][][] style){
        double[][][] c = ArrayTools.copyArray(content);
        tweakValues(c, style, 1);
        return drawMap(k, c);
    }

    public static void main(String[] args) {

        double[][][] raw = Loader.loadImage_3d("res/images/TajMahal.png");
        double[][][] raw2 = Loader.loadImage_3d("res/images/Pikasso.png");

        Network sampler = downSampler(raw.length, raw[0].length, raw[0][0].length, 8);
        Loader.writeImage("res/images/TajMahal250.png", sampler.calculate(raw));

        Network network = createNetwork(sampler.getOUTPUT_DEPTH(), sampler.getOUTPUT_WIDTH(), sampler.getOUTPUT_HEIGHT());
        network.overview();

        double[][][] expected1 = network.calculate(sampler.calculate(raw));
        double[][][] expected2 = network.calculate(sampler.calculate(raw2));

        averageValue(expected1,expected2);

        double[][][] drawn1 = drawMap(network, expected1);

        Loader.writeImage("res/images/TajMahal250_drawn_2.png", drawn1);


    }

//    public static void main(String[] args) {
//
//        Network sampler = downSampler(1, 1024, 1024, 4);
////
////
////        double[][][] input = sampler.calculate(Loader.loadImage_3d_bw("res/monalisa.png"));
////        //double[][][] input = ArrayTools.createRandomArray(1,10,10,0,1);
////
////        ConvLayer convLayer = null;
////        DeconvLayer deconvLayer = null;
////        NetworkBuilder builder = new NetworkBuilder(input.length,input[0].length,input[0][0].length);
////        builder.addLayer(convLayer = new ConvLayer(8,3,1,0));
////        builder.addLayer(deconvLayer = new DeconvLayer(8,3,1));
////        Network network = builder.buildNetwork();
////
////        double[][][] output = network.calculate(input);
////        double[][][] vals = drawMap(network, output);
////
////
////
////        Loader.writeImage("perfect", input);
////        Loader.writeImage("drawn", vals);
////        Loader.writeImage("out", output);
////        Loader.writeImage("deconv_loss", deconvLayer.getOutput_error_values());
////        Loader.writeImage("conv_loss", convLayer.getOutput_error_values());
//
//
//
//
////        Network k = createNetwork(3, 1024, 1024);
////
////        k.printArchitecture();
////
////        double[][][] style_input = sampler.calculate(
////                Loader.loadImage_3d("res/bubbles.png")
////        );
////        double[][][] content_input = sampler.calculate(
////                Loader.loadImage_3d("res/monalisa.png"));
////
////
////
////
////        double[][][] contentLoss = generateContentLoss(k, content_input);
////        double[][][] contentLoss2 = generateContentLoss(k, style_input);
////
////        MagicPainter.tweakValues(contentLoss, contentLoss2, 0);
////
////        double[][][] finalImage = drawMap(k, contentLoss);
////        Loader.writeImage("final", finalImage);
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////        double[][][] contentMap = generateContentMap(k, content_input);
////        List<double[][][]> styles = generateStyleMaps(k, style_input);
////
////        Loader.writeImage("content" + Math.random(), contentMap);
////        for(double[][][] style:styles){
////            Loader.writeImage("style" + Math.random(), style);
////            //tweakValues(contentMap, style, 0.3d/styles.size());
////        }
//
////        double[][][] fin = drawMap(k, contentMap);
////        Loader.writeImage("final", fin);
//
//
//
////
////        Loader.writeImage("map", tweakInput);
////        Loader.writeImage("grammatrix",
////                evaluateGramMatrix((ConvLayer) k.getInputLayer().getNext_layer()));
////        tweakValues(tweakInput,
////                sampler.calculate(Loader.loadImage_3d_bw("res/missile.png")),
////                2);
////        Loader.writeImage("magic", tweakInput);
//
//
//    }

}
