package luecx.server.tcp.server;


import luecx.server.tcp.connection.Connection;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketListener extends Thread {

	private TCPServer TCPServer;
	private ServerSocket server_socket;

	private boolean isListening = false;
	private int loopTime = 1;

	public ServerSocketListener(TCPServer server, ServerSocket server_socket) {
		super();
		this.TCPServer = server;
		this.server_socket = server_socket;
		this.start();
	}

	public void setListeningStatus(boolean status) {
		this.isListening = status;
	}

	public boolean isListening() {
		return this.isListening;
	}

	public int getLoopTime() {
		return loopTime;
	}

	public void setLoopTime(int loopTime) {
		this.loopTime = loopTime;
	}

	public void run() {
		while (!this.isInterrupted()) {
			try {
				Thread.sleep(loopTime);
				Socket s = server_socket.accept();

				Connection<TCPServer> con = new Connection<TCPServer>(s, TCPServer);

				if (this.isListening && TCPServer.connectedClients() < TCPServer.max_clients) {
					if(TCPServer.hasServerLoginHandler()){
						TCPServer.getServerLoginHandler().logInEvent(con);
					}
					TCPServer.addConnection(con);
				} else {
					con.close();
				}
			} catch (Exception e) {
				this.interrupt();
			}
		}
	}
}
