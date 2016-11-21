package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class InputThread extends Thread {
	private Monitor mon;
	private Socket sock;
	private boolean expected;
	private int orderNbr;
	private byte [] jpeg;
	
	public InputThread(Monitor mon, Socket sock, int orderNbr) {
		this.mon = mon;
		this.sock = sock;
		expected = false;
		this.orderNbr = orderNbr;
		jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
	}

	public void run() {

		// vänta på att ta emot bild
		try {
			InputStream is = sock.getInputStream();
			
			// Read the response
			String responseLine = getLine(is);

			// The request is followed by some additional header lines,
			// followed by a blank line.
			String header;
			boolean cont; 
			do {
				//TODO do something with the header
				header = getLine(is);
				System.out.println(header); //TODO delete me
				cont = !(header.equals(""));
			} while (cont);

			System.out.println("HTTP request '" + responseLine
					+ "' received.");

			// Now load the JPEG image.
			int bufferSize = jpeg.length;
			int bytesRead  = 0;
			int bytesLeft  = bufferSize;
			int status;

			// We have to keep reading until -1 (meaning "end of file") is
			// returned. The socket (which the stream is connected to)
			// does not wait until all data is available; instead it
			// returns if nothing arrived for some (short) time.
			do {
				status = is.read(jpeg, bytesRead, bytesLeft);
				// The 'status' variable now holds the no. of bytes read,
				// or -1 if no more data is available
				if (status > 0) {
					bytesRead += status;
					bytesLeft -= status;
				}
			} while (status >= 0);
			System.out.println("Received image data ("
					+ bytesRead + " bytes).");
			// Interpret the request. Complain about everything but RECEIVE.
			// Ignore the file name.
			if (responseLine.substring(0,8).equals("RECEIVE ")) {
				// skicka bild till monitor
				mon.addImage(jpeg, orderNbr);				
			}
			
			if (!expected) {
				mon.changeMode(true);
			}
			expected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setExpected() {
		expected = true;
	}

	private Byte[] getImage(InputStream is) {
		ArrayList<Byte> list = new ArrayList<Byte>();
		try {
			byte temp = (byte) is.read();
			if (temp > 0) {
				list.add(temp);
			} else {
				return (Byte[]) list.toArray();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Read a line from InputStream 's', terminated by CRLF. The CRLF is
	 * not included in the returned string.
	 */
	private static String getLine(InputStream s)
			throws IOException {
		boolean done = false;
		String result = "";

		while(!done) {
			int ch = s.read();        // Read
			if (ch <= 0 || ch == 10) {
				// Something < 0 means end of data (closed socket)
				// ASCII 10 (line feed) means end of line
				done = true;
			}
			else if (ch >= ' ') {
				result += (char)ch;
			}
		}

		return result;
	}
}
