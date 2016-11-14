package Server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class InputThread  extends Thread{
	private Monitor monitor;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	public InputThread(Monitor mon, ServerSocket serverSocket){
		monitor = mon;
		this.serverSocket =  serverSocket;
		
	}

	public void run(){

			try {
				clientSocket = serverSocket.accept();
			
	
		monitor.setClientSocket(clientSocket);
		InputStream input = clientSocket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
}
