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
			Byte[] image = getImage(is);
			// skicka bild till monitor
			mon.addImage(image, orderNbr);
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
}
