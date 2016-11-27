package client;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class ClientMonitor {
	private LinkedList<Image> cam1Images;
	private LinkedList<Image> cam2Images;
	private LinkedList<ClientOutputThread> outputThreads;
	private int cameraMode;
	private String viewMode;
	private boolean motionDetected;
	public static final long synchronousDelay = 500;
	public static final int AUTO = 0;
	public static final int IDLE = 1;
	public static final int MOVIE = 2;

	public ClientMonitor() {
		cam1Images = new LinkedList<Image>();
		cam2Images = new LinkedList<Image>();
		outputThreads = new LinkedList<ClientOutputThread>();
		cameraMode = ClientMonitor.AUTO;
		motionDetected = false;
		viewMode = "Auto";
	}

	public synchronized void addImage(Image image, int orderNbr) {
		System.out.println(cam1Images.size() + "                    " + cam2Images.size());
		if (orderNbr == 1) {
			cam1Images.add(image);
		} else if (orderNbr == 2) {
			cam2Images.add(image);
		}
		notifyAll();
	}

	public synchronized void setCameraMode(int mode) {
		cameraMode = mode;
		if (cameraMode == MOVIE) {
			setMotionDetected(true);
		} else {
			setMotionDetected(false);
		}
		notifyAll();
	}

	public synchronized int getCameraMode() {
		return cameraMode;
	}

	public synchronized boolean motionDetected() {
		return motionDetected;
	}

	public synchronized void setMotionDetected(boolean motionDetected) {
		this.motionDetected = motionDetected;
		notifyAllOutputThreads();
		notifyAll();
	}

	public synchronized void setViewMode(String mode) {
		viewMode = mode;
		notifyAll();
	}

	public synchronized String getViewMode() {
		return viewMode;
	}

	public synchronized byte[] getCam1Image() throws InterruptedException {
		while (cam1Images.isEmpty()) {
			wait();
		}
		Image img = cam1Images.removeFirst();
		if (getViewMode().equals("Synchronous")) {
			long delay = getSynchronousDelay(img.getTime());
			wait(delay);
		}
		return img.getJPEG();
	}

	public synchronized byte[] getCam2Image() throws InterruptedException {
		while (cam2Images.isEmpty()) {
			wait();
		}
		Image img = cam2Images.removeFirst();
		if (getViewMode().equals("Synchronous")) {
			long delay = getSynchronousDelay(img.getTime());
			wait(delay);
		}
		return img.getJPEG();
	}

	private long getSynchronousDelay(byte[] time) {
		long delay = 0;
		long currentTime = System.currentTimeMillis();
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.put(time);
	    buffer.flip();//need flip 
	    long imageTime = buffer.getLong();
	    long timePassed = currentTime - imageTime;
	    if(timePassed < synchronousDelay){
	    	delay = synchronousDelay - timePassed;
	    	System.out.println("Running in Synchronous mode. Viewing image has been delayed by: " + delay + " milliseconds");
	    }
		return delay;
	}

	public synchronized void addOutputThread(ClientOutputThread clientOutputThread) {
		outputThreads.add(clientOutputThread);
	}

	private void notifyAllOutputThreads() {
		for (ClientOutputThread outputThread : outputThreads) {
			synchronized (outputThread) {
				outputThread.notifyAll();
			}
		}
	}
}
