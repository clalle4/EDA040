package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class OutputThread extends Thread {
	private Monitor mon;
	private Socket sock;
	private InputThread input;

	public OutputThread(Monitor mon, Socket sock, InputThread input) {
		this.mon = mon;
		this.sock = sock;
		this.input = input;
	}

	public void run() {

		// varje x sekund: skicka request till server

		if (!mon.movieMode()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			OutputStream os = sock.getOutputStream();
			sendRequest(os);
			input.setExpected();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void sendRequest(OutputStream os) {
		
		
	}
	

}
