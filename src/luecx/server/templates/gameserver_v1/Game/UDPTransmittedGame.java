package luecx.server.templates.gameserver_v1.Game;


import luecx.server.udp.server_side.UDPClientInformation;
import luecx.server.udp.udp_content.UDPContentInterface;

import java.util.HashMap;

public abstract class UDPTransmittedGame<R extends UDPContentInterface, S extends UDPContentInterface> {

    protected HashMap<UDPClientInformation, R> controller = new HashMap<>();

    public UDPTransmittedGame() {

    }

    public abstract void new_player(UDPClientInformation clientInformation);

    public abstract void remove_player(UDPClientInformation clientInformation);

    public abstract void process_controls(double passedTime);

    public abstract S getUpdatedPositions();


    public void set_controller(UDPClientInformation client, R controller){
        this.controller.put(client, controller);
    }



}
