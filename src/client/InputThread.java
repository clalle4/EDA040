package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class InputThread extends Thread {
	private Monitor mon;
	private Socket sock;
	private boolean expected;
	
	public InputThread(Monitor mon, Socket sock){
		this.mon = mon;
		this.sock = sock;
		expected = false;
	}
	
	public void run() {
		
		// vänta på att ta emot bild
		try {
			InputStream is = sock.getInputStream();
			Byte[] image = getImage(is);
			// skicka bild till monitor
			mon.addImage(image);
			if(!expected){
				mon.changeMode();
			}
			expected = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	

	}
	public void setExpected(){
		expected = true;
	}
	private Byte[] getImage(InputStream is) {
		
		return null;
	}

}
