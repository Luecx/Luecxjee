package luecx.server.tcp.connection;


import luecx.server.tcp.entities.Observer;

public abstract class ConnectionClosedHandler<T extends Observer<T>> {
	public abstract void connectionClosed(Connection<T> con);
}
