package main;

import java.io.IOException;

import client.ClientMain;
import server.ServerMain;

public class MainAll {

	public static void main(String[] args) {
		try {
			ServerMain.main(null);
			ClientMain.main(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
