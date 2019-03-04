package luecx.server.templates.gameserver_v1;


import luecx.server.templates.gameserver_v1.Game.UDPTransmittedGame;
import luecx.server.udp.udp_content.UDPContentInterface;

import java.net.SocketException;

public class UDPGameServer<R extends UDPContentInterface, S extends UDPContentInterface> extends Thread {


    public static final int REFRESH_RATE_250 = 4;
    public static final int REFRESH_RATE_100 = 10;
    public static final int REFRESH_RATE_50 = 20;
    public static final int REFRESH_RATE_25 = 40;
    public static final int REFRESH_RATE_20 = 50;

    public final int REFRESH_RATE;

    private UDPDataServer dataServer;
    private UDPTransmittedGame<R,S> game;

    public UDPGameServer(UDPTransmittedGame<R,S> game, int REFRESH_RATE, int port) throws SocketException {
        this.REFRESH_RATE = REFRESH_RATE;
        this.dataServer = new UDPDataServer<>(this);
        this.dataServer.start(port);

        this.game = game;
        super.start();
    }

    public UDPGameServer(UDPTransmittedGame<R,S> game, int REFRESH_RATE) throws SocketException {
        this.REFRESH_RATE = REFRESH_RATE;
        this.dataServer = new UDPDataServer<>(this);
        this.dataServer.start(55555);

        this.game = game;
        super.start();
    }

    @Override
    public void run() {
        long last = System.currentTimeMillis();

        while (!this.isInterrupted()) {
            try {
                Thread.sleep(REFRESH_RATE - System.currentTimeMillis() + last);
                this.game.process_controls((double)(System.currentTimeMillis()- last) / 1000d);
                last = System.currentTimeMillis();

                this.dataServer.send_to_all(this.game.getUpdatedPositions());

                this.dataServer.timeOutCheckAndKick(1000);
            } catch (Exception e) {
                this.interrupt();
            }
        }
    }

    public void close() {
        this.dataServer.close();
        this.interrupt();
    }

    @Deprecated
    public void start(){}

    public UDPDataServer getDataServer() {
        return dataServer;
    }

    public UDPTransmittedGame getGame() {
        return game;
    }


//    public static void main(String[] args) throws Exception {
//        UDPGameServer gameServer = new UDPGameServer(new UDPTransmittedGame() {
//            @Override
//            public void new_player(UDPClientInformation clientInformation) {
//
//            }
//
//            @Override
//            public void remove_player(UDPClientInformation clientInformation) {
//
//            }
//
//            @Override
//            public void process_controls() {
//
//            }
//
//            @Override
//            public UDPPositionPackage getUpdatedPositions() {
//                return new UDPPositionPackage(new UDPPositionData[3]);
//            }
//        }, 100);
//
//        Thread.sleep(100);
//
//        UDPClient<UDPPositionPackage, UDPControlData> client = new UDPClient<UDPPositionPackage, UDPControlData>("localhost", 55555) {
//            @Override
//            protected void package_received(UDPPositionPackage t) {
//                System.out.println(t.getClass().getAnnotatedInterfaces());
//            }
//        };
//        client.send(new UDPControlData());
//
//
//        Thread.sleep(2000);
//
//        client.close();
//
//
//        Thread.sleep(100);
//        gameServer.close();
//    }
}