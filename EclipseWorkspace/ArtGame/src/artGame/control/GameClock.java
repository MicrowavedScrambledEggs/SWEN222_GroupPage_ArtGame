package artGame.control;

import artGame.main.Game;
import artGame.ui.TestWindow;

public class GameClock extends Thread {
	public static int TICK_MS = 1000;
	private final Game game;
	
	public GameClock(Game game) {
		this.game = game;
	}
	
	public void run() {
		while(1 == 1) { // this is a strange tradition
			// Loop forever			
			try {
				game.getFloor().moveGuards();
				Thread.sleep(TICK_MS);
			} catch(InterruptedException e) {
				// should never happen
			}			
		}
	}
}
