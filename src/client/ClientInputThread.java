package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ClientInputThread extends Thread {
	private ClientMonitor mon;
	private Socket sock;
	private boolean expected;
	private int orderNbr;
	private byte[] jpeg;
	private int imageLength;
	private InputStream is;
	private boolean motionDetected;
	
	public ClientInputThread(ClientMonitor mon, Socket sock, int orderNbr) {
		this.mon = mon;
		this.sock = sock;
		expected = false;
		this.orderNbr = orderNbr;
		jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		motionDetected = false;
	}

	public void run() {
		
		// vänta på att ta emot bild
		while (true) {
			try {
					is = sock.getInputStream();
				
				// Read the header
				String responseLine = getLine(is);
				imageLength = Integer.parseInt(getLine(is));
				String state = getLine(is); //end of header
				if(state.equals("Motion has been detected")){
					motionDetected = true;
				}

				System.out.println("HTTP request " + responseLine + " received.");

				int read = 0;
				while (read < imageLength) {
					int n = is.read(jpeg, read, imageLength-read); // Blocking
					if (n == -1) throw new IOException();
					read += n;
					};
				System.out.println("Received image data (" + read + " bytes).");

				if (responseLine.substring(0, 8).equals("RECEIVE ")) {
					// skicka bild till monitor
					mon.addImage(jpeg, orderNbr);
				}

				if (!expected || motionDetected) {
					mon.setMovieMode(true);
				}
				expected = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setExpected() {
		expected = true;
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
