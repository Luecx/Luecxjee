package luecx.server.udp.udp_content;

import java.io.Serializable;

public abstract class UDPContent implements Serializable, UDPContentInterface {


    private long timestamp;
    private long client_id;

    @Override
    public long getClient_id() {
        return client_id;
    }

    @Override
    public void setClient_id(long client_id) {
        this.client_id = client_id;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
