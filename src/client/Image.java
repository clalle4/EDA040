package client;

import java.nio.ByteBuffer;

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
	public long getTime(){
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.put(time);
		buffer.flip();// need flip
		long imageTime = buffer.getLong();
		return imageTime;
	}
}
