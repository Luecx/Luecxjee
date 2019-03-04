package luecx.server.templates.gameserver_v2.testing;


public class ClientData extends luecx.server.templates.gameserver_v2.server.ClientData {

    private String name;

    public ClientData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
