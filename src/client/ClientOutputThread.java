package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClientOutputThread extends Thread {
	private ClientMonitor mon;
	private Socket sock;
	private static final byte[] CRLF = { 13, 10 };

	public ClientOutputThread(ClientMonitor mon, Socket sock) {
		this.mon = mon;
		this.sock = sock;
	}

	public void run() {
		

		while (true) {
			// varje x sekund: skicka request till server
			if (!mon.motionDetected()) {
				try {
					synchronized (this) {
						this.wait(5000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
					Thread.sleep(50); // 20 FPS
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			try {
				OutputStream os = sock.getOutputStream();
				sendRequest(os);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void sendRequest(OutputStream os) {
		// Send a simple request, always for "/image.jpg"
		try {
			putLine(os, "GET /image.jpg HTTP/1.0");
			putLine(os, ""); // The request ends with an empty line
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send a line on OutputStream 's', terminated by CRLF. The CRLF should not
	 * be included in the string str.
	 */
	private static void putLine(OutputStream s, String str) throws IOException {
		s.write(str.getBytes());
		s.write(CRLF);
	}

}
