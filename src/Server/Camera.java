package Server;

import java.io.IOException;
import java.net.ServerSocket;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class Camera extends Thread{
	private AxisM3006V myCamera;
	private int port;
	private Monitor monitor;
	private InputThread input;
	private ServerSocket serverSocket;
	private OutputThread output;
	
	public Camera(int port){
		this.port = port;
		myCamera = new AxisM3006V();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		monitor = new Monitor();
		input = new InputThread(monitor, serverSocket);
		input.start();
		output = new OutputThread(monitor);
		output.start();
	}
}
