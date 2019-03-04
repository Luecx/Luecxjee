package luecx.server.tcp.connection;


import luecx.server.tcp.entities.Observer;

public class ConnectionListener<T extends Observer<T>> extends Thread{

	private Connection<T> con;
	private boolean isBuffering = false;
	private int loopTime = 0;
	
	public ConnectionListener(Connection<T> con) {
		this.con = con;
		this.start();
	}
	
	public void setBufferWritingStatus(boolean status) {
		this.isBuffering = status;
	}
	
	public boolean isBufferWriting() {
		return this.isBuffering;
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
				Object o = con.in.readObject();
				if(o == null) con.close();
				else{
					if(con.getObserver().hasIncomingMessageHandler()){
						con.getObserver().getIncomingMessageHandler().incomingMessage(con, o);
					}

					if(this.isBuffering){
						con.messages.add(o);
					}
				}
			} catch (Exception e) {
				this.interrupt();
			}

		}
		con.close();
	}

	
}
