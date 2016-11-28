package main;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import server.Camera;
import client.GUI;
import client.ClientInputThread;
import client.ClientMonitor;
import client.ClientOutputThread;
import client.ServerThread;

public class Main {

	public static void main(String[] args) throws UnknownHostException, IOException {
		ClientMonitor mon = new ClientMonitor();
		Socket[] sock = new Socket[2];
		ClientInputThread[] input = new ClientInputThread[2];
		ClientOutputThread[] output = new ClientOutputThread[2];
		Camera[] camera = new Camera[2];
		ServerThread[] http = new ServerThread[2];
		for (int i = 0; i < 2; i++) {
			String server = null;
			int port = 8011 + i;
			camera[i] = new Camera(port);
			sock[i] = new Socket(server, port);
			output[i] = new ClientOutputThread(mon, sock[i]);
			input[i] = new ClientInputThread(mon, sock[i], i + 1);
			mon.addOutputThread(output[i]);
			System.out.println("Operating at port: "+ port);
			http[i] = new ServerThread(port);
		}
		for (ClientInputThread it : input) {
			it.start();
		}
		for (ClientOutputThread ot : output) {
			ot.start();
		}
		for (Camera cam : camera) {
			cam.start();
		}
		for (ServerThread server : http) {
			server.start();
		}
		GUI gui = new GUI(mon);
		gui.run();
	}

}
