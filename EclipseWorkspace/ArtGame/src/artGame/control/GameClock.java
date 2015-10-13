package artGame.control;

import artGame.game.Player;
import artGame.main.Game;
import artGame.main.Main;

public class GameClock extends Thread {
	public static int TICK_MS = 20;
	public static int MOVE_TICK = 1000;
	private Game game;
	
	private long lastGuardMove;

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
					if(System.currentTimeMillis() - lastGuardMove >= MOVE_TICK){
						game.getFloor().moveGuards();
						game.getFloor().checkGuards();
						lastGuardMove = System.currentTimeMillis();
					}
				//	for(Player p : game.getPlayers()){
					//	if(p.isMoving()){
						//	if(System.currentTimeMillis() - p.getLastMove() >= MOVE_TICK){
						//	//	game.getFloor().moveCharacter(p);
								//p.setLastMove(System.currentTimeMillis());
						//}
						//}
					//}
					
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
