package luecx.server.templates.gameserver_v2.server;


import luecx.server.tcp.connection.Connection;
import luecx.server.tcp.server.TCPServer;
import luecx.server.udp.server_side.UDPClientInformation;

public abstract class ClientData {

    private Connection<TCPServer> TCP_Connection;
    private UDPClientInformation UDP_Connection;

    public ClientData() {
    }

    public Connection<TCPServer> getTCP_Connection() {
        return TCP_Connection;
    }

    public void setTCP_Connection(Connection<TCPServer> TCP_Connection) {
        this.TCP_Connection = TCP_Connection;
    }
}
