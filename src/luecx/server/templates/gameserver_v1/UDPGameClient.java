package luecx.server.templates.gameserver_v1;

import luecx.server.udp.client_side.UDPClient;
import luecx.server.udp.udp_content.UDPContentInterface;

import java.net.InetAddress;

public abstract class UDPGameClient<S extends UDPContentInterface, R extends UDPContentInterface> extends UDPClient<R,S> {


    public UDPGameClient(InetAddress target_adress, int target_port) throws Exception {
        super(target_adress, target_port);
    }

    public UDPGameClient(InetAddress target_adress, int target_port, int data_size_max) throws Exception {
        super(target_adress, target_port, data_size_max);
    }

    public UDPGameClient(String ip, int target_port, int data_size_max) throws Exception {
        super(ip, target_port, data_size_max);
    }

    public UDPGameClient(String ip, int target_port) throws Exception {
        super(ip, target_port);
    }
}
