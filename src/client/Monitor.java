package client;

import java.util.ArrayList;

public class Monitor {
	private ArrayList<Byte[]> images;
	private ArrayList<String> adresses;
	private boolean movieMode;

	public Monitor() {
		images = new ArrayList<Byte[]>();
		adresses = new ArrayList<String>();
		movieMode = false;
	}

	public void addImage(Byte[] image) {
		images.add(image);

		boolean motionDetected = image[0] == 1;
		// byte 1 - 8: timestamp

		for (int i = 1; i <= 8; i++) {
			String temp = image[i].toString();
		}
		// byte 9 - 12: imagesize in bytes

	}

	public void changeMode() {
		movieMode = !movieMode;
	}
	
	public boolean movieMode() {
		return movieMode;
	}
}
