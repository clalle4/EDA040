package server;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {

	public static void main(String[] args) throws UnknownHostException, IOException {
			String server = null;
			int port = Integer.parseInt(args[0]);
			Camera camera = new Camera(server, port);
			JPEGHTTPServer HTTP = new JPEGHTTPServer();
			camera.start();
			HTTP.start();
	}

}
