package luecx.ai.neuralnetwork;


import luecx.ai.neuralnetwork.activation.*;
import luecx.ai.neuralnetwork.data.TrainSet;
import luecx.ai.neuralnetwork.error.CrossEntropy;
import luecx.ai.neuralnetwork.error.ErrorFunction;
import luecx.ai.neuralnetwork.error.MSE;
import luecx.ai.neuralnetwork.layers.*;
import luecx.ai.neuralnetwork.tools.ArrayTools;
import luecx.data.parser.parser.Parser;
import luecx.data.parser.parser.ParserTools;
import luecx.data.parser.tree.Attribute;
import luecx.data.parser.tree.Node;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by finne on 25.01.2018.
 */
public class Network {

    private InputLayer inputLayer;
    private OutputLayer outputLayer;

    public Network(InputLayer inputLayer) {
        Layer cur = inputLayer;
        this.inputLayer = inputLayer;
        while (cur.getNext_layer() != null) {
            cur = cur.getNext_layer();
        }
        if (cur instanceof OutputLayer == false) {
            System.err.println("Network does not have a Output Layer");
            System.exit(-1);
        } else {
            this.outputLayer = (OutputLayer) cur;
        }
    }

    public void printArchitecture() {
        Layer cur = inputLayer;

        while (cur.getNext_layer() != null) {
            System.out.println(cur);
            cur = cur.getNext_layer();
        }
    }

    public Network setErrorFunction(ErrorFunction errorFunction) {
        this.outputLayer.setErrorFunction(errorFunction);
        return this;
    }

    public ErrorFunction getErrorFunction() {
        return this.outputLayer.getErrorFunction();
    }

    public InputLayer getInputLayer() {
        return inputLayer;
    }

    public OutputLayer getOutputLayer() {
        return outputLayer;
    }

    public double[][][] calculate(double[][][] in) {
        if (this.getInputLayer().matchingDimensions(in) == false) return null;
        this.inputLayer.setInput(in);
        this.inputLayer.feedForwardRecursive();
        return getOutput();
    }

    public void backpropagateError(double[][][] expectedOutput) {
        if (this.getOutputLayer().matchingDimensions(expectedOutput) == false) return;
        this.outputLayer.calculateOutputErrorValues(expectedOutput);
        this.outputLayer.backpropagateErrorRecursive();
    }

    public void updateWeights(double eta) {
        this.inputLayer.updateWeightsRecursive(eta);
    }

    public void train(double[][][] input, double[][][] expected, double eta) {
        if (this.getInputLayer().matchingDimensions(input) == false ||
                this.getOutputLayer().matchingDimensions(expected) == false) {
            System.err.println(
                    this.inputLayer.getOUTPUT_DEPTH() + " - " + input.length + "\n" +
                            this.inputLayer.getOUTPUT_WIDTH() + " - " + input[0].length + "\n" +
                            this.inputLayer.getOUTPUT_HEIGHT() + " - " + input[0][0].length + "\n" +
                            this.outputLayer.getOUTPUT_DEPTH() + " - " + expected.length + "\n" +
                            this.outputLayer.getOUTPUT_WIDTH() + " - " + expected[0].length + "\n" +
                            this.outputLayer.getOUTPUT_HEIGHT() + " - " + expected[0][0].length + "\n");
            return;
        }
        this.calculate(input);
        this.backpropagateError(expected);
        this.updateWeights(eta);
    }

    public void train(TrainSet trainSet, int epochs, int batch_size, double fall_of) {
        double e = 0.1;

        long time = System.currentTimeMillis();

        for (int i = 0; i < epochs; i++) {
            ArrayList<TrainSet> trainSets = trainSet.shuffledParts(batch_size);
            int index = 0;
            for (TrainSet t : trainSets) {
                index++;
                for (int k = 0; k < t.size(); k++) {
                    train(t.getInput(k), t.getOutput(k), e * fall_of);
                }
                e = overall_error(t);
                System.out.println(index + "     " + e);
            }
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<< " + i + " >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }

        System.out.println("Passed Time: " + (System.currentTimeMillis() - time) / 1000d + "s");

    }

