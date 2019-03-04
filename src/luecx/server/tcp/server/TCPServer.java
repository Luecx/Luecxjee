package luecx.server.tcp.server;


import luecx.server.tcp.connection.Connection;
import luecx.server.tcp.entities.Observer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServer extends Observer<TCPServer> {

	protected ArrayList<Connection<TCPServer>> connections = new ArrayList<>();
	protected ServerSocketListener listener;
	protected ServerSocket server_socket;

	protected ServerLoginHandler serverLoginHandler;
	public int max_clients = 12;


	public static void main(String[] args){

	}

	public TCPServer() {
	}

	public void start(int port) throws Exception {
		this.server_socket = new ServerSocket(port);
		this.listener = new ServerSocketListener(this, server_socket);
		this.startListening();
	}

	public void startListening() {
		this.listener.setListeningStatus(true);
	}

	public void stopListening() {
		this.listener.setListeningStatus(false);
	}

	public boolean isListening() {
		return this.listener.isListening();
	}

	public Connection<TCPServer> getConnection(int index) {
		return this.connections.get(index);
	}

	void addConnection(Connection<TCPServer> con) {
		this.connections.add(con);
	}

	public void close() throws Exception {
		this.listener.interrupt();
		this.closeAllConnections();
		this.server_socket.close();
	}

	public void closeAllConnections() {

		for (int i = 0; i < this.connections.size(); i++) {
			this.removeConnection(i);
		}

	}

	public void removeConnection(int index) {
		this.connections.get(index).close();
	}

	@Override
	public void removeConnection(Connection<TCPServer> connection) {
		for (int i = 0; i < this.connections.size(); i++) {
			if (connections.get(i).equals(connection)) {
				connections.get(i).close();
				connections.remove(i);
				break;
			}
		}
	}
	
	public ArrayList<Connection<TCPServer>> getConnections() {
		return connections;
	}
	
	public int connectedClients() {
		return this.connections.size();
	}
	
	public int getMaxClients() {
		return this.max_clients;
	}
	
	public void setMaxClients(int max){
		if(max < 0){
			this.max_clients = 0;
		}else{
			this.max_clients = max;
		}
	}
	
	public ServerSocketListener getListener() {
		return listener;
	}
	
	public ServerSocket getServer_socket() {
		return server_socket;
	}

	public void print() {
		String style = "%-15s %-15s %-15s%n";

		System.out.format(style, "Connection-ip", "BufferWriting", "Buffer size");

		for (int i = 0; i < this.connections.size(); i++) {
			Connection<TCPServer> c = this.connections.get(i);
			Socket s = c.getSocket();
			System.out.format(style, s.getInetAddress(), c.isBufferWriting(), c.messages.size());
		}

	}

	public ServerLoginHandler getServerLoginHandler() {
		return serverLoginHandler;
	}
	

	public void addServerLoginHandler(ServerLoginHandler serverLoginHandler) {
		this.serverLoginHandler = serverLoginHandler;
	}
	
	public void removeServerLoginHandler() {
		this.serverLoginHandler = null;
	}
	
	public boolean hasServerLoginHandler() {
		return this.serverLoginHandler != null;
	}

	
	

}
