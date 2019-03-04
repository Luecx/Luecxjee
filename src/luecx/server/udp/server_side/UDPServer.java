package luecx.server.udp.server_side;


import luecx.server.udp.udp_content.UDPContentInterface;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class UDPServer<R extends UDPContentInterface, S extends UDPContentInterface> extends Thread{


    private DatagramSocket socket;
    private final int max_data_size;

    private DatagramPacket packet;

    protected HashMap<UDPClientInformation, R> contents = new HashMap<>();

    public UDPServer() {this(1024);}

    public UDPServer(int max_data_size) { this.max_data_size = max_data_size; }


    public void run(){
        while(!this.isInterrupted() && socket.isClosed() == false){
            try {
                DatagramPacket packet = new DatagramPacket(new byte[max_data_size], max_data_size);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                int len = packet.getLength();
                byte[] data = packet.getData();

                R t = newInstance(data);

                UDPClientInformation c = new UDPClientInformation(address, t.getClient_id(), port);


                if(contents.containsKey(c)){
                    if(contents.get(c).getTimestamp() <= t.getTimestamp()){
                        contents.put(c, t);
                        package_received(c, len, t);
                    }
                }else{
                    contents.put(c, t);
                    this.new_connection(c);
                    package_received(c, len, t);
                }
            }catch (Exception e){

            }
        }
        this.interrupt();
    }

    public abstract void package_received(UDPClientInformation c, int len, R packet);

    public abstract void connection_timeout(UDPClientInformation c);

    public abstract void new_connection(UDPClientInformation c);


    public void start(int port) throws SocketException{
        this.socket = new DatagramSocket(port);
        super.start();
    }

    public void close(){
        this.socket.close();
        this.contents.clear();
        this.interrupt();
    }


    private void send(InetAddress address, int port, byte[] bytes) throws IOException {
        if(this.socket == null) socket = new DatagramSocket();
        packet = new DatagramPacket( bytes, bytes.length, address, port );
        socket.send(packet);
    }

    public void send(UDPClientInformation client, S object) throws IOException {
        send(client.getAddress(), client.getPort(), toBytes(object));
    }

    public void send_to_all(S object) {
        for(UDPClientInformation to:contents.keySet()){
            try {
                send(to, object);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public int getMax_data_size() {
        return max_data_size;
    }

    public boolean timeOut(UDPClientInformation address, int threshold_milli){
        if(contents.containsKey(address) == false) return true;
        return (System.currentTimeMillis() - contents.get(address).getTimestamp() > threshold_milli);
    }

    public void timeOutCheckAndKick(int threshold_milli){
        long t = System.currentTimeMillis();
        ArrayList<UDPClientInformation> kicks = null;
        for(UDPClientInformation client:this.contents.keySet()){
            if(contents.get(client).getTimestamp() - t < -threshold_milli){
                if(kicks == null){
                    kicks = new ArrayList<>();
                }
                kicks.add(client);
            }
        }
        if(kicks != null){
            for(UDPClientInformation c:kicks){
                contents.remove(c);
                connection_timeout(c);
            }
        }
    }

    private R newInstance(byte[] bytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        R o = null;
        try {
            in = new ObjectInputStream(bis);
            o = (R)in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ClassCastException e){
            e.printStackTrace();
        }finally {
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

    private byte[] toBytes(S object){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] bytes = new byte[1];
        try {
            out = new ObjectOutputStream(bos);

            object.setTimestamp(System.currentTimeMillis());

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

    public HashMap<UDPClientInformation, R> getContents() {
        return contents;
    }
}