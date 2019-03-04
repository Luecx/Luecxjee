package luecx.server.udp.server_side;

import java.net.InetAddress;
import java.util.Objects;

public class UDPClientInformation {


    private InetAddress address;
    private long id;
    private int port;

    public UDPClientInformation(InetAddress address, long id, int port) {
        this.address = address;
        this.id = id;
        this.port = port;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "UDPClientInformation{" +
                "address=" + address +
                ", id=" + id +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UDPClientInformation that = (UDPClientInformation) o;
        return id == that.id &&
                port == that.port &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {

        return Objects.hash(address, id, port);
    }
}
