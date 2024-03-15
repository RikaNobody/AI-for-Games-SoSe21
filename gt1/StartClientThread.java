package gt1;

import java.io.IOException;

/**
 * 
 * Die Klasse wird für die Evolution benötigt durch diese werden die Clients als
 * eigene Threads gestartet.
 *
 */
public class StartClientThread extends Thread {

	String name;

	StartClientThread(String s) {
		this.name = s;
	}

	public void run() {
		System.out.println("Start " + name);
		String[] args = null;
		try {
			Client.main(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
