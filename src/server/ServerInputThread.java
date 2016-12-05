package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles incoming communication from the client 
 */
public class ServerInputThread extends Thread {
	private ServerMonitor mon;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private OutputStream os;
	private InputStream is;
	private boolean firstTime;
	private ServerOutputThread outputThread;

	public ServerInputThread(ServerMonitor mon, ServerSocket serverSocket, ServerOutputThread outputThread) {
		this.mon = mon;
		this.serverSocket = serverSocket;
		this.outputThread = outputThread;
		firstTime = true;
	}

	/**
	 * Waits for the client to send a request, sends the OutputStream from the
	 * received client socket to the ServerOutputThread. Reads the request and
	 * informs the serverMonitor about it if it was a GET request.
	 */
	public void run() {
		while (true) {
			try {
				if (firstTime) {
					clientSocket = serverSocket.accept();
					is = clientSocket.getInputStream();
					os = clientSocket.getOutputStream();
					outputThread.setOutputStream(os);
					firstTime = false;
				}
				// Read the request
				String request = getLine(is);
				// TODO busy wait?
				while (request.isEmpty()) {
					request = getLine(is);
				}
				System.out.println("Client request '" + request + "' received.");
				// Interpret the request. Complain about everything but GET.
				if (request.substring(0, 4).equals("GET ")) {
					// Got a GET request.
					mon.gotARequest(true);
					String cameraMode = getLine(is);
					String s = cameraMode.substring(13, cameraMode.indexOf(" ("));
					if (s.equals("Movie")) {
						mon.runningInMovieMode(true);
					} else {
						mon.runningInMovieMode(false);
					}
					s = cameraMode.substring(cameraMode.indexOf("(") + 1, cameraMode.length() - 1);
					if (s.equals("Auto")) {
						mon.setCameraControl("Auto");
					} else {
						mon.setCameraControl("Manual");
					}
				}
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
