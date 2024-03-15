package gt1;

import java.io.IOException;

import lenz.htw.blocks.Server;

/**
 * 
 * Diese Klasse startet den Server in einem extra Thread
 *
 */
public class StartServerThread extends Thread {
	String name;
	int winner;

	StartServerThread(String s) {
		this.name = s;
	}

    @Override
    public void run() {
		winner = Server.runOnceAndReturnTheWinner(8);
	}
	
	public int getValue() {
		return winner;
	}
}
