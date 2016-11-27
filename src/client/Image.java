package client;

public class Image {
	private byte[] jpeg;
	private byte[] time;

	public Image(byte[] jpeg, byte[] time){
		this.jpeg = jpeg;
		this.time = time;
	}
	public byte[] getJPEG(){
		return jpeg;
	}
	public byte[] getTime(){
		return time;
	}
}
