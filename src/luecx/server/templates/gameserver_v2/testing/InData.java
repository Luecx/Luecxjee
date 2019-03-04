package luecx.server.templates.gameserver_v2.testing;


import luecx.server.udp.udp_content.UDPContent;

import java.util.Objects;

public class InData extends UDPContent {

    private int x;

    public InData(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "InData{" +
                "x=" + x +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InData inData = (InData) o;
        return x == inData.x;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x);
    }
}
