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
import luecx.visual.basic.ProgressBar;

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

    public double train(TrainSet trainSet, int epochs, int batch_size, double fall_of) {
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
        return e;
    }

    public double train(TrainSet trainSet, int batch_size, double eta) {
        ArrayList<TrainSet> trainSets = trainSet.shuffledParts(batch_size);
        int index = 0;
        double e = 0.1;
        for (TrainSet t : trainSets) {
            index++;
            for (int k = 0; k < t.size(); k++) {
                train(t.getInput(k), t.getOutput(k), eta);
            }
            System.out.println(index + "     " + (e = overall_error(t)));
        }
        return e;
    }

    public double validate_binary(TrainSet trainSet){
        if(this.getOUTPUT_DEPTH() * this.getOUTPUT_WIDTH() != 1){
            System.err.println("Binary validation is only available for 1 dimensional outputs");
        }
        int correct = 0;
        for(int i = 0; i < trainSet.size(); i++){
            this.calculate(trainSet.getInput(i));


            if(this.getOUTPUT_HEIGHT() == 1){
                if(this.getOutput()[0][0][0] > 0.5 && trainSet.getOutput(i)[0][0][0] > 0.5 ||
                        this.getOutput()[0][0][0] < 0.5 && trainSet.getOutput(i)[0][0][0] < 0.5){
                    correct ++;
                }
            }
            else{
                if(ArrayTools.indexOfHighestValue(this.getOutput()[0][0]) == ArrayTools.indexOfHighestValue(trainSet.getOutput(i)[0][0])){
                    correct++;
                }
            }

            ProgressBar.update("binary validation", i+1, trainSet.size(), 50, (""+correct / (double) (i + 1) +"      ").substring(0,5));
        }
        return correct / (double)trainSet.size();
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
                layer_node = new Node("ConvLayer " + index);
                layer_node.addAttribute("channels", ((ConvLayer) layer).getChannel_amount()+"");
                layer_node.addAttribute("filter_size", ((ConvLayer) layer).getFilter_size()+"");
                layer_node.addAttribute("input_depth", ((ConvLayer) layer).getINPUT_DEPTH()+"");
                layer_node.addAttribute("filter_stride", ((ConvLayer) layer).getFilter_Stride()+"");
                layer_node.addAttribute("padding", ((ConvLayer) layer).getPadding()+"");
                layer_node.addAttribute("bias", Arrays.toString(((ConvLayer) layer).getBias()));
                layer_node.addAttribute("activation_function", ((ConvLayer) layer).getActivationFunction().getClass().getSimpleName());

                Node filter = new Node("filter");
                for(int i = 0; i < ((ConvLayer) layer).getChannel_amount(); i++){
                    filter.addAttribute("" + i, Arrays.toString(ArrayTools.convertComplexToFlatArray(((ConvLayer) layer).getFilter(i))));
                }
                layer_node.addChild(filter);
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
                } else if (name.equals("ConvLayer")) {
                    l = new ConvLayer(
                            Integer.parseInt(n.getAttribute("channels").getValue()),
                            Integer.parseInt(n.getAttribute("filter_size").getValue()),
                            Integer.parseInt(n.getAttribute("filter_stride").getValue()),
                            Integer.parseInt(n.getAttribute("padding").getValue()));

                    switch (n.getAttribute("activation_function").getValue()) {
                        case "Sigmoid":
                            ((ConvLayer) l).setActivationFunction(new Sigmoid());
                            break;
                        case "ReLU":
                            ((ConvLayer) l).setActivationFunction(new ReLU());
                            break;
                        case "LeakyReLU":
                            ((ConvLayer) l).setActivationFunction(new LeakyReLU());
                            break;
                        case "Softmax":
                            ((ConvLayer) l).setActivationFunction(new Softmax());
                            break;
                        case "Linear":
                            ((ConvLayer) l).setActivationFunction(new Linear());
                            break;
                        case "TanH":
                            ((ConvLayer) l).setActivationFunction(new TanH());
                            break;
                    }

                    Node w = n.getChild("filter");
                    for(int i = 0; i < ((ConvLayer) l).getChannel_amount(); i++){
                        ((ConvLayer) l).setFilter(i,
                            ArrayTools.convertFlatToComplexArray(
                                    ParserTools.parseDoubleArray(w.getAttribute(i+"").getValue()),
                                    Integer.parseInt(n.getAttribute("input_depth").getValue()),
                                    Integer.parseInt(n.getAttribute("filter_size").getValue()),
                                    Integer.parseInt(n.getAttribute("filter_size").getValue())),0
                                );
                    }
                    ((ConvLayer) l).setBias(ParserTools.parseDoubleArray(n.getAttribute("bias").getValue()));
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


        NetworkBuilder builder = new NetworkBuilder(3, 7, 7);
        builder.addLayer(new ConvLayer(3,3,1,0));
        builder.addLayer(new ConvLayer(3,3,1,0));
        builder.addLayer(new TransformationLayer());
        builder.addLayer(new DenseLayer(10));
        Network network = builder.buildNetwork();
        network.setErrorFunction(new MSE());

        network.save_network("res/net1");
        Network network1 = Network.load_network("res/net1");

        double[][][] in = ArrayTools.createRandomArray(3,7,7,0,1);
        Layer.printArray(network.calculate(in));
        Layer.printArray(network1.calculate(in));
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
                connections += cur.getINPUT_DEPTH() * cur.getINPUT_HEIGHT() * cur.getINPUT_WIDTH() * cur.getOUTPUT_DEPTH() *
                        ((DeconvLayer) cur).getFilter_size() * ((DeconvLayer) cur).getFilter_size();
            }
            else{
                connections += cur.getINPUT_DEPTH() * cur.getINPUT_WIDTH() * cur.getINPUT_HEIGHT();
            }
            System.out.format("%-15s %-25s %-30s %-30s\n", cur.getClass().getSimpleName(),
                    " -> [" + cur.getOUTPUT_DEPTH() +
                    ", " + cur.getOUTPUT_WIDTH() +
                    ", " + cur.getOUTPUT_HEIGHT() +
                    "]", "Connections: " + connections, "Parameters: " + params);


            totalparams += params;
            cur = cur.getNext_layer();
        }
        System.out.println("===========================================================================================");
        System.out.println("Tunable Parameters: " + totalparams);
        System.out.println("");

    }

    public void speed_check(int samples){
        double[][][] in = ArrayTools.createRandomArray(getINPUT_DEPTH(),getINPUT_WIDTH(), getINPUT_HEIGHT(),0,1);
        double[][][] out = ArrayTools.createRandomArray(getOUTPUT_DEPTH(),getOUTPUT_WIDTH(), getOUTPUT_HEIGHT(),0,1);

        long time = System.nanoTime();
        for(int i = 0; i < samples; i++){
            this.train(in, out, 0);
            ProgressBar.update("speed_check:", i + 1,samples,50,  ((System.nanoTime()-time) / (Math.pow(10,6) * (i + 1))+"   ").substring(0,6) + "ms");
        }
        System.out.println("");
        System.out.println("speed per sample: " + (System.nanoTime() - time) / Math.pow(10,6) / samples + " ms");        System.out.println("");
        System.out.println("");

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
