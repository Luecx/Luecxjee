package luecx.server.templates.gameserver_v2.server;


import luecx.server.protocol.commands.Argument;
import luecx.server.protocol.commands.Command;
import luecx.server.protocol.commands.CommandDataBase;
import luecx.server.protocol.commands.Executable;
import luecx.server.tcp.client.TCPClient;
import luecx.server.tcp.connection.Connection;
import luecx.server.tcp.connection.ConnectionClosedHandler;
import luecx.server.tcp.entities.IncomingMessageHandler;
import luecx.server.tcp.server.TCPServer;
import luecx.server.udp.client_side.UDPClient;
import luecx.server.udp.server_side.UDPClientInformation;
import luecx.server.udp.server_side.UDPServer;
import luecx.server.udp.udp_content.UDPContent;
import luecx.server.udp.udp_content.UDPContentInterface;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public abstract class GameServer<D extends ClientData, R extends UDPContent> {

    private CommandDataBase TCP_PROTOCOL = new CommandDataBase();
    private HashMap<Connection<TCPServer>, D> LOGGED_IN_CLIENTS = new HashMap<>();
    private TCPServer TCP_SERVER;

    private UDPServer<R, ServerOut<R>> UDP_SERVER;
    private ServerOut<R> UDP_SERVER_OUT_DATA;
    private GameServerUpdater UDP_SERVER_SENDER;

    private boolean gameIsRunning = false;

    public GameServer() {
    }

    public static void main(String[] args) throws Exception {
        class EData extends ClientData {
            private String name;

            public EData(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
        class InData extends UDPContent{
            private int a;

            public InData(int a) {
                this.a = a;
            }

            public int getA() {
                return a;
            }

            public void setA(int a) {
                this.a = a;
            }
        }

        GameServer<EData, ServerOut> g = new GameServer<EData, ServerOut>() {
            @Override
            protected EData acceptLoginRequest(Command c) {
                return new EData(c.getArgument("name").getValue());
            }
            @Override
            protected ArrayList<Argument> createLoginArguments() {
                Argument name = new Argument("name", true, "n");
                ArrayList<Argument> out = new ArrayList<>();
                out.add(name);
                return out;
            }

            @Override
            protected ArrayList<Command> createTCPCommands() {
                return new ArrayList<>();
            }
        };
        g.start(55555);
        Thread.sleep(100);
        TCPClient client = new TCPClient();
        TCPClient client2 = new TCPClient();
        TCPClient client3 = new TCPClient();
        client.connect("localhost", 55555);
        client.getConnection().sendMessage("login -n finn");
        client2.connect("localhost", 55555);
        client2.getConnection().sendMessage("login -n kai");
        client3.connect("localhost", 55555);
        client3.getConnection().sendMessage("login -n eric");
        Thread.sleep(1000);

        g.printClients();


        g.startGame(55566);
        Thread.sleep(200);
        UDPClient<ServerOut<InData>, InData> client1 = new UDPClient<ServerOut<InData>, InData>("localhost", 55566) {
            @Override
            protected void package_received(ServerOut<InData> t) {
                System.out.println(t);
            }
        };
        client1.send(new InData(2));




        Thread.sleep(1000);

        client1.close();
        g.stopGame();
        

        Thread.sleep(100);
        client.disconnect();
        client2.disconnect();
        client3.disconnect();

        g.close();
    }

    public void printClients(){
        System.out.format("%-10s%20s%n", "InetAddress","Port");
        Set<Connection<TCPServer>> addresses = LOGGED_IN_CLIENTS.keySet();

        for(Connection<TCPServer> c:TCP_SERVER.getConnections()){
            if(addresses.contains(c)){
                System.out.format("%-10s%20s%n", c.getSocket().getInetAddress(), c.getSocket().getPort());
            }else{
                System.err.format("%-10s%20s%n", c.getSocket().getInetAddress(), c.getSocket().getPort());
            }
        }
    }

    protected abstract D acceptLoginRequest(Command c);

    protected abstract ArrayList<Argument> createLoginArguments();

    protected abstract ArrayList<Command> createTCPCommands();

    private void registerTCPCommands() {
        Command c = new Command("login", "request of a user to login and get access to the game data");
        for (Argument a : createLoginArguments()) {
            c.registerArgument(a);
        }
        c.setExecutable(new Executable() {
            @Override
            public void execute(Connection<?> con, Command c) {
                D d = acceptLoginRequest(c);
                if (d == null || gameIsRunning) {
                    con.close();
                } else {
                    d.setTCP_Connection((Connection<TCPServer>) con);
                    LOGGED_IN_CLIENTS.put((Connection<TCPServer>) con, d);
                }
            }
        });
        TCP_PROTOCOL.registerCommand(c);

        for(Command command:createTCPCommands()){
            TCP_PROTOCOL.registerCommand(command);
        }
    }

    private void createTCPListener() {

        this.TCP_SERVER.addIncomingMessageHandler(new IncomingMessageHandler<TCPServer>() {
            @Override
            public void incomingMessage(Connection<TCPServer> con, Object msg) {
                if (msg instanceof String) {
                    TCP_PROTOCOL.executeCommand(con, (String) msg);
                }
            }
        });

        this.TCP_SERVER.addConnectionClosedHandler(new ConnectionClosedHandler<TCPServer>() {
            @Override
            public void connectionClosed(Connection<TCPServer> con) {
                LOGGED_IN_CLIENTS.remove(con);
            }
        });


    }

    public void startGame(int port){
        if(UDP_SERVER != null){
            UDP_SERVER_SENDER.interrupt();
            UDP_SERVER.close();
        }
        UDP_SERVER = new UDPServer() {
            @Override
            public void package_received(UDPClientInformation c, int len, UDPContentInterface packet) {

            }

            @Override
            public void connection_timeout(UDPClientInformation c) {
            }

            @Override
            public void new_connection(UDPClientInformation c) {
                boolean valid = false;
                for(Connection address:LOGGED_IN_CLIENTS.keySet()){
                    if(address.getSocket().getInetAddress().getHostAddress().equals(c.getAddress().getHostAddress())){
                        valid = true;
                        break;
                    }
                }
                if(!valid){
                    UDP_SERVER.getContents().remove(c);
                }
            }
        };

        try {
            UDP_SERVER.start(port);
            UDP_SERVER_OUT_DATA = new ServerOut<R>();
            UDP_SERVER_SENDER = new GameServerUpdater(this, 100);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void stopGame(){
        this.UDP_SERVER.close();
        this.UDP_SERVER_SENDER.interrupt();
        this.UDP_SERVER_OUT_DATA = null;
        this.gameIsRunning = false;
    }

    public void sendUDPData(){
        ServerOut<R> r = new ServerOut<>();
        for(UDPClientInformation c:UDP_SERVER.getContents().keySet()){
            r.getContent().add(UDP_SERVER.getContents().get(c));
        }
        UDP_SERVER.send_to_all(r);
    }

    public void start(int TCP_Port) throws Exception {
        this.TCP_SERVER = new TCPServer();

        registerTCPCommands();
        createTCPListener();

        try {
            this.TCP_SERVER.start(TCP_Port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.TCP_SERVER.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
