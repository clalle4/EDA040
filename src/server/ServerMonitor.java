package server;

import java.io.IOException;
import java.io.OutputStream;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ServerMonitor {
	private OutputStream output;
	private int len; 
	private byte[] jpeg;
	private boolean motionDetected;
	// By convention, these bytes are always sent between lines
	// (CR = 13 = carriage return, LF = 10 = line feed)
	private static final byte[] CRLF = { 13, 10 };


	public ServerMonitor(){
		jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
	}

	//TODO move to outputthread?
	synchronized public void handleRequest(OutputStream os) throws IOException {
		output = os; 
		putLine(output, "RECEIVE image");
		putLine(output, len+"");
		String s;
		if(motionDetected){
			s = "Motion has been detected";
		} else {
			s = "Motion has not been detected";
		}
		putLine(output, s); // end of header
		sendImage();
		notifyAll();
	}

	/**
	 * Send a line on OutputStream 's', terminated by CRLF. The CRLF should not
	 * be included in the string str.
	 */
	private static void putLine(OutputStream s, String str) throws IOException {
		s.write(str.getBytes());
		s.write(CRLF);
	}

	synchronized public void setImage(int len, byte[] jpeg){
		this.len = len;
		System.arraycopy(jpeg, 0, this.jpeg, 0, len);
		notifyAll();
	}

	synchronized private void sendImage() throws IOException{
		output.write(jpeg, 0, len);
	}

	synchronized public void motionDetected(boolean motionDetected) {
		this.motionDetected = motionDetected;
		notifyAll();	
	}
}
