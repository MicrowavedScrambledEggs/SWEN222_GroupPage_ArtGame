package artGame.control;

import artGame.main.Game;
import artGame.ui.TestWindow;

public class GameClock extends Thread {
	private final int wait;
	private final Game game;
	// TODO Vicki: Need a field here for whatever updates the display, client-side.
	
	public GameClock(int wait, Game game, TestWindow window) { // TODO Again, TestWindow should be whatever has to update. 
		this.wait = wait;
		this.game = game;
		// TODO ditto!
	}
	
	public void run() {
		while(1 == 1) { // this is a strange tradition of not using 'true'. 
			// Loop forever			
			try {
				Thread.sleep(wait);
				// game do something
			} catch(InterruptedException e) {
				// should never happen
			}			
		}
	}
}
