package luecx.server.templates.gameserver_v2.server;

public class GameServerUpdater extends Thread {


    int refresh;
    GameServer gameServer;

    public GameServerUpdater(GameServer gameServer, int refresh){
        this.refresh = refresh;
        this.gameServer = gameServer;
        this.start();
    }

    public void run(){
        while(!this.isInterrupted()){
            try {
                Thread.sleep(refresh);
            } catch (InterruptedException e) {
                System.out.println(e);
                this.interrupt();
            }
            gameServer.sendUDPData();
        }
    }


}
