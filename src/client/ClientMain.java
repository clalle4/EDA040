package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMain {

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		String server = "";
		int port = 0;
		
		Socket sock = new Socket(server, port);
		Monitor mon = new Monitor();
		GUI gui = new GUI(mon);
		InputThread input = new InputThread(mon, sock);
		OutputThread output = new OutputThread(mon, sock, input);
		input.start();
		output.start();
	}

}
