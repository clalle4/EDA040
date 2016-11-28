package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ClientInputThread extends Thread {
	private ClientMonitor mon;
	private Socket sock;
	private int orderNbr;
	private byte[] jpeg;
	private byte[] time;
	private int imageLength;
	private InputStream is;

	public ClientInputThread(ClientMonitor mon, Socket sock, int orderNbr) {
		this.mon = mon;
		this.sock = sock;
		this.orderNbr = orderNbr;
		jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		time = new byte[AxisM3006V.TIME_ARRAY_SIZE];
	}

	public void run() {

		// vänta på att ta emot bild
		while (true) {
			try {
				is = sock.getInputStream();
				// Read the header
				String responseLine = getLine(is);
				System.out.println("Server response " + responseLine + " received.");
				if(responseLine.equals("Switch to Movie mode!") && mon.getCameraMode() == ClientMonitor.AUTO){
					mon.setMotionDetected(true);
					
				} else if (responseLine.substring(0, 8).equals("RECEIVE ")) {
					imageLength = Integer.parseInt(getLine(is));
					getLine(is); // state
					//get the time
					int read = 0;
					while (read < AxisM3006V.TIME_ARRAY_SIZE) {
						int n = is.read(time, read, AxisM3006V.TIME_ARRAY_SIZE - read); // Blocking
						if (n == -1)
							throw new IOException();
						read += n;
					} // end of header
					//get the image
					jpeg = new byte[imageLength + 1];
					read = 0;
					while (read < imageLength) {
						int n = is.read(jpeg, read, imageLength - read); // Blocking
						if (n == -1)
							throw new IOException();
						read += n;
					}
					System.out.println("Received image data (" + read + " bytes).");
					// skicka bild till monitor
					Image image = new Image(jpeg, time);
					mon.addImage(image, orderNbr);
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
