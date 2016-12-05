package server;

import java.io.IOException;
import java.net.UnknownHostException;

public class ServerMain {
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Camera[] camera = new Camera[2];
		JPEGHTTPServer[] HTTP = new JPEGHTTPServer[2];
		for (int i = 0; i < 2; i++) {
			String server = null;
			int port = 8011 + i;
			camera[i] = new Camera(server, port);
			HTTP[i] = new JPEGHTTPServer();
		}
		for (Camera cam : camera) {
			cam.start();
		}
		for (JPEGHTTPServer s: HTTP) {
			s.start();
		}
	}

}
