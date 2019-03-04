package luecx.server.templates.gameserver_v1;


import luecx.server.udp.server_side.UDPClientInformation;
import luecx.server.udp.server_side.UDPServer;
import luecx.server.udp.udp_content.UDPContentInterface;

public class UDPDataServer<R extends UDPContentInterface, S extends UDPContentInterface> extends UDPServer<R, S> {


    private UDPGameServer<R,S> gameServer;

    public UDPDataServer(UDPGameServer<R,S> udpGameServer) {
        this.gameServer = udpGameServer;
    }

    @Override
    public void connection_timeout(UDPClientInformation c) {
        this.gameServer.getGame().remove_player(c);
    }

    @Override
    public void package_received(UDPClientInformation c, int len, R packet) {
        gameServer.getGame().set_controller(c, packet);
    }

    @Override
    public void new_connection(UDPClientInformation c) {
        gameServer.getGame().new_player(c);
    }
}
