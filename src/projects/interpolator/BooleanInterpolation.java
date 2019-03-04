package projects.interpolator;

import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.data.TrainSet;
import luecx.ai.neuralnetwork.tools.ArrayTools;
import luecx.math.values.tensor.Vector;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BooleanInterpolation {



    private class Trainer extends Thread{

        private BooleanInterpolation bi;
        private boolean training = false;

        public Trainer(BooleanInterpolation bi) {
            this.bi = bi;
            this.start();
        }

        public void run(){
            while(!this.isInterrupted()){
                try{
                    if(training && bi.trainSet != null && bi.network != null){
                        for(int i = 0; i < 10000; i++){
                            network.train(bi.trainSet, 10, 0.03);
                        }
                        System.out.println(network.overall_error(bi.trainSet));

                    }

                    if(bi.panel != null){
                        if (this.bi.network != null) {
                            for (int x = 0; x < res; x++) {
                                for (int y = 0; y < res; y++) {
                                    double z = panel.getjSlider1().getValue() / 100d;
                                    float v = (float) network.calculate(ArrayTools.createComplexFlatArray(
                                            x / (double) (res - 1), y / (double) (res - 1), z))[0][0][0];
                                    image.setRGB(x, y, new Color(v, 1 - v, 0, 1).getRGB());
                                }
                            }
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    this.interrupt();
                }
            }
            this.interrupt();
        }

        public void startTraining(){
            training = true;
        }

        public void stopTraining(){
            training = false;
        }
    }


    int res = 300;

    private BufferedImage image;
    private TrainSet trainSet;
    private Network network;
    private Trainer trainer;
    private InterpolationPanel panel;
    private int dimension;


    public BooleanInterpolation(int dimension, InterpolationPanel panel) {
        this.dimension = dimension;
        this.trainer = new Trainer(this);
        this.panel = panel;
        this.image = new BufferedImage(res, res, 1);
        this.trainSet = new TrainSet(1,1,dimension,1,1,1);
    }

    public BooleanInterpolation(int dimension) {
        this.dimension = dimension;
        this.trainer = new Trainer(this);
        this.trainSet = new TrainSet(1,1,dimension,1,1,1);

        this.panel = null;
        this.image = null;
    }

    public void createRandomPoints(int amount) {
        for(int i = 0; i < amount; i++){
            System.out.println(this.dimension);
            Vector v = new Vector(this.dimension);
            for(int k = 0; k < v.getSize(); k++){
                v.setValue(Math.random(), k);
            }
            boolean t = Math.random() > 0.5 ? true:false;
            this.addPoint(v, t);
        }

    }

    public TrainSet getTrainSet() {
        return trainSet;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public int getDimension() {
        return dimension;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void startTraining(){
        trainer.startTraining();
    }

    public void stopTraining(){
        trainer.stopTraining();
    }

    public void addPoint(Vector v, boolean value){
        if(v.getSize() != dimension) {
            System.out.println("wrong dim");
            return;
        };
        trainSet.addData(
                ArrayTools.createComplexFlatArray(v.getValues()),
                ArrayTools.createComplexFlatArray(value ? 1:0));
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
