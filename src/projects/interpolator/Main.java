package projects.interpolator;

import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.NetworkBuilder;
import luecx.ai.neuralnetwork.layers.DenseLayer;
import luecx.ai.neuralnetwork.tools.ArrayTools;
import luecx.visual.basic.ProgressBar;
import projects.interpolator.minecraft_world_gen.net.morbz.minecraft.blocks.Material;
import projects.interpolator.minecraft_world_gen.net.morbz.minecraft.blocks.SimpleBlock;
import projects.interpolator.minecraft_world_gen.net.morbz.minecraft.level.FlatGenerator;
import projects.interpolator.minecraft_world_gen.net.morbz.minecraft.level.GameType;
import projects.interpolator.minecraft_world_gen.net.morbz.minecraft.level.IGenerator;
import projects.interpolator.minecraft_world_gen.net.morbz.minecraft.level.Level;
import projects.interpolator.minecraft_world_gen.net.morbz.minecraft.world.DefaultLayers;
import projects.interpolator.minecraft_world_gen.net.morbz.minecraft.world.World;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void create_minecraft_block(int size, double threshold, Network network) throws IOException {
        DefaultLayers layers = new DefaultLayers();
        layers.setLayer(0, Material.BEDROCK);
        layers.setLayers(1, 20, Material.STONE);

        IGenerator generator = new FlatGenerator(layers);

        Level level = new Level("3D World Gen", generator);
        level.setGameType(GameType.CREATIVE);
        level.setSpawnPoint(-50, 0, -50);

        World world = new World(level, layers);

        for(int x = 0; x < size; x++){

            ProgressBar.update("Saving", x, size-1, 50);

            for(int y = 0; y < size; y++){
                for(int z = 0; z < size; z++){
                    double v = network.calculate(ArrayTools.createComplexFlatArray(
                            (double) x / (size - 1),
                            (double) y / (size - 1),
                            (double) z / (size - 1)
                    ))[0][0][0];

                    if(Math.abs(v - threshold) < 0.12){
                        System.out.println(":..");
                        world.setBlock(x,y + 30,z, SimpleBlock.GLASS);
                    }
                }
            }
        }

        File f = world.save();
        System.out.println(f.getAbsolutePath());
    }

    public static void main(String[] args) throws Exception {
        BooleanInterpolation interpolation = new BooleanInterpolation(3);

        NetworkBuilder builder = new NetworkBuilder(1,1,3);
        builder.addLayer(new DenseLayer(10));
        builder.addLayer(new DenseLayer(10));
        builder.addLayer(new DenseLayer(1));

        interpolation.setNetwork(builder.buildNetwork());
        interpolation.createRandomPoints(150);

        interpolation.startTraining();

        Thread.sleep(20000);

        interpolation.stopTraining();

        Thread.sleep(8000);

        create_minecraft_block(100, 0.7, interpolation.getNetwork());

        System.exit(-1);
    }


}
