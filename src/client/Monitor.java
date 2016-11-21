package client;

import java.util.LinkedList;

public class Monitor {
	private LinkedList<byte[]> cam1Images;
	private LinkedList<byte[]> cam2Images;
	private boolean movieMode;
	private String viewMode;

	public Monitor() {
		cam1Images = new LinkedList<byte[]>();
		cam2Images = new LinkedList<byte[]>();
		movieMode = false;
	}

	public synchronized void addImage(byte[] image, int orderNbr) {
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
	
	public synchronized void setViewMode(String mode){
		viewMode = mode;
	}
	
	public synchronized String getViewMode(){
		return viewMode;
	}
	
	public synchronized LinkedList<byte[]> getImages(){
		LinkedList<byte[]> images = new LinkedList<byte[]>();
		try{
		images.add(cam1Images.remove(0));
		images.add(cam2Images.remove(0));
		}catch(NullPointerException e){
			System.out.println("Missing image from one or both cameras");
		}
		return images;
	}
}
