package server;

public class ServerMain {

	public static void main(String[] args){
		Camera camera1 = new Camera(8011);
		camera1.start();
		Camera camera2 = new Camera(8012);
		camera2.start();
	}
}
