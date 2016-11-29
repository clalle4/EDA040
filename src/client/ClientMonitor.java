package client;

import java.util.LinkedList;

public class ClientMonitor {
	private LinkedList<Image> cam1Images;
	private LinkedList<Image> cam2Images;
	private LinkedList<ClientOutputThread> outputThreads;
	private int cameraMode;
	private String viewMode;
	private boolean motionDetected;
	private boolean synchronous;
	public static final long synchronousDelay = 200;
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

	public synchronized boolean synchronous() {
		return synchronous;
	}

	public synchronized byte[] getCam1Image() throws InterruptedException {
		while (cam1Images.isEmpty()) {
			wait();
		}
		Image img = cam1Images.removeFirst();
		long delay = getSynchronousDelay(img.getTime());
		wait(delay);
		return img.getJPEG();
	}

	public synchronized byte[] getCam2Image() throws InterruptedException {
		while (cam2Images.isEmpty()) {
			wait();
		}
		Image img = cam2Images.removeFirst();
		long delay = getSynchronousDelay(img.getTime());
		wait(delay);
		return img.getJPEG();
	}

	private long getSynchronousDelay(long imageTime) {
		long delay = 0;
		long currentTime = System.currentTimeMillis();
		
		long timePassed = currentTime - imageTime;
		if (timePassed < synchronousDelay && !viewMode.equals("Asynchronous")) {
			delay = synchronousDelay - timePassed;
			synchronous = true;
			System.out.println(
					"Running in Synchronous mode. Viewing image has been delayed by: " + delay + " milliseconds");
		} else if (timePassed > synchronousDelay && !viewMode.equals("Synchronous")) {
			synchronous = false;
			System.out.println("Running in Asynchronous mode.");
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
