package luecx.server.tcp.connection;


import luecx.server.tcp.entities.Observer;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;


public class Connection<T extends Observer<T>> {
	
	
	public Socket 					socket;
	public ObjectOutputStream 		out;
	public ObjectInputStream 		in;

    private T 						observer;
    private ConnectionListener<T> 	listener;
    public Queue<Object> 			messages = new LinkedList<Object>();

	
 	public Connection(Socket sc, T observer){
		try{
			this.socket = sc;
			this.observer = observer;
			
			
			InputStream in = sc.getInputStream();
			OutputStream out = sc.getOutputStream();
			

			this.out = new ObjectOutputStream(out);
			this.in = new ObjectInputStream(in);
            
			
            this.listener = new ConnectionListener<T>(this);
            
		} catch (Exception ex) {
        	this.socket = null;
            ex.printStackTrace();
        }
	}
	
    public void enableBuffering() {
    	this.listener.setBufferWritingStatus(true);
    }
    
    public void disableBuffering() {
    	this.listener.setBufferWritingStatus(false);
    }
    
    public void close() {
        if(isRunning()) {
            try {
            	if(observer.hasConnectionClosedHandler()){
            		observer.getConnectionClosedHandler().connectionClosed(this);
            	}
                this.listener.interrupt();
                socket.close();
                out.close();
                in.close(); 
                this.remove();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
    }
    
    private void remove(){
		this.observer.removeConnection(this);
	}
    
    public void sendMessage(Object o) throws Exception {
    	this.out.writeObject(o);
    	this.out.flush();
    }
    
    public boolean isRunning(){
        return (this.socket.isBound() && !this.socket.isClosed());
    }
      
    public boolean isBufferWriting() {
    	return this.listener.isBufferWriting();
    }
 
    public Object getNextMessage() {
    	if(!this.messages.isEmpty())
    		return this.messages.peek();
    	else{
    		return null;
    	}
    }
    
    public Object popNextMessage() {
    	if(!this.messages.isEmpty())
        	return this.messages.poll();
    	else{
    		return null;
    	}
    }

    public Socket getSocket() {
		return socket;
	}
  
    public T getObserver() {
    	return this.observer;
    }
    
    public ObjectOutputStream getObjectOutputStream() {
		return out;
	}

	public ObjectInputStream getObjectInputStream() {
		return in;
	}

	
	
}