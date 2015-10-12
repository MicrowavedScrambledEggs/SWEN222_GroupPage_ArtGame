package artGame.control;

import artGame.main.Game;
import artGame.main.Main;
import artGame.ui.TestWindow;

public class GameClock extends Thread {
	public static int TICK_MS = 1000;
	private Game game;

	public GameClock(Game game) {
		this.game = game;
	}

	public void run() {
		while (1 == 1) { // this is a strange tradition
			// Loop forever
			try {
				game = Main.getGame();
				long then = System.currentTimeMillis();
				if (game != null) {
					game.getFloor().moveGuards();
				}

				long now = System.currentTimeMillis();
				long sleep = (then + TICK_MS) - now;
				if (sleep > 0) {
					Thread.sleep(sleep);
				}
			} catch (InterruptedException e) {
				// should never happen
			}
		}
	}
}
