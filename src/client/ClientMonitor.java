package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class ClientMonitor {
	private LinkedList<Image> cam1Images;
	private LinkedList<Image> cam2Images;
	private LinkedList<ClientOutputThread> outputThreads;
	private int cameraMode;
	private String viewMode;
	private boolean motionDetected;
	private boolean synchronous;
	private long receiveCam1Time;
	private long receiveCam2Time;
	private String triggeringCamera;

	// ta bort??
	public static final long synchronousDelay = 200;

	public static final int AUTO = 0;
	public static final int IDLE = 1;
	public static final int MOVIE = 2;

	public static final int SYNC_AUTO = 0;
	public static final int SYNC = 1;
	public static final int ASYNC = 2;

	private int syncMode = 0;

	private int delay = 500;
	private int tresHold = 200;

	private long latestTimeStampCam1 = 0;
	private long latestTimeStampCam2 = 0;

	public ClientMonitor() {
		cam1Images = new LinkedList<Image>();
		cam2Images = new LinkedList<Image>();
		outputThreads = new LinkedList<ClientOutputThread>();
		cameraMode = ClientMonitor.AUTO;
		motionDetected = false;
		viewMode = "Auto";
		receiveCam1Time = 0;
		receiveCam2Time = Long.MAX_VALUE;
	}

	public synchronized void addImage(Image image, int orderNbr) {
		System.out.println(cam1Images.size() + "                    " + cam2Images.size());
		if (orderNbr == 1) {
			receiveCam1Time = System.currentTimeMillis();
			cam1Images.add(image);
		} else if (orderNbr == 2) {
			receiveCam2Time = System.currentTimeMillis();
			cam2Images.add(image);
		}
		notifyAll();
	}

	public synchronized void setCameraMode(int mode) {
		cameraMode = mode;
		if (cameraMode == MOVIE) {
			setMotionDetected(true, "Manual");
		} else {
			setMotionDetected(false, "Manual");
		}
		notifyAll();
	}

	public synchronized int getCameraMode() {
		return cameraMode;
	}

	public synchronized boolean motionDetected() {
		return motionDetected;
	}

	public synchronized void setMotionDetected(boolean motionDetected, String triggeringCamera) {
		if (!this.motionDetected && motionDetected) {
			this.triggeringCamera = triggeringCamera;
		}
		this.motionDetected = motionDetected;
		notifyAllOutputThreads();
		notifyAll();
	}

	public synchronized void setViewMode(String mode) {
		viewMode = mode;
		if (mode.equals("Auto"))
			syncMode = SYNC_AUTO;
		if (mode.equals("Synchronous"))
			syncMode = SYNC;
		if (mode.equals("Asynchronous"))
			syncMode = ASYNC;
		notifyAll();
	}

	public synchronized String getViewMode() {
		return viewMode;
	}

	public synchronized boolean synchronous() {
		return synchronous;
	}

	public synchronized String getTriggeringCamera() {
		return triggeringCamera;
	}

	// // OBS TEST METOD!!
	// public synchronized Image[] getImages() throws InterruptedException {
	// while (cam1Images.isEmpty() && cam2Images.isEmpty()) {
	// wait();
	// }
	//
	// // setSynchronisedMode(1);
	//
	// Image cam1Img = (!cam1Images.isEmpty()) ? cam1Images.removeFirst() :
	// null;
	// Image cam2Img = (!cam2Images.isEmpty()) ? cam2Images.removeFirst() :
	// null;
	//
	// Image[] images = new Image[] { cam1Img, cam2Img };
	//
	// return images;
	// }

	public synchronized byte[] getCam1Image() throws InterruptedException {
		while (cam1Images.isEmpty()) {
			wait();
		}

		Image img = cam1Images.removeFirst();
		latestTimeStampCam1 = img.getTime();

		// måste komma efter ovanstående rader!!
		setSynchronisedMode(1);

		waitIfSynchronous(img);

		return img.getJPEG();
	}

	public synchronized byte[] getCam2Image() throws InterruptedException {
		while (cam2Images.isEmpty()) {
			wait();
		}

		Image img = cam2Images.removeFirst();

		// DELAY FÖR ATT TESTA SYNCHRONIZED
		img.addTime(1000);

		latestTimeStampCam2 = img.getTime();

		// måste komma efter ovanstående rader!!
		setSynchronisedMode(2);

		waitIfSynchronous(img);

		return img.getJPEG();
	}

	private void waitIfSynchronous(Image img) throws InterruptedException {
		if (synchronous) {
			long timeStamp = img.getTime();
			long timeToSend = timeStamp + delay;
			wait(timeToSend - System.currentTimeMillis());
			notifyAll();
		}

	}

	// fix this method!!
	private void setSynchronisedMode(int camera) {
		long currentTimeStamp = -1;
		long prevTimeStamp = -1;
		long nextTimeStamp = -1;

		switch (camera) {
		case 1:
			currentTimeStamp = latestTimeStampCam1;
			prevTimeStamp = latestTimeStampCam2;
			if (!cam2Images.isEmpty())
				nextTimeStamp = cam2Images.getFirst().getTime();
			break;

		case 2:
			currentTimeStamp = latestTimeStampCam2;
			prevTimeStamp = latestTimeStampCam1;
			if (!cam1Images.isEmpty())
				nextTimeStamp = cam1Images.getFirst().getTime();
			break;
		default:
			System.out.println("Must choose between camera 1 or 2");
		}

		switch (syncMode) {
		case SYNC_AUTO:
			if ((Math.abs(currentTimeStamp - prevTimeStamp) < tresHold)
					|| (nextTimeStamp != -1 && (Math.abs(currentTimeStamp - nextTimeStamp) < tresHold))) {
				synchronous = true;
			} else {
				synchronous = false;
			}
			break;
		case SYNC:
			synchronous = true;
			break;
		case ASYNC:
			synchronous = false;
			break;
		default:
			break;
		}
		notifyAll();
	}

	// // remove this method??
	// private long getSynchronousDelay(long imageTime) {
	// long delay = 0;
	// long currentTime = System.currentTimeMillis();
	// long timePassed = currentTime - imageTime;
	// if (synchronous) {
	// delay = synchronousDelay - timePassed;
	// System.out.println(
	// "Running in Synchronous mode. Viewing image has been delayed by: " +
	// delay + " milliseconds");
	// } else {
	// System.out.println("Running in Asynchronous mode.");
	// }
	// return delay;
	// }
	//
	// // remove this method??
	// private void checkSynchronous() {
	// long timeDiff = Math.abs(receiveCam1Time - receiveCam2Time);
	// if (timeDiff <= synchronousDelay && !viewMode.equals("Asynchronous")) {
	// synchronous = true;
	// } else if (timeDiff > synchronousDelay &&
	// !viewMode.equals("Synchronous")) {
	// synchronous = false;
	// }
	// notifyAll();
	// }

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
