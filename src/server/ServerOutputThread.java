package server;

import java.io.IOException;
import java.io.OutputStream;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ServerOutputThread extends Thread {
	private ServerMonitor mon;
	private OutputStream os;
	private boolean motionDetected;
	private byte[] jpeg;
	private boolean sendSwitchResponse;
	// By convention, these bytes are always sent between lines
	// (CR = 13 = carriage return, LF = 10 = line feed)
	private static final byte[] CRLF = { 13, 10 };

	public ServerOutputThread(ServerMonitor serverMonitor) {
		mon = serverMonitor;
		jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		motionDetected = false;
	}


	public void run() {
		while (true) {
			
			motionDetected = mon.motionDetected();
			boolean gotARequest = mon.checkIfGotARequest();
			mon.waitForRequest();
			if (sendSwitchResponse && motionDetected) {
				try {
					putLine(os, "Switch to Movie mode!");
				} catch (IOException e) {
					System.out.println("Failed to send SWITCH response");
				}
				sendSwitchResponse = false;
			} else if (gotARequest) {
				jpeg = mon.getImage();
				int len = mon.getImageLength();
				try {
					putLine(os, "RECEIVE image");
					putLine(os, len + "");
					String s;
					if (motionDetected) {
						s = "Motion has been detected";
					} else {
						s = "Motion has not been detected";
					}
					putLine(os, s); // end of header
					os.write(jpeg, 0, len); // send the image
				} catch (IOException e) {
					System.out.println("Failed to send RECEIVE response");
				}
				mon.gotARequest(false);
				sendSwitchResponse = true;
			}
		}
	}

	public void setOutputStream(OutputStream os) {
		this.os = os;
	}

	/**
	 * Send a line on OutputStream 's', terminated by CRLF. The CRLF should not
	 * be included in the string str.
	 */
	private static void putLine(OutputStream s, String str) throws IOException {
		s.write(str.getBytes());
		s.write(CRLF);
	}
}
