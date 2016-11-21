package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class Monitor {
	private byte[] jpegPICTURE;
	private Socket clientSocket;
	private OutputStream output;
	private int len; 
	private byte[] jpeg;
	// By convention, these bytes are always sent between lines
	// (CR = 13 = carriage return, LF = 10 = line feed)
	private static final byte[] CRLF = { 13, 10 };



	synchronized public void handleRequest(OutputStream os) throws IOException {
		output = os; 
		putLine(output, "RECEIVE image");
		putLine(output, "HTTP/1.0 200 OK");
		putLine(output, "Image size: bla");
		putLine(output, "bla bla");
		putLine(output, ""); // Means 'end of header'
		sendImage();
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
		this.jpeg = jpeg;
	}

	synchronized public void sendImage() throws IOException{
		output.write(jpeg, 0, len);		
	}
}
