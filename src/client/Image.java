package client;

import java.nio.ByteBuffer;

public class Image {
	private byte[] jpeg;
	// private byte[] time;

	private long timeStamp;

	public Image(byte[] jpeg, byte[] time) {
		this.jpeg = jpeg;
		// this.time = time;
		timeStamp = convertTime(time);
	}

	public byte[] getJPEG() {
		return jpeg;
	}

	private long convertTime(byte[] time) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.put(time);
		buffer.flip();// need flip
		long imageTime = buffer.getLong();
		return imageTime;
	}

	public long getTime() {
		return timeStamp;
	}

	// to test sync!!
	public void addTime(long time) {
		timeStamp += time;
	}
}
