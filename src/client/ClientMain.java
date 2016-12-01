package client;

	import java.io.IOException;
	import java.net.Socket;
	import java.net.UnknownHostException;
	import client.GUI;
	import client.ClientInputThread;
	import client.ClientMonitor;
	import client.ClientOutputThread;

public class ClientMain {
		public static void main(String[] args) throws UnknownHostException, IOException {
			ClientMonitor mon = new ClientMonitor();
			Socket[] sock = new Socket[2];
			ClientInputThread[] input = new ClientInputThread[2];
			ClientOutputThread[] output = new ClientOutputThread[2];
			for (int i = 0; i < 2; i++) {
				String server = null;
				int port = 8011+ i;
				sock[i] = new Socket(server, port);
				output[i] = new ClientOutputThread(mon, sock[i]);
				input[i] = new ClientInputThread(mon, sock[i], i + 1);
				mon.addOutputThread(output[i]);
				System.out.println("Operating at port: "+ port);
			}
			for (ClientInputThread it : input) {
				it.start();
			}
			for (ClientOutputThread ot : output) {
				ot.start();
			}


			GUI gui = new GUI(mon);
			gui.run();
		}

	}
