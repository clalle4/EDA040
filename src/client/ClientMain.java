package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMain {

	public static void main(String[] args) throws UnknownHostException, IOException {
		System.out.println("1 or 2 cameras?");
		Monitor mon = new Monitor();
		int camAmount = System.in.read();
		Socket[] sock = new Socket[camAmount];
		ClientInputThread[] input = new ClientInputThread[camAmount];
		ClientOutputThread[] output = new ClientOutputThread[camAmount];
		for (int i = 0; i < camAmount; i++) {
			String server = null;
			int port = 8011 + i;
			sock[i] = new Socket(server, port);
			input[i] = new ClientInputThread(mon, sock[i], i+1);
			output[i] = new ClientOutputThread(mon, sock[i], input[i]);
		}
		GUI gui = new GUI(mon);
		gui.run();
		for (ClientInputThread it : input) {
			it.start();
		}
		for (ClientOutputThread ot : output) {
			ot.start();
		}
	}

}
