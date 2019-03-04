package luecx.server.templates.gameserver_v2.server;


import luecx.server.udp.udp_content.UDPContent;

import java.util.ArrayList;

public class ServerOut<R extends UDPContent> extends UDPContent{

    private ArrayList<R> content = new ArrayList<>();

    public ArrayList<R> getContent() {
        return content;
    }

    public void setContent(ArrayList<R> content) {
        this.content = content;
    }
}
