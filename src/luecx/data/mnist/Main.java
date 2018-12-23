package luecx.data.mnist;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    MnistSet trainingSet ;
    MnistSet testSet ;

    Main(){
        ClassLoader classLoader = getClass().getClassLoader();
        File testLabelfile = new File(classLoader.getResource("t10k-labels.idx1-ubyte").getFile());
        File testImagefile = new File(classLoader.getResource("t10k-images.idx3-ubyte").getFile());

        File trainLabelfile = new File(classLoader.getResource("train-labels.idx1-ubyte").getFile());
        File trainImagefile = new File(classLoader.getResource("train-images.idx3-ubyte").getFile());

        testSet = new MnistSet(testImagefile,testLabelfile);
        trainingSet = new MnistSet(trainImagefile,trainLabelfile);

    }
    public static void main(String[] args) {
        System.out.println("Hello World!");
        new Main();

    }
}
