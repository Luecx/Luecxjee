package luecx.server.udp.client_side;


import luecx.server.udp.udp_content.UDPContentInterface;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class UDPClient<R extends UDPContentInterface, S extends UDPContentInterface> extends Thread {

    private InetAddress target_adress;
    private int target_port;

    private final int max_data_size;

    private DatagramPacket packet;
    private DatagramSocket socket;

    private R last;
    private long id = (long) (Math.random() * 10000000);


    public UDPClient(InetAddress target_adress, int target_port) throws Exception {
        this.target_adress = target_adress;
        this.target_port = target_port;
        this.max_data_size = 1024;
        this.startListening();
    }

    public UDPClient(InetAddress target_adress, int target_port, int data_size_max) throws Exception {
        this.target_adress = target_adress;
        this.target_port = target_port;
        this.max_data_size = data_size_max;
        this.startListening();
    }

    public UDPClient(String ip, int target_port, int data_size_max) throws Exception {
        this.target_adress = InetAddress.getByName(ip);
        this.target_port = target_port;
        this.max_data_size = data_size_max;
        this.startListening();
    }

    public UDPClient(String ip, int target_port) throws Exception {
        this.target_adress = InetAddress.getByName(ip);
        this.target_port = target_port;
        this.max_data_size = 1024;
        this.startListening();
    }

    public void run() {
        while (!this.isInterrupted() && this.socket.isClosed() == false) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[max_data_size], max_data_size);
                socket.receive(packet);

                R t = newInstance(packet.getData());

                if (last == null) {
                    last = t;
                    this.package_received(t);
                } else {
                    if (t.getTimestamp() > last.getTimestamp()) {
                        last = t;
                        this.package_received(t);
                    }
                }

            } catch (Exception e) {

            }
        }
        this.interrupt();
    }

    protected abstract void package_received(R t);

    private void send(byte[] data) throws Exception {
        if (this.socket == null || this.socket.isClosed())
            this.socket = new DatagramSocket();

        packet = new DatagramPacket(data, data.length, target_adress, target_port);
        socket.send(packet);
    }

    public void send(S packet) throws Exception {
        if (packet != null)
            send(toBytes(packet));
    }

    private void startListening() throws Exception {
        this.socket = new DatagramSocket();
        super.start();
    }

    public void close() {
        this.socket.close();
        this.interrupt();
    }

    @Override
    public long getId() {
        return id;
    }

    private R newInstance(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        R o = null;
        try {
            in = new ObjectInputStream(bis);
            o = (R) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return o;
    }

    private byte[] toBytes(S object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        object.setClient_id(this.id);
        object.setTimestamp(System.currentTimeMillis());
        byte[] bytes = new byte[1];
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return bytes;
    }


}
