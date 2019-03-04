package luecx.server.tcp.entities;


import luecx.server.tcp.connection.Connection;
import luecx.server.tcp.connection.ConnectionClosedHandler;

public abstract class Observer<T extends Observer<T>> {
	
	public abstract void removeConnection(Connection<T> connection);
	protected IncomingMessageHandler<T> incomingMessageHandler;
	protected ConnectionClosedHandler<T> connectionClosedHandler;
	
	public IncomingMessageHandler<T> getIncomingMessageHandler() {
		return incomingMessageHandler;
	}
	
	public void addIncomingMessageHandler(IncomingMessageHandler<T> incomingMessageHandler) {
		this.incomingMessageHandler = incomingMessageHandler;
	}
	
	public void removeIncomingMessageHandler() {
		this.incomingMessageHandler = null;
	}
	
	public boolean hasIncomingMessageHandler() {
		return this.incomingMessageHandler != null;
	}

	
	
	public ConnectionClosedHandler<T> getConnectionClosedHandler() {
		return connectionClosedHandler;
	}
	
	public void addConnectionClosedHandler(ConnectionClosedHandler<T> connectionClosedHandler) {
		this.connectionClosedHandler = connectionClosedHandler;
	}
	
	public void removeConnectionClosedHandler() {
		this.connectionClosedHandler = null;
	}
	
	public boolean hasConnectionClosedHandler() {
		return this.connectionClosedHandler != null;
	}

	
	
	
}
