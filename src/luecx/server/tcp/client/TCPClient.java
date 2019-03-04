package luecx.server.tcp.client;

import luecx.server.tcp.connection.Connection;
import luecx.server.tcp.entities.Observer;

import java.net.Socket;


public class TCPClient extends Observer<TCPClient> {

	private Connection<TCPClient> connection;

	public TCPClient() {

	}

	public void connect(String ip, int port) {
		if (!isConnected()) {
			try {
				connection = new Connection<TCPClient>(new Socket(ip, port), this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	public Connection<TCPClient> getConnection() {
		return this.connection;
	}

	public boolean isConnected() {
		if (connection != null) {
			if (connection.isRunning()) {
				return true;
			}
		}
		return false;
	}

	public void disconnect() {
		this.connection.close();
	}

	@Override
	public void removeConnection(Connection<TCPClient> connection) {
		// TODO Auto-generated method stub

	}

}
