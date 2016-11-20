package client;

import java.util.LinkedList;

public class Monitor {
	private LinkedList<Byte[]> cam1Images;
	private LinkedList<Byte[]> cam2Images;
	private boolean movieMode;

	public Monitor() {
		cam1Images = new LinkedList<Byte[]>();
		cam2Images = new LinkedList<Byte[]>();
		movieMode = false;
	}

	public void addImage(Byte[] image, int orderNbr) {
		if(orderNbr == 1){
			cam1Images.add(image);			
		} else if (orderNbr == 2){
			cam2Images.add(image);						
		}
		if(image[0] == 1){
			changeMode(true);
		}
	}

	public synchronized void changeMode(boolean mode) {
		movieMode = mode;
	}
	
	public synchronized boolean movieMode() {
		return movieMode;
	}
}
