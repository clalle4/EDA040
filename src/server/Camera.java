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
	private byte[] jpeg;
	
	public Camera(int port){
		this.port = port;
		jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		myCamera = new AxisM3006V();
		myCamera.init();
		myCamera.setProxy("argus-1.student.lth.se", port);
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
	
	public void run(){
		
		if (!myCamera.connect()) {
			System.out.println("Failed to connect to camera!");
			System.exit(1);
		}
		int len = myCamera.getJPEG(jpeg, 0);
		monitor.setImage(len, jpeg);
	}
	}

