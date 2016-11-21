package main;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import Server.Camera;
import client.GUI;
import client.InputThread;
import client.Monitor;
import client.OutputThread;

public class Main {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Monitor mon = new Monitor();
		Socket[] sock = new Socket[2];
		InputThread[] input = new InputThread[2];
		OutputThread[] output = new OutputThread[2];
		Camera[] camera = new Camera[2];
		for (int i = 0; i < 2; i++) {
			String server = null;
			int port = 8011 + i;
			camera[i] = new Camera(port);
			sock[i] = new Socket(server, port);
			input[i] = new InputThread(mon, sock[i], i + 1);
			output[i] = new OutputThread(mon, sock[i], input[i]);
		}
//		GUI gui = new GUI(mon);
//		gui.run();
		for (InputThread it : input) {
			it.start();
		}
		for (OutputThread ot : output) {
			ot.start();
		}
		for (Camera cam : camera) {
			cam.start();
		}
	}

}