    public void train(TrainSet trainSet, int batch_size, double eta) {
        ArrayList<TrainSet> trainSets = trainSet.shuffledParts(batch_size);
        int index = 0;
        for (TrainSet t : trainSets) {
            index++;
            for (int k = 0; k < t.size(); k++) {
                train(t.getInput(k), t.getOutput(k), eta);
            }
            //System.out.println(index + "     " + overall_error(t));
        }

    }


    public void save_network(String file) {
        Parser p = new Parser();
        p.create(file);
        Node root = new Node("Network");
        root.addAttribute("loss_function", this.getErrorFunction().getClass().getSimpleName());
        root.addAttribute("input_dimensions", "[" + this.getINPUT_DEPTH() + "," + this.getINPUT_WIDTH() + "," + this.getINPUT_HEIGHT() + "]");
        Layer layer = this.getInputLayer();
        int index = 0;
        while (layer != this.getOutputLayer()) {
            layer = layer.getNext_layer();
            index++;
            Node layer_node = null;

            if (layer instanceof PoolingLayer) {
                layer_node = new Node("PoolingLayer " + index);
                layer_node.addAttribute("pooling_factor", "" + ((PoolingLayer) layer).getPooling_factor());
            } else if (layer instanceof TransformationLayer) {
                layer_node = new Node("TransformationLayer " + index);
            } else if (layer instanceof DenseLayer) {
                layer_node = new Node("DenseLayer " + index);
                layer_node.addAttribute(new Attribute("neurons", "" + layer.getOUTPUT_HEIGHT()));
                layer_node.addAttribute(new Attribute("bias", Arrays.toString(((DenseLayer) layer).getBias())));
                layer_node.addAttribute("activation_function", ((DenseLayer) layer).getActivationFunction().getClass().getSimpleName());
                Node weights = new Node("weights");
                int id = 0;
                for (double[] d : ((DenseLayer) layer).getWeights()) {
                    weights.addAttribute(id + "", Arrays.toString(d));
                    id++;
                }
                layer_node.addChild(weights);
            } else if (layer instanceof ConvLayer) {
                //coming soon
            }

            root.addChild(layer_node);
        }

        p.getContent().addChild(root);

        try {
            p.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Network load_network(String file) {
        Parser p = new Parser();
        try {
            p.load(file);
            Node root = p.getContent().getChild("Network");

            int[] dim = ParserTools.parseIntArray(root.getAttribute("input_dimensions").getValue());
            NetworkBuilder builder = new NetworkBuilder(dim[0], dim[1], dim[2]);

            Layer l = null;
            for (Node n : root.getChilds()) {
                String name = n.getName().split(" ")[0];
                if (name.equals("DenseLayer")) {
                    l = new DenseLayer(Integer.parseInt(n.getAttribute("neurons").getValue()));
                    switch (n.getAttribute("activation_function").getValue()) {
                        case "Sigmoid":
                            ((DenseLayer) l).setActivationFunction(new Sigmoid());
                            break;
                        case "ReLU":
                            ((DenseLayer) l).setActivationFunction(new ReLU());
                            break;
                        case "LeakyReLU":
                            ((DenseLayer) l).setActivationFunction(new LeakyReLU());
                            break;
                        case "Softmax":
                            ((DenseLayer) l).setActivationFunction(new Softmax());
                            break;
                        case "Linear":
                            ((DenseLayer) l).setActivationFunction(new Linear());
                            break;
                        case "TanH":
                            ((DenseLayer) l).setActivationFunction(new TanH());
                            break;
                    }
                    ((DenseLayer) l).setBias(ParserTools.parseDoubleArray(n.getAttribute("bias").getValue()));
                    Node w = n.getChild("weights");
                    double[][] weights = new double[w.getAttributes().size()][];
                    for (Attribute b : w.getAttributes()) {
                        weights[Integer.parseInt(b.getName())] = ParserTools.parseDoubleArray(b.getValue());
                    }
                    ((DenseLayer) l).setWeights(weights);
                } else if (name.equals("TransformationLayer")) {
                    l = new TransformationLayer();
                } else if (name.equals("PoolingLayer")) {
                    l = new PoolingLayer(Integer.parseInt(n.getAttribute("pooling_factor").getValue()));
                }
                builder.addLayer(l);
            }

            Network network = builder.buildNetwork();
            switch (root.getAttribute("loss_function").getValue()) {
                case "CrossEntropy":
                    network.setErrorFunction(new CrossEntropy());
                    break;
                case "MSE":
                    network.setErrorFunction(new MSE());
                    break;
            }
            return network;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public void train(TrainSet trainSet, int iterations, int batch_size, double eta) {
//
//        for (int it = 0; it < iterations; it++) {
//            TrainSet batch = trainSet.extractBatch(batch_size);
//            for (int k = 0; k < batch.size(); k++) {
//                train(batch.getInput(k), batch.getOutput(k), eta);
//            }
//            System.out.println(it + "   " + this.overall_error(batch));
//        }
//    }
//
//    public void train(TrainSet trainSet, int iterations, int batch_size, double init_eta, double factor) {
//
//        double er = init_eta;
//
//        for (int it = 0; it < iterations; it++) {
//            TrainSet batch = trainSet.extractBatch(batch_size);
//            for (int k = 0; k < batch.size(); k++) {
//                train(batch.getInput(k), batch.getOutput(k), er * factor);
//            }
//            System.out.println(it + "   " + (er = this.overall_error(batch)));
//        }
//    }

    public double overall_error(TrainSet trainSet) {
        double t = 0;
        for (int i = 0; i < trainSet.size(); i++) {
            this.calculate(trainSet.getInput(i));
            t += this.getOutputLayer().overall_error(trainSet.getOutput(i));
        }
        return t / (double) trainSet.size();
    }

    public double overall_error(double[][][] in, double[][][] exp) {
        this.calculate(in);
        return this.getOutputLayer().overall_error(exp);
    }

    public double[][][] getOutput() {
        return ArrayTools.copyArray(this.outputLayer.getOutput_values());
    }

    public double[][][] getInput() {
        return ArrayTools.copyArray(this.inputLayer.getOutput_values());
    }

    public static void main(String[] args) {


//        TrainSet trainSet = new TrainSet(1,1,1,1,1,1);
//        for(int i = 0; i < 10; i++){
//            trainSet.addData(ArrayTools.createComplexFlatArray((int)(Math.random() * 1000)),ArrayTools.createComplexFlatArray((int)(Math.random() * 1000)));
//        }
//
//        System.out.println(trainSet);
//
//        for(TrainSet t:trainSet.shuffledParts(3)){
//            System.out.println(t);
//        }


        DenseLayer denseLayer;
        NetworkBuilder builder = new NetworkBuilder(1, 1, 3);
        builder.addLayer(denseLayer = new DenseLayer(3)
                .setActivationFunction(new ReLU())
                .setWeights(new double[][]{
                        {0.1, 0.3, 0.4},
                        {0.2, 0.2, 0.3},
                        {0.3, 0.7, 0.9}})
                .setBias(new double[]{1, 1, 1}));
        builder.addLayer(new DenseLayer(3)
                .setActivationFunction(new Sigmoid())
                .setWeights(new double[][]{
                        {0.2, 0.3, 0.6},
                        {0.3, 0.5, 0.4},
                        {0.5, 0.7, 0.8}})
                .setBias(new double[]{1, 1, 1}));
        builder.addLayer(new DenseLayer(3)
                .setActivationFunction(new Softmax())
                .setWeights(new double[][]{
                        {0.1, 0.3, 0.5},
                        {0.4, 0.7, 0.2},
                        {0.8, 0.2, 0.9}})
                .setBias(new double[]{1, 1, 1}));

        Network network = builder.buildNetwork();
        network.setErrorFunction(new CrossEntropy());

        network.save_network("res/net1");

        Network network1 = Network.load_network("res/net1");
        network1.save_network("res/net2");
//
//        double[][][] in = ArrayTools.createComplexFlatArray(0.1, 0.2, 0.7);
//        double[][][] out = ArrayTools.createComplexFlatArray(1, 0, 0);
//
//        for (int i = 0; i < 1000; i++) {
//            network.train(in, out, 0.003);
//            double e = network.overall_error(in, out);
//            if (Double.isNaN(e)) {
//                network.analyseNetwork();
//                break;
//            } else {
//                System.out.println(e);
//            }
//        }
//
//        network.analyseNetwork();
    }

    public void analyseNetwork() {
        Layer cur = inputLayer;
        System.out.println(cur.getClass().getSimpleName());
        while (cur.getNext_layer() != null) {
            System.out.println("############################################################################");
            System.out.println(cur.getClass().getSimpleName());
            System.out.println("Output:");
            Layer.printArray(cur.getOutput_values());
            System.out.println("Derivative:");
            Layer.printArray(cur.getOutput_derivative_values());
            System.out.println("Error:");
            Layer.printArray(cur.getOutput_error_values());
            if (cur instanceof DenseLayer) {
                System.out.println("Weights:");
                Layer.printArray(new double[][][]{((DenseLayer) cur).getWeights()});
            }
            cur = cur.getNext_layer();
        }
    }

    public void overview() {
        Layer cur = inputLayer;
        int totalparams = 0;
        while (cur.getNext_layer() != null) {
            System.out.println("===========================================================================================");

            int params = 0;
            int connections = 0;
            if (cur instanceof ConvLayer) {
                params += ((ConvLayer) cur).getFilter_size() * ((ConvLayer) cur).getFilter_size() * cur.getINPUT_DEPTH() *
                        cur.getOUTPUT_DEPTH();
                connections += cur.getOUTPUT_DEPTH() * cur.getOUTPUT_HEIGHT() * cur.getOUTPUT_WIDTH() * cur.getINPUT_DEPTH() *
                        ((ConvLayer) cur).getFilter_size() * ((ConvLayer) cur).getFilter_size();
            }
            else if (cur instanceof DenseLayer) {
                params += ((DenseLayer) cur).getWeights().length * ((DenseLayer) cur).getWeights()[0].length;
                connections += params;
            }
            else if (cur instanceof DeconvLayer) {
                params += ((DeconvLayer) cur).getFilter_size() * ((DeconvLayer) cur).getFilter_size() * cur.getINPUT_DEPTH() *
                        cur.getOUTPUT_DEPTH();
                connections += cur.getOUTPUT_DEPTH() * cur.getOUTPUT_HEIGHT() * cur.getOUTPUT_WIDTH() * cur.getINPUT_DEPTH() *
                        ((DeconvLayer) cur).getFilter_size() * ((DeconvLayer) cur).getFilter_size();
            }
            else{
                connections += cur.getINPUT_DEPTH() * cur.getINPUT_WIDTH() * cur.getINPUT_HEIGHT();
            }
            System.out.format("%-40s %-30s %-30s\n", cur.getClass().getSimpleName() +
                    " -> [" + cur.getOUTPUT_DEPTH() +
                    ", " + cur.getOUTPUT_WIDTH() +
                    ", " + cur.getOUTPUT_HEIGHT() +
                    "]", "Connections: " + connections, "Parameters: " + params);


            totalparams += params;
            cur = cur.getNext_layer();
        }
        System.out.println("===========================================================================================");
        System.out.println("Tunable Parameters: " + totalparams);
    }

    /**
     * creates a new network based on this.
     * Every layer is copied until the given layer is reached. This one will be the last layer
     * After that, the Error Function will be applied.
     *
     * @param layer
     * @param function
     * @return
     */
    public Network extractNewNetwork(Layer layer, ErrorFunction function) {
        NetworkBuilder networkBuilder = new NetworkBuilder(this.getINPUT_DEPTH(),
                this.getINPUT_WIDTH(), this.getINPUT_HEIGHT());

        Layer k = this.getInputLayer();
        while (k != layer) {
            k = k.getNext_layer();
            networkBuilder.addLayer(k.clone());
        }
        return networkBuilder.buildNetwork().setErrorFunction(function);
    }

    public Network extractNewNetwork(ErrorFunction errorFunction) {
        return extractNewNetwork(outputLayer.getPrev_layer(), errorFunction);
    }

    public int getINPUT_DEPTH() {
        return inputLayer.getOUTPUT_DEPTH();
    }

    public int getINPUT_WIDTH() {
        return inputLayer.getOUTPUT_WIDTH();
    }

    public int getINPUT_HEIGHT() {
        return inputLayer.getOUTPUT_HEIGHT();
    }

    public int getOUTPUT_DEPTH() {
        return outputLayer.getOUTPUT_DEPTH();
    }

    public int getOUTPUT_WIDTH() {
        return outputLayer.getOUTPUT_WIDTH();
    }

    public int getOUTPUT_HEIGHT() {
        return outputLayer.getOUTPUT_HEIGHT();
    }
}
