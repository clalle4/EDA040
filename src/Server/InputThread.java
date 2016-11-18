package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class InputThread extends Thread {
	private Monitor monitor;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private OutputStream os;

	public InputThread(Monitor mon, ServerSocket serverSocket) {
		monitor = mon;
		this.serverSocket = serverSocket;
	}

	public void run() {
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				InputStream is = clientSocket.getInputStream();
				os = clientSocket.getOutputStream();
				// Read the request
				String request = getLine(is);

				// Interpret the request. Complain about everything but GET.
				// Ignore the file name.
				if (request.substring(0, 4).equals("GET ")) {
					// Got a GET request.
					monitor.handleRequest(os);
				}
				InputStream input = clientSocket.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Read a line from InputStream 's', terminated by CRLF. The CRLF is not
	 * included in the returned string.
	 */
	private static String getLine(InputStream s) throws IOException {
		boolean done = false;
		String result = "";

		while (!done) {
			int ch = s.read(); // Read
			if (ch <= 0 || ch == 10) {
				// Something < 0 means end of data (closed socket)
				// ASCII 10 (line feed) means end of line
				done = true;
			} else if (ch >= ' ') {
				result += (char) ch;
			}
		}

		return result;
	}

}
