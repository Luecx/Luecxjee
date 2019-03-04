package luecx.server.tcp.server;


import luecx.server.tcp.connection.Connection;

public abstract class ServerLoginHandler{
	public abstract void logInEvent(Connection<TCPServer> con);
}
