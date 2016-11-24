package server;


import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ServerMonitor {
	private int len; 
	private byte[] jpeg;
	private boolean motionDetected;
	private boolean gotARequest;
	// By convention, these bytes are always sent between lines
	// (CR = 13 = carriage return, LF = 10 = line feed)
//	private static final byte[] CRLF = { 13, 10 };


	public ServerMonitor(){
		jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
	}

	/**
	 * Send a line on OutputStream 's', terminated by CRLF. The CRLF should not
	 * be included in the string str.
	 */

	synchronized public void setImage(int len, byte[] jpeg){
		this.len = len;
		System.arraycopy(jpeg, 0, this.jpeg, 0, len);
		notifyAll();
	}
	
	synchronized public byte[] getImage(){
		return jpeg;
	}
	
	synchronized public int getImageLength(){
		return len;
	}
	
	synchronized public void gotARequest(boolean gotARequest){
		this.gotARequest = gotARequest;
		notifyAll();
	}

	synchronized public boolean checkIfGotARequest(){
		return gotARequest;
	}


	synchronized public void motionDetected(boolean motionDetected) {
		this.motionDetected = motionDetected;
		notifyAll();	
	}
	
	synchronized public boolean motionHasBeenDetected(){
		return motionDetected;
	}
}
