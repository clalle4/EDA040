package client;

import java.io.IOException;

public class ServerThread extends Thread {
private int port;
	public ServerThread(int port){
		this.port = port;
	}
	public void run() {
		JPEGHTTPServer theServer = new JPEGHTTPServer(port);
		try {
			theServer.handleRequests();
		} catch (IOException e) {
			System.out.println("HTTP: Error!");
			theServer.destroy();
		}
	}
}
