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
		InputThread[] input = new InputThread[camAmount];
		OutputThread[] output = new OutputThread[camAmount];
		for (int i = 0; i < camAmount; i++) {
			String server = null;
			int port = 8011 + i;
			sock[i] = new Socket(server, port);
			input[i] = new InputThread(mon, sock[i], i+1);
			output[i] = new OutputThread(mon, sock[i], input[i]);
		}
		GUI gui = new GUI(mon);
		for (InputThread it : input) {
			it.start();
		}
		for (OutputThread ot : output) {
			ot.start();
		}
	}

}
