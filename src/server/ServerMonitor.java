package server;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ServerMonitor {
	private int len;
	private byte[] jpeg;
	private byte[] time;
	private boolean motionDetected;
	private boolean gotARequest;
	private boolean runningInMovieMode;
	private String cameraControl = "";
	// By convention, these bytes are always sent between lines
	// (CR = 13 = carriage return, LF = 10 = line feed)
	// private static final byte[] CRLF = { 13, 10 };

	public ServerMonitor() {
		jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		time = new byte[AxisM3006V.TIME_ARRAY_SIZE];
	}

	/**
	 * Send a line on OutputStream 's', terminated by CRLF. The CRLF should not
	 * be included in the string str.
	 */

	synchronized public void setImage(int len, byte[] jpeg) {
		this.len = len;
		System.arraycopy(jpeg, 0, this.jpeg, 0, len);
		notifyAll();
	}
	
	synchronized public void setTime(byte[] time){
		System.arraycopy(time, 0, this.time, 0, AxisM3006V.TIME_ARRAY_SIZE);
		notifyAll();
	}

	synchronized public byte[] getImage() {
		return jpeg;
	}

	synchronized public int getImageLength() {
		return len;
	}
	
	synchronized public byte[] getTime(){
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
