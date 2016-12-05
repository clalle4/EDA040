package server;

import java.io.IOException;
import java.net.ServerSocket;
import se.lth.cs.eda040.fakecamera.AxisM3006V;

/**
 * Starts up the server which will be responsible for taking pictures from the AxisM3006V camera and send them to the client
 */
public class Camera extends Thread {
	private AxisM3006V myCamera;
	private ServerMonitor serverMonitor;
	private ServerInputThread input;
	private ServerSocket serverSocket;
	private ServerOutputThread output;
	private byte[] jpeg;
	private byte[] time;

	/**
	 * Creates an instant of AxisM3006V camera and initiates it, creates a
	 * serverSocket to handle the communication with the client, creates a
	 * monitor for this camera and creates two threads to handle the incoming
	 * and outgoing communication with the client and starts them
	 * 
	 * @param server
	 *            The server for the camera
	 * @param port
	 *            The port in which the camera and socket will operate
	 */
	public Camera(String server, int port) {
		jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		time = new byte[AxisM3006V.TIME_ARRAY_SIZE];
		myCamera = new AxisM3006V();
		myCamera.init();
		myCamera.setProxy(server, port);
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

	/**
	 * Starts the camera thread. Updates the serverMonitor if motion have been
	 * detected or not then takes a picture and sends it to the serverMonitor
	 * along with the length and time stamp of it.
	 */
	public void run() {
		while (true) {
			if (!myCamera.connect()) {
				System.out.println("Failed to connect to camera!");
				System.exit(1);
			}
			serverMonitor.motionDetected(myCamera.motionDetected());
			int len = myCamera.getJPEG(jpeg, 0);
			serverMonitor.setImage(len, jpeg);
			myCamera.getTime(time, 0);
			serverMonitor.setTime(time);
		}
	}
}
