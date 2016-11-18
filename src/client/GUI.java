package client;

public class GUI {
	private Monitor mon;

	public GUI(Monitor mon) {
		this.mon = mon;
	}

	public void changeMode() {
		mon.changeMode();
	}

}
