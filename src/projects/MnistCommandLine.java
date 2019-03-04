package projects;

import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.NetworkBuilder;
import luecx.ai.neuralnetwork.layers.DenseLayer;
import luecx.server.protocol.commands.Argument;
import luecx.server.protocol.commands.Command;
import luecx.server.protocol.commands.CommandDataBase;
import luecx.server.protocol.commands.Executable;
import luecx.server.tcp.connection.Connection;

import java.util.Scanner;

public class MnistCommandLine {

    private CommandDataBase commandDataBase = new CommandDataBase();
    private CommandDataBase architectDataBase = new CommandDataBase();


    private boolean construction = false;
    private Network network;
    private NetworkBuilder builder;

    public MnistCommandLine() {
        this.commandDataBase.registerCommand(new Command("create_network")
                .registerArgument(new Argument("depth", "input depth of the network, default = 1", false, "d"))
                .registerArgument(new Argument("width", "input width of the network, default = 1", false, "w"))
                .registerArgument(new Argument("height", "input height of the network", true, "h"))
                .setExecutable(new Executable() {
            @Override
            public void execute(Connection<?> con, Command c) {
                System.out.println(c);

                int d = c.getArgument("depth").getValue() == null? 1: Integer.parseInt(c.getArgument("depth").getValue());
                int w = c.getArgument("width").getValue() == null? 1: Integer.parseInt(c.getArgument("width").getValue());
                int h = c.getArgument("height").getValue() == null? 1: Integer.parseInt(c.getArgument("height").getValue());

                builder = new NetworkBuilder(d,w,h);
                System.err.println("Construction mode:");
                construction = true;
            }
        }));
        this.commandDataBase.registerCommand(new Command("network_overview").setExecutable(new Executable() {
            @Override
            public void execute(Connection<?> con, Command c) {
                if(network!=null)
                    network.overview();
            }
        }));

        this.architectDataBase.registerCommand(new Command("overview").setExecutable(new Executable() {
            @Override
            public void execute(Connection<?> con, Command c) {
                if(builder != null){
                    builder.overview();
                }
            }
        }));

        this.architectDataBase.registerCommand(new Command("build").setExecutable(new Executable() {
            @Override
            public void execute(Connection<?> con, Command c) {
                network = builder.buildNetwork();
                construction = false;
                System.err.println("Construction mode finished");
            }
        }));


        this.architectDataBase.registerCommand(new Command("add_dense")
                .registerArgument(new Argument("neurons", "neurons in the next layer", true, "n"))
                .registerArgument(new Argument("weight_range", "range of the weight values", false, "w"))
                .registerArgument(new Argument("bias_range", "range of the bias values", false, "b"))
                .setExecutable(new Executable() {
                    @Override
                    public void execute(Connection<?> con, Command c) {
                        int neurons = c.getArgument("neurons").getValue() == null? 1: Integer.parseInt(c.getArgument("neurons").getValue());
                        DenseLayer d = new DenseLayer(neurons);


                        if(c.getArgument("weight_range").getValues() != null){
                            double lower = Double.parseDouble(c.getArgument("weight_range").getValues()[0]);
                            double upper = Double.parseDouble(c.getArgument("weight_range").getValues()[1]);
                            d.weightsRange(lower, upper);
                        }
                        if(c.getArgument("bias_range").getValues() != null){
                            double lower = Double.parseDouble(c.getArgument("bias_range").getValues()[0]);
                            double upper = Double.parseDouble(c.getArgument("bias_range").getValues()[1]);
                            d.biasRange(lower, upper);
                        }
                        builder.addLayer(new DenseLayer(neurons));
                    }
                }));

    }

    public void process(String s){
        if(construction) architectDataBase.executeCommand(s);
        else{
            commandDataBase.executeCommand(s);
        }
    }
    public static void main(String[] args){
        MnistCommandLine c = new MnistCommandLine();
        Scanner scanner = new Scanner(System.in);
        while(true){
            c.process(scanner.nextLine());
        }
    }
}
