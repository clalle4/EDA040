package server;

import java.io.IOException;
import java.net.ServerSocket;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class Camera extends Thread {
	private AxisM3006V myCamera;
	private ServerMonitor serverMonitor;
	private ServerInputThread input;
	private ServerSocket serverSocket;
	private ServerOutputThread output;
	private byte[] jpeg;

	public Camera(int port) {
		jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		myCamera = new AxisM3006V();
		myCamera.init();
		myCamera.setProxy("argus-1.student.lth.se", port);
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverMonitor = new ServerMonitor();
		output = new ServerOutputThread(serverMonitor);
		output.start();
		input = new ServerInputThread(serverMonitor, serverSocket, output);
		input.start();
	}

	public void run() {
		while(true){
		if (!myCamera.connect()) {
			System.out.println("Failed to connect to camera!");
			System.exit(1);
		}
		serverMonitor.motionDetected(myCamera.motionDetected());
		int len = myCamera.getJPEG(jpeg, 0);
		serverMonitor.setImage(len, jpeg);
		}
	}
}
