package artGame.control;

import artGame.main.Game;
import artGame.ui.TestWindow;

public class GameClock extends Thread {
	public static int TICK_MS = 1000;
	private final Game game;
	private final ServerThread[] threads;
	
	public GameClock(Game game, ServerThread[] st) {
		this.game = game;
		threads = st;
	}
	
	public void run() {
		while(1 == 1) { // this is a strange tradition
			// Loop forever			
			try {
				long then = System.currentTimeMillis();
				game.getFloor().moveGuards();
				long now = System.currentTimeMillis();
				long sleep = (then+TICK_MS) - now;
				if (sleep > 0) {
					Thread.sleep(sleep);
				}
			} catch(InterruptedException e) {
				// should never happen
			}			
		}
	}
}
