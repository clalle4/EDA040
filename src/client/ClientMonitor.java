package client;

import java.util.LinkedList;

public class ClientMonitor {
	private LinkedList<byte[]> cam1Images;
	private LinkedList<byte[]> cam2Images;
	private boolean movieMode;
	private String viewMode;
	public static final int AUTO = 0;
	public static final int IDLE = 1;
	public static final int MOVIE = 2;

	public ClientMonitor() {
		cam1Images = new LinkedList<byte[]>();
		cam2Images = new LinkedList<byte[]>();
		movieMode = false;
	}

	public synchronized void addImage(byte[] image, int orderNbr) {
		System.out.println(cam1Images.size() +"                    "+ cam2Images.size());
		if (orderNbr == 1) {
			cam1Images.add(image);
		} else if (orderNbr == 2) {
			cam2Images.add(image);
		}
		notifyAll();
	}

	public synchronized void setMovieMode(boolean mode) {
		movieMode = mode;
		notifyAll();
	}

	public synchronized boolean movieMode() {
		return movieMode;
	}

	public synchronized void setViewMode(String mode) {
		viewMode = mode;
		notifyAll();
	}

	public synchronized String getViewMode() {
		return viewMode;
	}

	public synchronized LinkedList<byte[]> getImages() {
		while (cam1Images.isEmpty() || cam2Images.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LinkedList<byte[]> images = new LinkedList<byte[]>();
		images.add(cam1Images.remove(0));
		images.add(cam2Images.remove(0));
		return images;
	}
}
