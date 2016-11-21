package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

public class InputThread extends Thread {
	private Monitor mon;
	private Socket sock;
	private boolean expected;
	private int orderNbr;
	
	public InputThread(Monitor mon, Socket sock, int orderNbr) {
		this.mon = mon;
		this.sock = sock;
		expected = false;
		this.orderNbr = orderNbr;
	}

	public void run() {

		// vänta på att ta emot bild
		try {
			InputStream is = sock.getInputStream();
			
			// Read the request
			String request = getLine(is);

			// The request is followed by some additional header lines,
			// followed by a blank line.
			String header;
			boolean cont; 
			do {
				//TODO do something with the header
				header = getLine(is);
				cont = !(header.equals(""));
			} while (cont);

			System.out.println("HTTP request '" + request
					+ "' received.");

			Byte[] image = getImage(is);
			// Interpret the request. Complain about everything but RECEIVE.
			// Ignore the file name.
			if (request.substring(0,8).equals("RECEIVE ")) {
				// skicka bild till monitor
				mon.addImage(image, orderNbr);				
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
