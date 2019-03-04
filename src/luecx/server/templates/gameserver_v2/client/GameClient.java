package luecx.server.templates.gameserver_v2.client;


import luecx.server.protocol.commands.Command;
import luecx.server.protocol.commands.CommandDataBase;
import luecx.server.tcp.client.TCPClient;
import luecx.server.tcp.connection.Connection;
import luecx.server.tcp.entities.IncomingMessageHandler;
import luecx.server.templates.gameserver_v2.server.ServerOut;
import luecx.server.udp.client_side.UDPClient;
import luecx.server.udp.udp_content.UDPContent;

import java.util.ArrayList;

public abstract class GameClient<S extends UDPContent> {


    String ip;

    CommandDataBase dataBase;

    TCPClient TCP_CIENT;
    UDPClient<ServerOut<S>, S> UDP_CLIENT;

    public GameClient(){

    }

    protected abstract ArrayList<Command> createTCPCommands();

    public void connect(String ip, int port, String loginData){


        this.ip = ip;
        TCP_CIENT = new TCPClient();

        TCP_CIENT.addIncomingMessageHandler(new IncomingMessageHandler<TCPClient>() {
            @Override
            public void incomingMessage(Connection<TCPClient> con, Object msg) {

            }
        });

        TCP_CIENT.connect(ip, port);
        try {
            TCP_CIENT.getConnection().sendMessage("login " + loginData);
        } catch (Exception e) {
            e.printStackTrace();
        }



        dataBase = new CommandDataBase();
        dataBase.registerCommand(new Command("udpconnect"));
    }

    public void disconnect(){

    }


}
