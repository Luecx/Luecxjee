package luecx.server.tcp.entities;


import luecx.server.tcp.connection.Connection;

public abstract class IncomingMessageHandler<T extends Observer<T>> {
	public abstract void incomingMessage(Connection<T> con, Object msg);
}
