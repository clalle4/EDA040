package server;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

/**
 * The monitor to handle synchronization between the server threads (Camera,
 * ServerInputThread and serverOutputThread)
 */
public class ServerMonitor {
	private int len;
	private byte[] jpeg;
	private byte[] time;
	private boolean motionDetected;
	private boolean gotARequest;
	private boolean runningInMovieMode;
	private String cameraControl = "";

	public ServerMonitor() {
		jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		time = new byte[AxisM3006V.TIME_ARRAY_SIZE];
	}

	/**
	 * Sets the image and the length
	 * 
	 * @param len
	 *            Length of the image
	 * @param jpeg
	 *            The image
	 */
	synchronized public void setImage(int len, byte[] jpeg) {
		this.len = len;
		System.arraycopy(jpeg, 0, this.jpeg, 0, len);
		notifyAll();
	}

	/**
	 * Sets the time of the image
	 * 
	 * @param time
	 *            Time stamp of the image
	 */
	synchronized public void setTime(byte[] time) {
		System.arraycopy(time, 0, this.time, 0, AxisM3006V.TIME_ARRAY_SIZE);
		notifyAll();
	}

	synchronized public byte[] getImage() {
		return jpeg;
	}

	synchronized public int getImageLength() {
		return len;
	}

	synchronized public byte[] getTime() {
		return time;
	}

	synchronized public void gotARequest(boolean gotARequest) {
		this.gotARequest = gotARequest;
		notifyAll();
	}

	synchronized public boolean checkIfGotARequest() {
		return gotARequest;
	}

	synchronized public void motionDetected(boolean motionDetected) {
		this.motionDetected = motionDetected;
		notifyAll();
	}

	synchronized public boolean motionDetected() {
		return motionDetected;
	}

	synchronized public void runningInMovieMode(boolean movieMode) {
		this.runningInMovieMode = movieMode;
		notifyAll();
	}

	synchronized public boolean runningInMovieMode() {
		return runningInMovieMode;
	}

	synchronized public void setCameraControl(String cameraControl) {
		this.cameraControl = cameraControl;
		notifyAll();
	}

	/**
	 * If the camera control is "Auto" waits as long we don't have a request and
	 * motion has not been detected from the camera or as long we don't have a
	 * request and the client is already running in movie mode. Otherwise waits
	 * until there is a request
	 */
	synchronized public void waitForRequest() {
		try {
			if (cameraControl.equals("Auto")) {
				while ((!gotARequest && runningInMovieMode) || (!gotARequest && !motionDetected)) {
					wait();
				}
			} else {
				while (!gotARequest) {
					wait();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
