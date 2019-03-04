package projects;

import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.NetworkBuilder;
import luecx.ai.neuralnetwork.layers.ConvLayer;
import luecx.ai.neuralnetwork.layers.DenseLayer;
import luecx.ai.neuralnetwork.layers.PoolingLayer;
import luecx.ai.neuralnetwork.layers.TransformationLayer;

public class RandomStuff {

    public static void main(String[] args){

        int d = 3;
        int w = 100;
        int h = 100;

        NetworkBuilder builder = new NetworkBuilder(d,w,h);
        builder.addLayer(new ConvLayer(10,3,1,0));
        builder.addLayer(new PoolingLayer(2));
        builder.addLayer(new ConvLayer(10,3,1,0));
        Network network = builder.buildNetwork();
        network.overview();

        NetworkBuilder builder2 = new NetworkBuilder(d,w,h);
        builder2.addLayer(new TransformationLayer());
        builder2.addLayer(new DenseLayer(100));
        builder2.addLayer(new DenseLayer(20000));

        Network network2 = builder2.buildNetwork();
        network2.overview();

        network.speed_check(30);
        network2.speed_check(30);

    }

}
