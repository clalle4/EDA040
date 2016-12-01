package main;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import server.Camera;
import client.GUI;
import client.ClientInputThread;
import client.ClientMonitor;
import client.ClientOutputThread;

public class main2 {

	public static void main(String[] args) throws UnknownHostException, IOException {
		ClientMonitor mon = new ClientMonitor();
		String server = "rt@argus-1:8011";
		int port = 8011;
		Camera camera = new Camera(server, port);
		Socket sock = new Socket(server, port);
		ClientOutputThread output = new ClientOutputThread(mon, sock);
		ClientInputThread input = new ClientInputThread(mon, sock, 1);
		mon.addOutputThread(output);
		System.out.println("Operating at port: " + port);
		camera.start();
		output.start();
		input.start();
		GUI gui = new GUI(mon);
		gui.run();
	}

}
