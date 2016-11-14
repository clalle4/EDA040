package Server;

import java.net.Socket;

public class Monitor {
	private Socket clientSocket;
	
	
	public void setClientSocket(Socket clientSocket){
		this.clientSocket = clientSocket;
	}
}
